package org.smartly.application.desktopgap.impl.app.server.handlers;

import org.eclipse.jetty.http.*;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.application.desktopgap.impl.app.server.ServletUtils;
import org.smartly.application.desktopgap.impl.app.server.WebServer;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.PathUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

/**
 * User: angelo.geminiani
 */
public class ResourceHandler extends HandlerWrapper {

    private WebServer _server;

    private ContextHandler _context;
    private Resource _baseResource;
    private Resource _defaultStylesheet;
    private Resource _stylesheet;
    private String[] _welcomeFiles = {"index.html"};
    private MimeTypes _mimeTypes = new MimeTypes();
    private boolean _directory;
    private boolean _etags;
    private String _cacheControl;


    public ResourceHandler() {
        _mimeTypes.addMimeMapping("vcf", "text/vcard");
    }

    public void setServer(final WebServer server) {
        _server = server;

    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    /* ------------------------------------------------------------ */
    public MimeTypes getMimeTypes() {
        return _mimeTypes;
    }

    /* ------------------------------------------------------------ */
    public void setMimeTypes(MimeTypes mimeTypes) {
        _mimeTypes = mimeTypes;
    }

    /* ------------------------------------------------------------ */

    /**
     * Get the directory option.
     *
     * @return true if directories are listed.
     */
    public boolean isDirectoriesListed() {
        return _directory;
    }

    /* ------------------------------------------------------------ */

    /**
     * Set the directory.
     *
     * @param directory true if directories are listed.
     */
    public void setDirectoriesListed(boolean directory) {
        _directory = directory;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return True if ETag processing is done
     */
    public boolean isEtags() {
        return _etags;
    }

    /* ------------------------------------------------------------ */

    /**
     * @param etags True if ETag processing is done
     */
    public void setEtags(boolean etags) {
        _etags = etags;
    }

    /* ------------------------------------------------------------ */
    @Override
    public void doStart()
            throws Exception {
        ContextHandler.Context scontext = ContextHandler.getCurrentContext();
        _context = (scontext == null ? null : scontext.getContextHandler());

        super.doStart();
    }

/* ------------------------------------------------------------ */

    /**
     * @return Returns the resourceBase.
     */
    public Resource getBaseResource() {
        if (_baseResource == null)
            return null;
        return _baseResource;
    }

    /* ------------------------------------------------------------ */

    /**
     * @return Returns the base resource as a string.
     */
    public String getResourceBase() {
        if (_baseResource == null)
            return null;
        return _baseResource.toString();
    }


    /* ------------------------------------------------------------ */

    /**
     * @param base The resourceBase to set.
     */
    public void setBaseResource(Resource base) {
        _baseResource = base;
    }

    /* ------------------------------------------------------------ */

    /**
     * @param resourceBase The base resource as a string.
     */
    public void setResourceBase(String resourceBase) {
        try {
            setBaseResource(Resource.newResource(resourceBase));
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(resourceBase);
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * @return Returns the stylesheet as a Resource.
     */
    public Resource getStylesheet() {
        if (_stylesheet != null) {
            return _stylesheet;
        } else {
            if (_defaultStylesheet == null) {
                try {
                    _defaultStylesheet = Resource.newResource(this.getClass().getResource("/jetty-dir.css"));
                } catch (IOException e) {
                    this.getLogger().warning(e.toString());
                }
            }
            return _defaultStylesheet;
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * @param stylesheet The location of the stylesheet to be used as a String.
     */
    public void setStylesheet(String stylesheet) {
        try {
            _stylesheet = Resource.newResource(stylesheet);
            if (!_stylesheet.exists()) {
                this.getLogger().warning("unable to find custom stylesheet: " + stylesheet);
                _stylesheet = null;
            }
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(stylesheet.toString());
        }
    }

    /* ------------------------------------------------------------ */

    /**
     * @return the cacheControl header to set on all static content.
     */
    public String getCacheControl() {
        return _cacheControl.toString();
    }

    /* ------------------------------------------------------------ */

    /**
     * @param cacheControl the cacheControl header to set on all static content.
     */
    public void setCacheControl(String cacheControl) {
        _cacheControl = cacheControl;
    }

    /* ------------------------------------------------------------ */
    /*
     */
    public Resource getResource(String path) throws MalformedURLException {
        if (path == null || !path.startsWith("/"))
            throw new MalformedURLException(path);

        Resource base = _baseResource;
        if (base == null) {
            if (_context == null)
                return null;
            base = _context.getBaseResource();
            if (base == null)
                return null;
        }

        try {
            path = URIUtil.canonicalPath(path);
            return base.addPath(path);
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
        }

        return null;
    }

    /* ------------------------------------------------------------ */
    protected Resource getResource(HttpServletRequest request) throws MalformedURLException {
        String servletPath;
        String pathInfo;
        Boolean included = request.getAttribute(RequestDispatcher.INCLUDE_REQUEST_URI) != null;
        if (included != null && included.booleanValue()) {
            servletPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            pathInfo = (String) request.getAttribute(RequestDispatcher.INCLUDE_PATH_INFO);

            if (servletPath == null && pathInfo == null) {
                servletPath = request.getServletPath();
                pathInfo = request.getPathInfo();
            }
        } else {
            servletPath = request.getServletPath();
            pathInfo = request.getPathInfo();
        }

        String pathInContext = URIUtil.addPaths(servletPath, pathInfo);
        return getResource(pathInContext);
    }


    /* ------------------------------------------------------------ */
    public String[] getWelcomeFiles() {
        return _welcomeFiles;
    }

    /* ------------------------------------------------------------ */
    public void setWelcomeFiles(String[] welcomeFiles) {
        _welcomeFiles = welcomeFiles;
    }

    /* ------------------------------------------------------------ */
    protected Resource getWelcome(Resource directory) throws MalformedURLException, IOException {
        for (int i = 0; i < _welcomeFiles.length; i++) {
            Resource welcome = directory.addPath(_welcomeFiles[i]);
            if (welcome.exists() && !welcome.isDirectory())
                return welcome;
        }

        return null;
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.jetty.server.Handler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled())
            return;

        boolean skipContentBody = false;

        if (!HttpMethod.GET.is(request.getMethod())) {
            if (!HttpMethod.HEAD.is(request.getMethod())) {
                //try another handler
                super.handle(target, baseRequest, request, response);
                return;
            }
            skipContentBody = true;
        }

        final String resourcePath = this.getResourcePath(request);
        Resource resource = getResource(request);

        if (resource == null || !resource.exists()) {

            if (this.isCMSPath(request.getPathInfo())) {
                baseRequest.setHandled(false);
                return;
            }

            if (target.endsWith("/jetty-dir.css")) {
                resource = getStylesheet();
                if (resource == null)
                    return;
                response.setContentType("text/css");
            } else {
                //no resource - try other handlers
                super.handle(target, baseRequest, request, response);
                // ServletUtils.notFound404(response);
                return;
            }
        }

        if (resource.isDirectory()) {

            if (this.isCMSPath(resourcePath)) {
                baseRequest.setHandled(false);
                return;
            }

            if (!request.getPathInfo().endsWith(URIUtil.SLASH)) {
                if (!this.isServletPath(resourcePath.concat(URIUtil.SLASH))) {
                    response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(request.getRequestURI(), URIUtil.SLASH)));
                    baseRequest.setHandled(true);
                } else {
                    // is a servlet
                    baseRequest.setHandled(false);
                }
                return;
            }

            final Resource welcome = getWelcome(resource);
            if (welcome != null && welcome.exists()) {
                //resource = welcome;
                // does not serve "/", but send redirect
                response.sendRedirect(response.encodeRedirectURL(URIUtil.addPaths(resourcePath, welcome.getFile().getName())));
                baseRequest.setHandled(true);
                return;
            } else {
                if (!this.isServletPath(resourcePath)) {
                    this.doDirectory(request, response, resource);
                    baseRequest.setHandled(true);
                } else {
                    // is a servlet
                    baseRequest.setHandled(false);
                }
                return;
            }
        }

        final boolean is_servlet_file = this.isServletExtension(resourcePath);

        if (is_servlet_file) {
            baseRequest.setHandled(false);
            return;
        }

        // We are going to serve something
        baseRequest.setHandled(true);

        // set some headers
        String etag = null;
        if (_etags) {
            // simple handling of only a single etag
            final String ifnm = request.getHeader(HttpHeader.IF_NONE_MATCH.asString());
            etag = resource.getWeakETag();
            if (ifnm != null && resource != null && ifnm.equals(etag)) {
                response.setStatus(HttpStatus.NOT_MODIFIED_304);
                baseRequest.getResponse().getHttpFields().put(HttpHeader.ETAG, etag);
                return;
            }
        }

        final long last_modified = is_servlet_file ? DateUtils.now().getTime() : resource.lastModified();
        if (!is_servlet_file && last_modified > 0) {
            long if_modified = request.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
            if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000) {
                response.setStatus(HttpStatus.NOT_MODIFIED_304);
                return;
            }
        }


        final String mimeType = this.getMimeType(resource, request);

        // set the headers
        this.doResponseHeaders(response, resource, mimeType != null ? mimeType : null);

        response.setDateHeader(HttpHeader.LAST_MODIFIED.asString(), last_modified);

        if (_etags)
            baseRequest.getResponse().getHttpFields().put(HttpHeader.ETAG, etag);

        if (skipContentBody)
            return;

        // Send the content
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }


        // Write content normally
        resource.writeTo(out, 0, resource.length());

    }

    /* ------------------------------------------------------------ */
    protected void doDirectory(HttpServletRequest request, HttpServletResponse response, Resource resource)
            throws IOException {
        if (_directory) {
            String listing = resource.getListHTML(request.getRequestURI(), request.getPathInfo().lastIndexOf("/") > 0);
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println(listing);
        } else
            response.sendError(HttpStatus.FORBIDDEN_403);
    }

    /* ------------------------------------------------------------ */

    /**
     * Set the response headers.
     * This method is called to set the response headers such as content type and content length.
     * May be extended to add additional headers.
     *
     * @param response
     * @param resource
     * @param mimeType
     */
    protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType) {
        if (mimeType != null)
            response.setContentType(mimeType);

        long length = resource.length();

        if (response instanceof Response) {
            HttpFields fields = ((Response) response).getHttpFields();

            if (length > 0)
                fields.putLongField(HttpHeader.CONTENT_LENGTH, length);

            if (_cacheControl != null)
                fields.put(HttpHeader.CACHE_CONTROL, _cacheControl);
        } else {
            if (length > 0)
                response.setHeader(HttpHeader.CONTENT_LENGTH.asString(), Long.toString(length));

            if (_cacheControl != null)
                response.setHeader(HttpHeader.CACHE_CONTROL.asString(), _cacheControl.toString());
        }

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    protected String getResourcePath(final HttpServletRequest request) throws MalformedURLException {
        return ServletUtils.getResourcePath(request);
    }

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private String getMimeType(final Resource resource, final HttpServletRequest request) {
        String mime = _mimeTypes.getMimeByExtension(resource.toString());
        if (mime == null)
            mime = _mimeTypes.getMimeByExtension(request.getPathInfo());
        return mime;
    }

    private boolean isServletExtension(final String target) {
        if (null != _server) {
            final String ext = PathUtils.getFilenameExtension(target, true);
            return _server.getServletExtensions().contains(ext);
        }
        return false;
    }

    private boolean isServletPath(final String target) {
        if (null != _server) {
            final String path = this.stripPath(target);
            return _server.getServletPaths().contains(path);
        }
        return false;
    }

    private boolean isCMSPath(final String target) {
        return false;
    }

    private static String stripPath(String path) {
        if (path == null)
            return null;
        int semi = path.indexOf(';');
        if (semi < 0)
            return path;
        return path.substring(0, semi);
    }


}
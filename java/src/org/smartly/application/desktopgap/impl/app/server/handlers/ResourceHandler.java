package org.smartly.application.desktopgap.impl.app.server.handlers;

import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethods;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.io.WriterOutputStream;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.application.desktopgap.impl.app.server.ServletUtils;
import org.smartly.application.desktopgap.impl.app.server.WebServer;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.PathUtils;

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

    private ContextHandler _context;
    private Resource _baseResource;
    private Resource _defaultStylesheet;
    private Resource _stylesheet;
    private String[] _welcomeFiles = {"index.html"};
    private MimeTypes _mimeTypes = new MimeTypes();
    private ByteArrayBuffer _cacheControl;
    private WebServer _server;
    private boolean _aliases;
    private boolean _directory;


    public ResourceHandler() {
        _mimeTypes.addMimeMapping("vcf", "text/vcard");
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void setServer(final WebServer server) {
        _server = server;
    }

    public MimeTypes getMimeTypes() {
        return _mimeTypes;
    }

    public void setMimeTypes(final MimeTypes mimeTypes) {
        _mimeTypes = mimeTypes;
    }


    /**
     * @return True if resource aliases are allowed.
     */
    public boolean isAliases() {
        return _aliases;
    }

    /* ------------------------------------------------------------ */

    /**
     * Set if resource aliases (eg symlink, 8.3 names, case insensitivity) are allowed.
     * Allowing aliases can significantly increase security vulnerabilities.
     * If this handler is deployed inside a ContextHandler, then the
     * {@link org.eclipse.jetty.server.handler.ContextHandler#isAliases()} takes precedent.
     *
     * @param aliases True if aliases are supported.
     */
    public void setAliases(boolean aliases) {
        _aliases = aliases;
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
    @Override
    public void doStart()
            throws Exception {
        final ContextHandler.Context scontext = ContextHandler.getCurrentContext();
        _context = (scontext == null ? null : scontext.getContextHandler());

        if (_context != null)
            _aliases = _context.isAliases();

        if (!_aliases && !FileResource.getCheckAliases())
            throw new IllegalStateException("Alias checking disabled");

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
    public void setBaseResource(final Resource base) {
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
    public void setStylesheet(final String stylesheet) {
        try {
            _stylesheet = Resource.newResource(stylesheet);
            if (!_stylesheet.exists()) {
                this.getLogger().warning("unable to find custom stylesheet: " + stylesheet);
                _stylesheet = null;
            }
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(stylesheet);
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
        _cacheControl = cacheControl == null ? null : new ByteArrayBuffer(cacheControl);
    }

    /* ------------------------------------------------------------ */

    public Resource getResource(final String path) throws MalformedURLException {
        return ServletUtils.getResource(_baseResource, _context, path);
    }

    /* ------------------------------------------------------------ */

    protected String getResourcePath(final HttpServletRequest request) throws MalformedURLException {
        return ServletUtils.getResourcePath(request);
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
    protected Resource getWelcome(final Resource directory) throws IOException {
        for (int i = 0; i < _welcomeFiles.length; i++) {
            final Resource welcome = directory.addPath(_welcomeFiles[i]);
            if (welcome.exists() && !welcome.isDirectory())
                return welcome;
        }
        return null;
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.jetty.server.Handler#handle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (baseRequest.isHandled())
            return;

        boolean skipContentBody = false;

        if (!HttpMethods.GET.equals(request.getMethod()) && !HttpMethods.POST.equals(request.getMethod())) {
            if (!HttpMethods.HEAD.equals(request.getMethod())) {
                //try another handler
                super.handle(target, baseRequest, request, response);
                return;
            }
            skipContentBody = true;
        }

        final String resourcePath = this.getResourcePath(request);
        Resource resource = this.getResource(resourcePath);

        //-- is css request?--//
        if (resource == null || !resource.exists()) {

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

        //-- is Alias? --//
        if (!_aliases && resource.getAlias() != null) {
            this.getLogger().info(resource + " aliased to " + resource.getAlias());
            return;
        }

        if (resource.isDirectory()) {

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
        final long last_modified = is_servlet_file ? DateUtils.now().getTime() : resource.lastModified();
        if (!is_servlet_file && last_modified > 0) {
            long if_modified = request.getDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
            if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000) {
                response.setStatus(HttpStatus.NOT_MODIFIED_304);
                return;
            }
        }

        final String mimeType = this.getMimeType(resource, request);

        // set the headers
        this.doResponseHeaders(response, resource, mimeType != null ? mimeType : null);

        response.setDateHeader(HttpHeaders.LAST_MODIFIED, last_modified);

        if (skipContentBody)
            return;

        // Send the content
        OutputStream out = null;
        try {
            out = response.getOutputStream();
        } catch (IllegalStateException e) {
            out = new WriterOutputStream(response.getWriter());
        }

        // See if a short direct method can be used?
        if (out instanceof AbstractHttpConnection.Output) {
            ((AbstractHttpConnection.Output) out).sendContent(resource.getInputStream());
        } else {
            // Write content normally
            resource.writeTo(out, 0, resource.length());
        }
    }

    /* ------------------------------------------------------------ */
    protected void doDirectory(final HttpServletRequest request, final HttpServletResponse response, final Resource resource)
            throws IOException {
        if (_directory) {
            final String listing = resource.getListHTML(request.getRequestURI(), request.getPathInfo().lastIndexOf("/") > 0);
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
        ServletUtils.doResponseHeaders(response, _cacheControl, resource, mimeType);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private String getMimeType(final Resource resource, final HttpServletRequest request) {
        Buffer mime = _mimeTypes.getMimeByExtension(resource.toString());
        if (mime == null)
            mime = _mimeTypes.getMimeByExtension(request.getPathInfo());
        return mime.toString();
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
            final String path = stripPath(target);
            return _server.getServletPaths().contains(path);
        }
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
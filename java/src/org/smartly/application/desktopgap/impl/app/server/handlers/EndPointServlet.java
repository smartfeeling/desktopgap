package org.smartly.application.desktopgap.impl.app.server.handlers;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.util.resource.Resource;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.server.ServletUtils;
import org.smartly.application.desktopgap.impl.app.server.WebServer;
import org.smartly.application.desktopgap.impl.app.server.vtools.Cookies;
import org.smartly.application.desktopgap.impl.app.server.vtools.Req;
import org.smartly.application.desktopgap.impl.app.utils.DOM;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.*;
import org.smartly.packages.velocity.impl.VLCManager;
import org.smartly.packages.velocity.impl.vtools.toolbox.VLCToolbox;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Servlet for site file parsing.
 * <p/>
 * This servlet replace SmartlyVHTMLServlet
 */
public class EndPointServlet
        extends HttpServlet {

    public static String PATH = "/*";
    private static final String CHARSET = Smartly.getCharset();
    private static final String MIME_HTML = "text/html";
    private static final String SP_VELOCITY_CONTEXT = "velocity-context";
    private static final String SP_URL_PARAMS = "url-params";

    private static final Set<String> _extensions = new HashSet<String>(Arrays.asList(new String[]{
            ".html",
            ".vhtml"  // velocity
    }));

    private Resource _baseResource;
    private WebServer _server;

    public EndPointServlet() {
    }

    public EndPointServlet(final Object params) {
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void setServer(final WebServer server) {
        _server = server;
        _server.getServletExtensions().addAll(_extensions);
    }

    public void setBaseResource(final Resource base) {
        _baseResource = base;
    }

    public void setResourceBase(final String resourceBase) {
        try {
            this.setBaseResource(Resource.newResource(resourceBase));
        } catch (Exception e) {
            this.getLogger().warning(e.toString());
            throw new IllegalArgumentException(resourceBase);
        }
    }

    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response) throws ServletException, IOException {
        this.handle(request, response);
    }

    protected void doPost(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        this.handle(request, response);
    }

    protected void handle(final HttpServletRequest request,
                          final HttpServletResponse response) throws ServletException, IOException {
        this.handleInternal(request, response);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this);
    }

    private void handleInternal(final HttpServletRequest request,
                                final HttpServletResponse response) throws ServletException, IOException {
        final String resourcePath = ServletUtils.getResourcePath(request);
        if (isHandled(resourcePath)) {
            //-- .vhtml --//
            final Resource resource = ServletUtils.getResource(_baseResource, null, resourcePath);

            if (!resource.exists()) {
                ServletUtils.notFound404(response);
                return;
            }

            // parse resource
            final byte[] output = this.merge(resource, request, response);
            if (output.length > 0) {
                // write body
                ServletUtils.writeResponse(response, DateUtils.now().getTime(), MIME_HTML, output);
                return;
            }
        }

        ServletUtils.notFound404(response);
    }

    private byte[] merge(final Resource resource,
                         final HttpServletRequest request,
                         final HttpServletResponse response) {
        final Map<String, String> urlParams = getParams(request);
        try {
            String result = new String(ByteUtils.getBytes(resource.getInputStream()), CHARSET);

            // resolve velocity template if any
            if (isVelocity(resource.getName())) {
                final Map<String, Object> sessionContext = new HashMap<String, Object>();
                final VelocityEngine engine = getEngine();

                // execution context
                final VelocityContext context = new VelocityContext(sessionContext, this.createInnerContext(
                        resource.getName(), urlParams, request, response));

                //-- eval velocity template --//
                if (null != engine) {
                    result = VLCManager.getInstance().evaluateText(engine, resource.getName(), result, context);
                } else {
                    result = VLCManager.getInstance().evaluateText(resource.getName(), result, context);
                }
                if (StringUtils.hasText(result)) {
                    return result.getBytes();
                }
            }


            if(!urlParams.isEmpty()){
                // get appid from parameters
                // retrieve manifest and pass to wrapInFrame
                final String appId = urlParams.get(IDesktopConstants.PARAM_APPID);
                final AppManifest manifest = _server.getManifest(appId);

                // merge page with frame
                if (urlParams.containsKey(IDesktopConstants.PARAM_DESKTOPGAP)) {
                    //result = wrapInFrame(manifest, result);
                    result = addDesktopGapScripts(result);
                }
            }

            return StringUtils.hasText(result) ? result.getBytes(CHARSET) : new byte[0];
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, FormatUtils.format(
                    "ERROR MERGING TEMPLATE FOR RESOURCE '{0}': {1}",
                    resource.getName(), ExceptionUtils.getRealMessage(t)), t);
        }
        return new byte[0];
    }

    private VelocityContext createInnerContext(final String url,
                                               final Map<String, String> restParams,
                                               final HttpServletRequest request,
                                               final HttpServletResponse response) {
        final VelocityContext result = new VelocityContext(VLCToolbox.getInstance().getToolsContext());

        //-- "$req" tool --//
        result.put(Req.NAME, new Req(url, restParams, request, response));

        //-- "$cookies" tool --//
        result.put(Cookies.NAME, new Cookies(request, response));

        return result;
    }

    private Map<String, String> getParams(final HttpServletRequest request){
        final String queryString = request.getQueryString();
        if(StringUtils.hasText(queryString)){
          return CollectionUtils.stringToMapOfStrings(queryString, "&");
        }
        return new HashMap<String, String>();
    }

    private String addDesktopGapScripts(final String pageHtml) {
        try {
            return DOM.addDesktopGapScripts(pageHtml, null);
        } catch (Throwable ignored) {
        }
        return "";
    }

    private String wrapInFrame(final String resourcePath, final String pageHtml) {
        try {
            final AppManifest manifest = AppManifest.getManifest(resourcePath);
            if (null != manifest) {
                return DOM.insertInFrame(manifest, pageHtml, null);
            }

        } catch (Throwable ignored) {
        }
        return "";
    }

    private String wrapInFrame(final AppManifest manifest, final String pageHtml) {
        try {
            if (null != manifest) {
                return DOM.insertInFrame(manifest, pageHtml, null);
            }

        } catch (Throwable ignored) {
        }
        return "";
    }
    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static VelocityEngine __engine;

    private static VelocityEngine getEngine() throws Exception {
        if (null == __engine) {
            __engine = VLCManager.getInstance().getEngine().getNativeEngine();
        }
        return __engine;
    }

    private static boolean isHandled(final String path) {
        final String ext = PathUtils.getFilenameExtension(path, true);
        return _extensions.contains(ext);
    }

    private static boolean isVelocity(final String path) {
        final String ext = PathUtils.getFilenameExtension(path, true);
        return ".vhtml".equalsIgnoreCase(ext);
    }
}

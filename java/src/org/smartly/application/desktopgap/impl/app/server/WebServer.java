package org.smartly.application.desktopgap.impl.app.server;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.DesktopController;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.server.handlers.EndPointServlet;
import org.smartly.application.desktopgap.impl.app.server.handlers.ResourceHandler;
import org.smartly.application.desktopgap.impl.app.utils.URLUtils;
import org.smartly.commons.util.PathUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Internal WebServer
 */
public final class WebServer {

    private static int PORT = DesktopGap.getPort();

    private final Server _jetty;
    private final String _absoluteBaseResource;
    private final Set<String> _servletExtensions; // resource's extensions managed from servlet (i.e. vhtml)
    private final Set<String> _servletPaths;

    private boolean _initialized;
    private DesktopController _desktopController;

    private WebServer() {
        this(PORT);
    }

    private WebServer(final int port) {
        PORT = port;
        _absoluteBaseResource = Smartly.getAbsolutePath(IDesktopConstants.INSTALLED_DIR);
        _jetty = new Server();
        _servletExtensions = new HashSet<String>();
        _servletPaths = new HashSet<String>();
        _initialized = false;
    }

    public void setDesktop(final DesktopController desktopController) {
        _desktopController = desktopController;
    }

    public String getRoot() {
        return _absoluteBaseResource;
    }

    public void registerEndPoint(final String endPoint) {
        if (endPoint.startsWith("*.")) {
            final String ext = endPoint.substring(1);
            _servletExtensions.add(ext);
        } else {
            final String path = endPoint.replace("*", "");
            _servletPaths.add(path);
        }
    }

    public Set<String> getServletExtensions() {
        return _servletExtensions;
    }

    public Set<String> getServletPaths() {
        return _servletPaths;
    }

    public void start() throws Exception {
        if (null != _jetty) {
            this.init();
            _jetty.start();
        }
    }

    public void stop() throws Exception {
        if (null != _jetty) {
            _jetty.stop();
        }
    }

    public AppManifest getManifest(final String appId) {
        if (null != _desktopController) {
            return _desktopController.getApplicationManifest(appId);
        }
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (!_initialized) {

            final Connector[] connectors = getConnectors(PORT);
            final HandlerList handlers = getChainHandlers();
            final ContextHandlerCollection endpoints = getContextHandlers();

            handlers.addHandler(endpoints);

            _jetty.setConnectors(connectors);
            _jetty.setHandler(handlers);

            _initialized = true;
        }
    }

    private Connector[] getConnectors(final int port) {
        final List<Connector> result = new LinkedList<Connector>();

        // http configuration
        final HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8112);

        final ServerConnector http = new ServerConnector(_jetty, new HttpConnectionFactory(http_config));
        http.setPort(port);
        http.setIdleTimeout(30000);

        result.add(http);

        return result.toArray(new Connector[result.size()]);
    }

    private HandlerList getChainHandlers() {
        final HandlerList result = new HandlerList();

        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setServer(this);
        resourceHandler.setResourceBase(this.getRoot());
        resourceHandler.setWelcomeFiles(new String[]{"index.html", "index.htm"});
        result.addHandler(resourceHandler);

        return result;
    }

    private ContextHandlerCollection getContextHandlers() {
        final ContextHandlerCollection result = new ContextHandlerCollection();

        final ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        contextHandler.setContextPath("/");

        // Main endpoint handler
        final String servlet_endpoint = "/*";
        final EndPointServlet servlet = new EndPointServlet();
        servlet.setServer(this);
        servlet.setResourceBase(this.getRoot());
        contextHandler.addServlet(new ServletHolder(servlet), servlet_endpoint);
        this.registerEndPoint(servlet_endpoint);

        result.addHandler(contextHandler);
        return result;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


    private static WebServer __instance;

    public static WebServer getInstance() {
        if (null == __instance) {
            __instance = new WebServer();
        }
        return __instance;
    }

    public static WebServer getInstance(final boolean lookupPort) {
        if (null == __instance) {
            final int port = getAvailablePort();
            __instance = new WebServer(port);
        }
        return __instance;
    }

    public static String getHttpRoot() {
        return "http://localhost:" + PORT + "/";
    }

    public static String getHttpPath(final String path, final boolean isDesktopGap) {
        if (PathUtils.isAbsolute(path)) {
            return URLUtils.addPageParamToUrl(path, isDesktopGap);
        }
        return URLUtils.addPageParamToUrl(PathUtils.concat(getHttpRoot(), path), isDesktopGap);
    }


    public static String getHttpPath(final String path) {
        return getHttpPath(path, true);
    }

    public static int getAvailablePort() {
        int port = PORT;
        while (true) {
            try {
                final Server jetty = new Server();
                final ServerConnector http = new ServerConnector(jetty);
                http.setPort(port);
                http.setIdleTimeout(100);
                jetty.setConnectors(new Connector[]{http});
                jetty.start();
                jetty.stop();
                break;
            } catch (Throwable t) {
                port++;
            }
        }
        return port;
    }
}

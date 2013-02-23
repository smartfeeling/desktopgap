package org.smartly.application.desktopgap.impl.app.applications.window.appbridge;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.JsEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.connection.ToolConnection;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.console.ToolConsole;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.device.ToolDevice;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.i18n.ToolI18n;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.registry.ToolRegistry;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.runtime.ToolRuntime;

import java.util.HashMap;
import java.util.Map;

/**
 * Javascript-Java Bridge.
 */
public class AppBridge {

    public static final String NAME = "bridge";

    private static final String VERSION = IDesktopConstants.VERSION;
    private static final String UNDEFINED = JsEngine.UNDEFINED;

    private final AppInstance _app;
    // tools
    private final Map<String, AbstractTool> _tools;

    public AppBridge(final AppInstance app) {
        _app = app;
        // tools
        _tools = new HashMap<String, AbstractTool>();
    }

    public AppBridge(final AppBridge parent) {
        _app = parent._app;
        // tools
        _tools = new HashMap<String, AbstractTool>();
        this.register(parent._tools);
    }


    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName()).append("{");
        result.append("hash: ").append(this.hashCode());
        result.append(", ");
        result.append("version: ").append(VERSION);
        result.append("}");

        return result.toString();
    }

    public String version() {
        return VERSION;
    }

    public void register(final Map<String, ? extends AbstractTool> map) {
        _tools.putAll(map);
    }

    public void register(final String name, final AbstractTool instance) {
        if (null != name && null != instance) {
            _tools.put(name, instance);
        }
    }

    public Object get(final String name) {
        return _tools.get(name);
    }

    public void registerDefault() {
        _tools.put(ToolDevice.NAME, new ToolDevice(_app));
        _tools.put(ToolConnection.NAME, new ToolConnection(_app));
        _tools.put(ToolConsole.NAME, ToolConsole.getConsole(_app));
        _tools.put(ToolI18n.NAME, new ToolI18n(_app));
        _tools.put(ToolRuntime.NAME, new ToolRuntime(_app));
        _tools.put(ToolRegistry.NAME, new ToolRegistry(_app));
    }

    // --------------------------------------------------------------------
    //               R E G I S T R Y
    // --------------------------------------------------------------------

    public Object registry() {
        return this.get(ToolRegistry.NAME);
    }

    // --------------------------------------------------------------------
    //               R U N T I M E
    // --------------------------------------------------------------------

    public Object runtime() {
        return this.get(ToolRuntime.NAME);
    }

    // --------------------------------------------------------------------
    //               C O N S O L E
    // --------------------------------------------------------------------

    public Object console() {
        return this.get(ToolConsole.NAME);
    }

    // --------------------------------------------------------------------
    //               I 1 8 N
    // --------------------------------------------------------------------

    public Object i18n() {
        return this.get(ToolI18n.NAME);
    }


    // --------------------------------------------------------------------
    //               D E V I C E
    // --------------------------------------------------------------------

    public Object device() {
        return this.get(ToolDevice.NAME);
    }

    public Object device(final Object property) {
        if (property instanceof String) {
            final ToolDevice tool = (ToolDevice) this.device();
            return tool.get((String) property);
        }
        return UNDEFINED;
    }

    // --------------------------------------------------------------------
    //               C O N N E C T I O N
    // --------------------------------------------------------------------

    public Object connection() {
        return this.get(ToolConnection.NAME);
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected Map<String, AbstractTool> getTools() {
        return _tools;
    }
}

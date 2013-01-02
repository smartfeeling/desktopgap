package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.connection.ToolConnection;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.console.ToolConsole;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.device.ToolDevice;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.frame.ToolFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.i18n.ToolI18n;

/**
 * Javascript-Java Bridge.
 */
public class AppBridge {

    public static final String NAME = "bridge";

    private static final String VERSION = IDesktopConstants.VERSION;
    private static final String UNDEFINED = JsEngine.UNDEFINED;

    // tools
    private final ToolDevice _device;
    private final ToolConnection _connection;
    private final ToolConsole _console;
    private final ToolFrame _frame;
    private final ToolI18n _i18n;

    public AppBridge(final AppFrame frame) {
        // tools
        _frame = new ToolFrame(frame);
        _device = new ToolDevice();
        _connection = new ToolConnection();
        _console = ToolConsole.getConsole(frame.getApp());
        _i18n = new ToolI18n(frame);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName()).append("{");
        result.append("version: ").append(VERSION);
        result.append("}");

        return result.toString();
    }

    public String version() {
        return VERSION;
    }

    // --------------------------------------------------------------------
    //               C O N S O L E
    // --------------------------------------------------------------------

    public ToolConsole console() {
        return _console;
    }

    // --------------------------------------------------------------------
    //               I 1 8 N
    // --------------------------------------------------------------------

    public ToolI18n i18n() {
        return _i18n;
    }

    // --------------------------------------------------------------------
    //               F R A M E
    // --------------------------------------------------------------------

    public ToolFrame frame() {
        return _frame;
    }

    // --------------------------------------------------------------------
    //               D E V I C E
    // --------------------------------------------------------------------

    public ToolDevice device() {
        return _device;
    }

    public Object device(final Object property) {
        if (property instanceof String) {
            return _device.get((String) property);
        }
        return UNDEFINED;
    }

    // --------------------------------------------------------------------
    //               C O N N E C T I O N
    // --------------------------------------------------------------------

    public ToolConnection connection() {
        return _connection;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

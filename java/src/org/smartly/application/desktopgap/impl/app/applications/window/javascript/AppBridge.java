package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import org.smartly.application.desktopgap.impl.app.applications.window.AppWindow;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.connection.ToolConnection;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.console.ToolConsole;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.device.ToolDevice;
import org.smartly.commons.util.ConversionUtils;

/**
 * Javascript-Java Bridge.
 */
public class AppBridge {

    public static final String NAME = "bridge";

    private static final String BUTTON_CLOSE = "close";
    private static final String BUTTON_MINIMIZE = "minimize";
    private static final String BUTTON_MAXIMIZE = "maximize";
    private static final String BUTTON_FULLSCREEN = "fullscreen";

    private static final String UNDEFINED = JsEngine.UNDEFINED;

    private final AppWindow _window;
    // tools
    private final ToolDevice _device;
    private final ToolConnection _connection;
    private final ToolConsole _console;

    public AppBridge(final AppWindow window) {
        _window = window;
        // tools
        _device = new ToolDevice();
        _connection = new ToolConnection();
        _console = ToolConsole.getConsole(_window.getApp());
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName()).append("{");

        result.append("}");

        return result.toString();
    }

    // --------------------------------------------------------------------
    //               C O N S O L E
    // --------------------------------------------------------------------

    public ToolConsole console() {
        return _console;
    }

    // --------------------------------------------------------------------
    //               F R A M E
    // --------------------------------------------------------------------

    public void buttonClicked(final Object name) {
        if (name instanceof String) {
            final String button_name = (String) name;
            if (BUTTON_CLOSE.equalsIgnoreCase(button_name)) {
                _window.close();
            } else if (BUTTON_MINIMIZE.equalsIgnoreCase(button_name)) {
                _window.minimize();
            }
        }
    }

    public void setArea(final Object name,
                        final Object left, final Object top, final Object right, final Object height) {
        if (name instanceof String && !name.toString().equalsIgnoreCase(UNDEFINED)) {
            final double d_left = ConversionUtils.toDouble(left);
            final double d_top = ConversionUtils.toDouble(top);
            final double d_right = ConversionUtils.toDouble(right);
            final double d_height = ConversionUtils.toDouble(height);
            _window.setArea((String) name, d_left, d_top, d_right, d_height);
        }
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

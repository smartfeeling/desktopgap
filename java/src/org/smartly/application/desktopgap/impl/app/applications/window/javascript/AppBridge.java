package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import org.smartly.application.desktopgap.impl.app.applications.window.AppWindow;
import org.smartly.commons.util.ConversionUtils;

/**
 * Javascript-Java Bridge.
 */
public class AppBridge {

    public static final String DESKTOPGAP_INSTANCE = "window.desktopgap";
    public static final String DESKTOPGAP_INIT_FUNC = DESKTOPGAP_INSTANCE + ".events.trigger('ready')";

    public static final String NAME = "bridge";

    private static final String BUTTON_CLOSE = "close";
    private static final String BUTTON_MINIMIZE = "minimize";
    private static final String BUTTON_MAXIMIZE = "maximize";
    private static final String BUTTON_FULLSCREEN = "fullscreen";

    private static final String UNDEFINED = "undefined";

    private AppWindow _window;

    public AppBridge(final AppWindow window) {
        _window = window;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName()).append("{");

        result.append("}");

        return result.toString();
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
            _window.setArea((String)name, d_left, d_top, d_right, d_height);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

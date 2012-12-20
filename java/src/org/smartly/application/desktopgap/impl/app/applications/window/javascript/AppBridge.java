package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import org.smartly.application.desktopgap.impl.app.applications.window.AppWindow;

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

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

/**
 * User: angelo.geminiani
 */
public class AppBridge {

    public static final String NAME = "foo";

    public AppBridge() {

    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(this.getClass().getSimpleName()).append("{");

        result.append("}");

        return result.toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

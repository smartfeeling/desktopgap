package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.logging.Logger;

/**
 *  Extend this to create a valid javascript extension.
 *
 */
public abstract class AbstractTool {

    private final AppInstance _app;

    public AbstractTool(final AppInstance app) {
        _app = app;
    }

    public abstract String getToolName();

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected AppInstance getApp() {
        return _app;
    }

    protected Logger getLogger() {
        return _app.getLogger();
    }

    // ------------------------------------------------------------------------
    //                      MAIN
    // ------------------------------------------------------------------------

    public static void main(String[] args) {
        // nothing to do
    }
}

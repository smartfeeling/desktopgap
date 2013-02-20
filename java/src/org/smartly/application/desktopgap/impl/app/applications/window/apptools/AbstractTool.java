package org.smartly.application.desktopgap.impl.app.applications.window.apptools;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.logging.Logger;

/**
 *  Extend this to create a valid javascript extension.
 *
 */
public abstract class AbstractTool {

    private final AppInstance _app;
    private final AppFrame _frame;

    public AbstractTool(final AppInstance app) {
        _frame = null;
        _app = app;
    }

    public AbstractTool(final AppFrame frame) {
        _frame = frame;
        _app = frame.getApp();
    }

    public abstract String getToolName();

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected AppInstance getApp() {
        return _app;
    }

    protected AppFrame getFrame(){
        return _frame;
    }

    protected boolean hasFrame(){
        return null!=_frame;
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

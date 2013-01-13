package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.logging.Logger;

/**
 *
 */
public class AbstractTool {

    private final AppFrame _frame;

    public AbstractTool(final AppFrame frame) {
        _frame = frame;
    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected AppFrame getFrame() {
        return _frame;
    }

    protected Logger getLogger() {
        return _frame.getApp().getLogger();
    }

}

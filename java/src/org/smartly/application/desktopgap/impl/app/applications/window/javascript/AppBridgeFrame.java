package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.frame.ToolFrame;

/**
 * Bridge for single frame
 */
public class AppBridgeFrame extends AppBridge {

    public AppBridgeFrame(final AppBridge parent,
                          final AppFrame frame) {
        super(frame.getApp());

        super.register(parent.getTools());
        super.register(ToolFrame.NAME, new ToolFrame(frame));
    }

    // --------------------------------------------------------------------
    //               F R A M E
    // --------------------------------------------------------------------

    public Object frame() {
        return super.get(ToolFrame.NAME);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

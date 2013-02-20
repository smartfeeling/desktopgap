package org.smartly.application.desktopgap.impl.app.applications.window.frame;

import org.smartly.application.desktopgap.impl.app.applications.window.appbridge.AppBridge;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.frame.ToolFrame;

/**
 * Bridge for single frame
 */
public class AppBridgeFrame extends AppBridge {

    public AppBridgeFrame(final AppBridge parent,
                          final AppFrame frame) {
        super(parent);

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

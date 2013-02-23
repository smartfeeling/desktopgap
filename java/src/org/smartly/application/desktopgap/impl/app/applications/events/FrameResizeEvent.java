package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.event.Event;

/**
 * Frame "resize" event
 */
public class FrameResizeEvent extends Event {

    public static final String NAME = IDesktopGapEvents.FRAME_RESIZE;

    public FrameResizeEvent(final Object sender) {
        super(sender, NAME);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

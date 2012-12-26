package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.event.Event;

/**
 * Frame "open" event
 */
public class FrameOpenEvent extends Event {

    public static final String NAME = IDesktopGapEvents.FRAME_OPEN;

    public FrameOpenEvent(final AppFrame sender) {
        super(sender, NAME);
    }

    @Override
    public AppFrame getSender() {
        return (AppFrame) super.getSender();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

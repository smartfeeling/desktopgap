package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.commons.event.Event;

/**
 *
 */
public class FrameScrollEvent extends Event {

    public static final String NAME = IDesktopGapEvents.FRAME_SCROLL;

    public FrameScrollEvent(final Object sender) {
        super(sender, NAME);
    }

}

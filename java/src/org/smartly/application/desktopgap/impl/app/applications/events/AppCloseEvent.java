package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.commons.event.Event;

/**
 * Frame "close" event
 */
public class AppCloseEvent extends Event {

    public static final String NAME = IDesktopGapEvents.APP_CLOSE;

    public AppCloseEvent(final AppInstance sender) {
        super(sender, NAME);
    }

    @Override
    public AppInstance getSender() {
        return (AppInstance) super.getSender();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

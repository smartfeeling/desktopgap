package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.commons.event.Events;
import org.smartly.commons.util.StringUtils;

/**
 * Listen frame events
 */
public interface IDesktopGapEvents {

    public static final String FRAME_CLOSE = StringUtils.concatDot("frame", Events.ON_CLOSE);
    public static final String FRAME_OPEN = StringUtils.concatDot("frame", Events.ON_OPEN);
    public static final String FRAME_RESIZE = StringUtils.concatDot("frame", "onResize");

    public static final String APP_CLOSE = StringUtils.concatDot("app", Events.ON_CLOSE);
    public static final String APP_OPEN = StringUtils.concatDot("app", Events.ON_OPEN);

}

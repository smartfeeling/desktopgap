package org.smartly.application.desktopgap.impl.app.applications.window.webview;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.events.IDesktopGapEvents;

/**
 *
 */
public abstract class AbstractScriptEngine {

    public static final String UNDEFINED = "undefined";

    public static final String DESKTOPGAP_INSTANCE = IDesktopConstants.DESKTOPGAP_INSTANCE;

    //-- js events --//
    public static final String EVENT_READY = IDesktopGapEvents.EVENT_READY;
    public static final String EVENT_DEVICEREADY = IDesktopGapEvents.EVENT_DEVICEREADY;
    public static final String EVENT_PLUGIN_READY = IDesktopGapEvents.EVENT_PLUGIN_READY;

    public static final String EVENT_RESIZE = IDesktopGapEvents.FRAME_RESIZE;
    public static final String EVENT_SCROLL = IDesktopGapEvents.FRAME_SCROLL;
    public static final String EVENT_DRAG_OVER = IDesktopGapEvents.FRAME_DRAG_DROPPED;

    public static final String EVENT_DATA = IDesktopGapEvents.EVENT_DATA;


    public abstract void whenReady(final String script);

    public abstract void executeScript(final String script);

    public abstract void emitEvent(final String name, final Object data);
}

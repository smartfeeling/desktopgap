package org.smartly.application.desktopgap.impl.app.applications.window.webview;

import org.smartly.application.desktopgap.impl.app.applications.events.FrameDragEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameResizeEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameScrollEvent;

/**
 *
 */
public abstract class AbstractScriptEngine {

    public static final String UNDEFINED = "undefined";

    //-- js events --//
    public static final String EVENT_READY = "ready";
    public static final String EVENT_DEVICEREADY = "deviceready";
    public static final String EVENT_PLUGIN_READY = "pluginready";

    public static final String EVENT_RESIZE = FrameResizeEvent.NAME;
    public static final String EVENT_SCROLL = FrameScrollEvent.NAME;
    public static final String EVENT_DRAG_OVER = FrameDragEvent.NAME;

    public static final String EVENT_DATA = "data";

    public static final String DESKTOPGAP_INSTANCE = "window.desktopgap";


    public abstract void whenReady(final String script);

    public abstract void executeScript(final String script);

}

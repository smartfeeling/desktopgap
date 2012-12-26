package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.commons.logging.Level;

/**
 * Javascript engine helper
 */
public class JsEngine {

    public static final String EVENT_READY = "ready";
    public static final String EVENT_DEVICEREADY = "deviceready";

    public static final String UNDEFINED = "undefined";

    private static final String DESKTOPGAP_INSTANCE = "window.desktopgap";

    private final WebEngine _engine;
    private final AppFrame _window;

    public JsEngine(final AppFrame window, final WebEngine engine) {
        _window = window;
        _engine = engine;
    }

    public void init() {
        try {
            //-- get reference to javascript window object --//
            final Object obj = _engine.executeScript(DESKTOPGAP_INSTANCE);
            if (obj instanceof JSObject) {
                final JSObject win = (JSObject) obj;
                // can add custom java objects
                win.setMember(AppBridge.NAME, new AppBridge(_window));
            }
        } catch (Throwable t) {
            _window.getApp().getLogger().log(Level.SEVERE, null, t);
        }
    }

    public void dispatchEvent(final String eventName) {
        try {
            final String script = JsSnippet.getDispatchEvent(eventName);
            _engine.executeScript(script);
        } catch (Throwable t) {
            _window.getApp().getLogger().log(Level.SEVERE, null, t);
        }
    }

    /**
     * Dispatch both 'ready' and 'deviceready' events
     */
    public void dispatchReady() {
        try {
            final String script = JsSnippet.getDispatchEvent(EVENT_READY);
            _engine.executeScript(script);
        } catch (Throwable t) {
            _window.getApp().getLogger().log(Level.SEVERE, null, t);
        }
        try {
            final String script = JsSnippet.getDispatchEvent(EVENT_DEVICEREADY);
            _engine.executeScript(script);
        } catch (Throwable t) {
            _window.getApp().getLogger().log(Level.SEVERE, null, t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

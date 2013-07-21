package org.smartly.application.desktopgap.impl.app.applications.window.webview;

import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameResizeEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameScrollEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.commons.Delegates;

/**
 * Base class for webview containing utils methods and
 * event management.
 */
public abstract class AbstractWebView {

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    protected static interface OnInitialize {
        void handle();
    }

    public static interface OnScroll {
        void handle(final FrameScrollEvent event);
    }

    public static interface OnResize {
        void handle(final FrameResizeEvent event);
    }

    private static final Class EVENT_ON_SCROLL = OnScroll.class;
    private static final Class EVENT_ON_RESIZE = OnResize.class;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Delegates.Handlers _eventHandlers = new Delegates.Handlers();

    private AppFrame _frame;

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public void initialize(final AppFrame frame,
                           final OnInitialize callback) {
        if (null == _frame) {
            _frame = frame;
            if (null != callback) {
                callback.handle();
            }
        }
    }

    public AppFrame frame() {
        return _frame;
    }

    public abstract void initialize(final AppFrame frame);

    public abstract AbstractWebViewAreaManager getAreas();

    public abstract AbstractScriptEngine getScriptEngine();

    // --------------------------------------------------------------------
    //               events handlers
    // --------------------------------------------------------------------

    public void onScroll(final OnScroll handler) {
        _eventHandlers.add(handler);
    }

    public void triggerOnScroll(final FrameScrollEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_SCROLL, event);
        this.dispatchFrameScroll(new JSONObject());
    }

    public void onResize(final OnResize handler) {
        _eventHandlers.add(handler);
    }

    public void triggerOnResize(final FrameResizeEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_RESIZE, event);
        dispatchFrameResize(new JSONObject());
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void dispatchFrameResize(final JSONObject data) {
        // resize
        final String script = JsSnippet.getDispatchEvent(AbstractScriptEngine.EVENT_RESIZE, data);
        this.getScriptEngine().executeScript(script);
    }

    private void dispatchFrameScroll(final JSONObject data) {
        // resize
        final String script = JsSnippet.getDispatchEvent(AbstractScriptEngine.EVENT_SCROLL, data);
        this.getScriptEngine().executeScript(script);
    }

}

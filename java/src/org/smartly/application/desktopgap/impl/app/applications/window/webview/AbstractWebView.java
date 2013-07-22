package org.smartly.application.desktopgap.impl.app.applications.window.webview;

import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.events.*;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.application.desktopgap.impl.app.server.WebServer;
import org.smartly.application.desktopgap.impl.app.utils.URLUtils;
import org.smartly.commons.Delegates;
import org.smartly.commons.event.Event;

import java.util.Map;
import java.util.Set;

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


    private static final Class EVENT_ON_SCROLL = Handlers.OnScroll.class;
    private static final Class EVENT_ON_RESIZE = Handlers.OnResize.class;
    private static final Class EVENT_ON_KEY_PRESSED = Handlers.OnKeyPressed.class;
    private static final Class EVENT_ON_DRAG_OVER = Handlers.OnDragOver.class;
    private static final Class EVENT_ON_DRAG_DROPPED = Handlers.OnDragDropped.class;

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

    public String getHttpIndex() {
        final String url = _frame.getIndex();
        return getHttpIndex(url, true, null);
    }

    public String getHttpIndex(final boolean isDesktopGap) {
        final String url = _frame.getIndex();
        return getHttpIndex(url, isDesktopGap, null);
    }

    public String getHttpIndex(final String url, final boolean isDesktopGap, final Map<String, String> args) {
        String result = WebServer.getHttpPath(url, isDesktopGap); //uri.getUrl();
        result = URLUtils.addParamToUrl(result, IDesktopConstants.PARAM_APPID, _frame.getApp().getId());
        result = URLUtils.addParamToUrl(result, IDesktopConstants.PARAM_FRAMEID, _frame.getId());
        if (null != args && args.size() > 0) {
            final Set<String> keys = args.keySet();
            for (final String key : keys) {
                URLUtils.addParamToUrl(result, key, args.get(key));
            }
        }
        return result;
    }

    public abstract void initialize(final AppFrame frame);

    public abstract AbstractWebViewAreaManager getAreas();

    public abstract AbstractScriptEngine getScriptEngine();

    // --------------------------------------------------------------------
    //               events handlers
    // --------------------------------------------------------------------

    public void onEvent(final Delegates.Handler handler) {
        _eventHandlers.add(handler);
    }

    // --------------------------------------------------------------------
    //               events triggers
    // --------------------------------------------------------------------

    protected void triggerOnScroll(final FrameScrollEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_SCROLL, event);
        dispatchToJsEngine(event);
    }

    protected void triggerOnResize(final FrameResizeEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_RESIZE, event);
        dispatchToJsEngine(event);
    }

    protected void triggerOnKeyPressed(final FrameKeyPressedEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_KEY_PRESSED, event);
        dispatchToJsEngine(event);
    }

    protected void triggerOnDragDropped(final FrameDragEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_DRAG_DROPPED, event);
        dispatchToJsEngine(event);
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private void dispatchToJsEngine(final Event event) {
        // resize
        final String script = JsSnippet.getDispatchEvent(event.getName(), event.toJSON());
        this.getScriptEngine().executeScript(script);
    }

}

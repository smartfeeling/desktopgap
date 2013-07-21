package org.smartly.application.desktopgap.impl.app.applications.window.webview;

import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameKeyPressedEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameResizeEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameScrollEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.Handlers;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.application.desktopgap.impl.app.server.WebServer;
import org.smartly.application.desktopgap.impl.app.utils.URLUtils;
import org.smartly.commons.Delegates;

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

    public void onScroll(final Handlers.OnScroll handler) {
        _eventHandlers.add(handler);
    }

    public void triggerOnScroll(final FrameScrollEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_SCROLL, event);
        this.dispatchFrameScroll(event.toJSON());
    }

    public void onResize(final Handlers.OnResize handler) {
        _eventHandlers.add(handler);
    }

    public void triggerOnResize(final FrameResizeEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_RESIZE, event);
        dispatchFrameResize(event.toJSON());
    }

    public void onKeyPressed(final Handlers.OnKeyPressed handler) {
        _eventHandlers.add(handler);
    }

    public void triggerOnKeyPressed(final FrameKeyPressedEvent event) {
        _eventHandlers.triggerAsync(EVENT_ON_KEY_PRESSED, event);
        dispatchFrameResize(event.toJSON());
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

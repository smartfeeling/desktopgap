package org.smartly.application.desktopgap.impl.app.applications.window.webview.jfx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.appbridge.AppBridge;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppBridgeFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractScriptEngine;
import org.smartly.commons.logging.Level;

import java.util.*;

/**
 * Javascript engine helper
 */
public final class JfxJsEngine
        extends AbstractScriptEngine {


    private final AppInstance _app;
    private final AppBridgeFrame _bridge_frame;
    private final List<String> _cached_scripts;


    private AppFrame _frame;
    private WebEngine _engine;
    private boolean _script_ready;

    public JfxJsEngine(final AppFrame frame) {
        _app = frame.getApp();
        _bridge_frame = frame.getBridge();
        _frame = frame;
        _cached_scripts = Collections.synchronizedList(new LinkedList<String>());
        _script_ready = false;
    }

    public void handleLoading(final WebEngine engine) {
        _engine = engine;
        this.handleWebEngineLoading(engine);
    }

    @Override
    public void whenReady(final String script) {
        if (!_script_ready) {
            synchronized (_cached_scripts) {
                _cached_scripts.add(script);
            }
        } else {
            this.executeScript(script);
        }
    }

    @Override
    public void executeScript(final String script) {
        executeScript(this, script);
    }

    @Override
    public void emitEvent(final String name, final Object data) {
        final JfxJsEngine self = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                final String script_resize = JsSnippet.getDispatchEvent(name, data);
                executeScript(self, script_resize);
            }
        });
    }

    public void emitEventResize(final JSONObject data) {
        this.dispatchFrameResize(data);
    }

    // --------------------------------------------------------------------
    //               Javascript (protected. use whenReady to call Js)
    // --------------------------------------------------------------------


    //-- EVENTS --//

    protected void dispatchEvent(final String eventName, final Object data) {
        final String script = JsSnippet.getDispatchEvent(eventName, data);
        this.executeScript(script);
    }

    /**
     * Dispatch both 'ready' and 'deviceready' events
     */
    protected void dispatchReady() {
        // ready
        final String script_ready = JsSnippet.getDispatchEvent(EVENT_READY, null);
        this.executeScript(script_ready);
        // deviceready
        final String script_deviceready = JsSnippet.getDispatchEvent(EVENT_DEVICEREADY, null);
        this.executeScript(script_deviceready);
    }

    protected void dispatchFrameResize(final JSONObject data) {
        // resize
        final String script_resize = JsSnippet.getDispatchEvent(EVENT_RESIZE, data);
        this.executeScript(script_resize);
    }

    //-- SHOW HIDE ELEM--//

    protected void showHideElem(final Map<String, Boolean> elems) {
        final Set<String> keys = elems.keySet();
        for (final String key : keys) {
            this.showHideElem(key, elems.get(key));
        }
    }

    protected void showHideElem(final String elementId, final boolean visible) {
        final String script = JsSnippet.getShowHideElem(elementId, visible);
        this.executeScript(script);
    }

    //-- SET ELEM VALUE --//

    protected void setElemValue(final String elementId, final String value) {
        final String script = JsSnippet.getSetElemValue(elementId, value);
        this.executeScript(script);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        try {
            //-- get reference to javascript window object --//
            final Object obj = _engine.executeScript(DESKTOPGAP_INSTANCE);
            if (obj instanceof JSObject) {
                final JSObject win = (JSObject) obj;
                // can add custom java objects
                win.setMember(AppBridge.NAME, _bridge_frame);

                this.executeCache();
            }
        } catch (Throwable t) {
            _app.getLogger().log(Level.SEVERE, null, t);
        }
    }

    private void executeCache() {
        synchronized (_cached_scripts) {
            for (final String script : _cached_scripts) {
                this.executeScript(script);
            }
            _cached_scripts.clear();
        }
    }

    private void scriptReady(final boolean value) {
        _script_ready = value;
        if (value) {
            this.executeCache();
        }
    }


    private void handleWebEngineLoading(final WebEngine engine) {
        // process page loading
        engine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> ov,
                                        Worker.State oldState, Worker.State newState) {
                        try {
                            // debug info
                            //System.out.println(newState);
                            if (newState == Worker.State.CANCELLED) {
                                // navigation cancelled by user
                                scriptReady(false);
                            } else if (newState == Worker.State.FAILED) {
                                // navigation failed
                                scriptReady(false);
                            } else if (newState == Worker.State.READY) {
                                scriptReady(false);
                            } else if (newState == Worker.State.SCHEDULED) {
                                // browser scheduled navigation
                                //System.out.println(engine.getLocation());
                                scriptReady(false);
                            } else if (newState == Worker.State.RUNNING) {
                                // browser is loading data
                                scriptReady(false);
                            } else if (newState == Worker.State.SUCCEEDED) {
                                init();
                                showHideElem(_frame.getManifest().getButtonsMap());
                                dispatchReady();
                                scriptReady(true);
                            }
                        } catch (Throwable t) {
                            _app.getLogger().log(Level.SEVERE, null, t);
                        }
                    }
                }
        );

    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------

    private static void executeScript(final JfxJsEngine instance, final String script) {
        synchronized (instance) {
            try {
                instance._engine.executeScript(script);
            } catch (Throwable t) {
                instance._frame.getApp().getLogger().log(Level.SEVERE, null, t);
            }
        }
    }
}

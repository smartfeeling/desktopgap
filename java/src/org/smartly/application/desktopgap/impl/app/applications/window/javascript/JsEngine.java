package org.smartly.application.desktopgap.impl.app.applications.window.javascript;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.commons.logging.Level;

import java.util.*;

/**
 * Javascript engine helper
 */
public final class JsEngine {

    public static final String UNDEFINED = "undefined";

    //-- js events --//
    public static final String EVENT_READY = "ready";
    public static final String EVENT_DEVICEREADY = "deviceready";
    public static final String EVENT_DATA = "data";

    private static final String DESKTOPGAP_INSTANCE = "window.desktopgap";

    private final WebEngine _engine;
    private final AppFrame _frame;
    private final List<String> _cached_scripts;

    private boolean _script_ready;

    public JsEngine(final AppFrame frame,
                    final WebEngine engine) {
        _frame = frame;
        _engine = engine;
        _cached_scripts = Collections.synchronizedList(new LinkedList<String>());
        _script_ready = false;

        this.handleLoading(engine);
    }

    public void whenReady(final String script) {
        if (!_script_ready) {
            synchronized (_cached_scripts) {
                _cached_scripts.add(script);
            }
        } else {
            this.executeScript(script);
        }
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
                win.setMember(AppBridge.NAME, new AppBridge(_frame));

                this.executeCache();
            }
        } catch (Throwable t) {
            _frame.getApp().getLogger().log(Level.SEVERE, null, t);
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

    private void executeScript(final String script) {
        try {
            _engine.executeScript(script);
        } catch (Throwable t) {
            _frame.getApp().getLogger().log(Level.SEVERE, null, t);
        }
    }

    private void handleLoading(final WebEngine engine) {
        // process page loading
        engine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> ov,
                                        Worker.State oldState, Worker.State newState) {
                        // debug info
                        //System.out.println(newState);
                        if (newState == Worker.State.CANCELLED) {
                            // navigation cancelled by user
                            _script_ready = false;
                        } else if (newState == Worker.State.FAILED) {
                            // navigation failed
                            _script_ready = false;
                        } else if (newState == Worker.State.READY) {
                            _script_ready = false;
                        } else if (newState == Worker.State.SCHEDULED) {
                            // browser scheduled navigation
                            //System.out.println(engine.getLocation());
                            _script_ready = false;
                        } else if (newState == Worker.State.RUNNING) {
                            // browser is loading data
                            _script_ready = false;
                        } else if (newState == Worker.State.SUCCEEDED) {
                            init();
                            showHideElem(_frame.getManifest().getButtonsMap());
                            dispatchReady();
                            _script_ready = true;
                        }
                    }
                }
        );

    }
}

package org.smartly.application.desktopgap.impl.app.applications.window.frame;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.applications.events.*;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.AppRegistry;
import org.smartly.application.desktopgap.impl.app.applications.window.AppWindows;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractScriptEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractWebView;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.jfx.JfxJsEngine;
import org.smartly.application.desktopgap.impl.app.utils.fx.FX;
import org.smartly.application.desktopgap.impl.resources.AppResources;
import org.smartly.commons.Delegates;
import org.smartly.commons.async.Async;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.SystemUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Window
 */
public final class AppFrame {

    private static final double OFF_SET = 0; // border for shadow (with frame was 10 px)

    private final static String STYLE_SHEET = "window.css";

    private static final String[] ICONS = new String[]{
            "icon.png",
            "icon_16.png",
            "icon_32.png",
            "icon_64.png",
            "icon_125.png"
    };

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    private static final Class EVENT_ON_OPEN = Handlers.OnOpen.class;
    private static final Class EVENT_ON_CLOSE = Handlers.OnClose.class;
    private static final Class EVENT_ON_HIDDEN = Handlers.OnHidden.class;
    private static final Class EVENT_ON_SCROLL = Handlers.OnScroll.class;
    private static final Class EVENT_ON_RESIZE = Handlers.OnResize.class;
    private static final Class EVENT_ON_KEY_PRESSED = Handlers.OnKeyPressed.class;
    private static final Class EVENT_ON_DRAG_DROPPED = Handlers.OnDragDropped.class;

    // --------------------------------------------------------------------
    //               f i e l d s
    // --------------------------------------------------------------------

    private final Delegates.Handlers _eventHandlers = new Delegates.Handlers();

    private final Parent _fxml;
    private final FXMLLoader _loader;
    private final AbstractWebView _webview;

    private final AppWindows _windows;
    private final AppInstance _app;
    private final AppBridgeFrame _bridge_frame;
    private final AppLibsFrame _libs_frame;

    private final String _id;
    private String _title;
    private boolean _maximized;
    private Rectangle2D _old_rect; // size before maximize

    private Stage __stage;

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public AppFrame(final AppWindows windows,
                    final String id) {
        _windows = windows;
        _app = _windows.getApp();

        _loader = new FXMLLoader();
        _fxml = getContent(_loader);
        _webview = _loader.getController();

        _id = id;
        _title = _app.getManifest().getTitle();
        _bridge_frame = new AppBridgeFrame(_app.getBridge(), this);
        _libs_frame = new AppLibsFrame(_app.getLibs(), _bridge_frame);

        _maximized = false;
        _old_rect = this.getRegistryRect();

        // initializes frame, controller and jsengine
        this.initialize(_webview);
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String getId() {
        return _id;
    }

    public AppInstance getApp() {
        return _app;
    }

    public AppBridgeFrame getBridge() {
        return _bridge_frame;
    }

    public boolean isMain() {
        return this.getId().equalsIgnoreCase(_app.getId());
    }

    public boolean isResizable() {
        return _app.getManifest().isResizable();
    }

    public boolean isDraggable() {
        return _app.getManifest().isDraggable();
    }

    public double getMinWidth() {
        return _app.getManifest().getMinWidth();
    }

    public double getMinHeight() {
        return _app.getManifest().getMinHeight();
    }

    public double getMaxWidth() {
        return _app.getManifest().getMaxWidth();
    }

    public double getMaxHeight() {
        return _app.getManifest().getMaxHeight();
    }

    public String getFrame() {
        try {
            if (null != _app) {
                return _app.getManifest().getAbsolutePageFrame();
            }
        } catch (Throwable ignored) {
        }
        return AppResources.getPageUri_BLANK();
    }

    public String getIndex() {
        try {
            if (null != _app) {
                return _app.getManifest().getRelativeIndex();
            }
        } catch (Throwable ignored) {
        }
        return AppResources.getPageUri_BLANK();
    }

    public AppManifest getManifest() {
        return null != _app ? _app.getManifest() : null;
    }

    public boolean isMaximized() {
        return _maximized;
    }

    public double getX() {
        if (null != __stage) {
            return __stage.getX();
        }
        return 0.0;
    }

    public double setX(final double value) {
        final double old_value = this.getX();
        if (null != __stage) {
            __stage.setX(value);
        }
        return old_value;
    }

    public double getY() {
        if (null != __stage) {
            return __stage.getY();
        }
        return 0.0;
    }

    public double setY(final double value) {
        final double old_value = this.getY();
        if (null != __stage) {
            __stage.setY(value);
        }
        return old_value;
    }

    public double getWidth() {
        if (null != __stage) {
            return __stage.getScene().getWidth();
        }
        return 0.0;
    }

    public double setWidth(final double value) {
        final double old_value = this.getWidth();
        if (null != __stage) {
            __stage.setWidth(value);
        }
        return old_value;
    }

    public double getHeight() {
        if (null != __stage) {
            if (__stage.getHeight() > 0) {
                // height of frame with form title
                return __stage.getHeight();
            }
            return __stage.getScene().getHeight();
        }
        return 0.0;
    }

    public double setHeight(final double value) {
        final double old_value = this.getHeight();
        if (null != __stage) {
            __stage.setHeight(value);
        }
        return old_value;
    }

    public void open() {
        openOrFocus();
    }

    // --------------------------------------------------------------------
    //               script engine
    // --------------------------------------------------------------------


    public void scriptExecute(final String script) throws Exception {
        final AbstractScriptEngine engine = this.getScriptEngine();
        if (null != engine) {
            engine.whenReady(script);
        } else {
            throw new Exception("ScriptEngine or WebView not initialized");
        }
    }

    public void scriptTriggerEvent(final String eventName, final Object data) throws Exception {
        final AbstractScriptEngine engine = this.getScriptEngine();
        if (null != engine) {
            engine.emitEvent(eventName, data);
        } else {
            throw new Exception("ScriptEngine or WebView not initialized");
        }
    }

    // --------------------------------------------------------------------
    //               frame
    // --------------------------------------------------------------------

    public Stage stage(){
        return this.getStage();
    }

    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = null != title ? title : "";

        // pass title to javascript
        try{
            this.scriptExecute(JsSnippet.getSetElemValue("title", _title));
        } catch(Throwable ignored){
        }

        if (null != __stage) {
            __stage.setTitle(_title);
        }
    }

    public void close() {
        if (null != __stage) {
            // close stage and trigger event
            _eventHandlers.trigger(EVENT_ON_CLOSE, new FrameCloseEvent(this));
            __stage.close();
        }
    }

    /**
     * Close frame with no events
     */
    public void kill() {
        if (null != __stage) {
            __stage.close();
        }
    }

    public void toFront() {
        if (null != __stage) {
            __stage.toFront();
        }
    }

    public void toBack() {
        if (null != __stage) {
            __stage.toBack();
        }
    }

    public void screenCenter() {
        if (null != __stage) {
            __stage.centerOnScreen();
        }
    }

    public void screenCenterTop() {
        this.screenCenter();
        this.setY(0 - OFF_SET);
    }

    public void minimize() {
        if (null != __stage) {
            __stage.setIconified(true);
        }
    }

    public void maximize() {
        _maximized = !_maximized;
        if (_maximized) {
            // save old position
            _old_rect = this.getCurrRect();
            // full screen size
            final Rectangle2D rect = FX.getScreenSize();
            this.setCurrRect(rect, OFF_SET);
        } else {
            // previous size
            this.setCurrRect(_old_rect);
        }
    }

    public void setArea(final String name,
                        final double left, final double top, final double right, final double height) {
        if (null != _webview) {
            _webview.getAreas().setArea(name, left, top, right, height);
        }
    }


    public void showHideElem(final String elementId, final boolean visible) {
        if (null != _webview && null != _webview.getScriptEngine()) {
            _webview.getScriptEngine().whenReady(JsSnippet.getShowHideElem(elementId, visible));
        }
    }

    /**
     * Pass custom arguments (data) to javascript.
     *
     * @param data Custom data to pass to javascript engine.
     */
    public void putArguments(final Object data) {
        if (null != _webview && null != _webview.getScriptEngine()) {
            _webview.getScriptEngine().whenReady(JsSnippet.getDispatchEvent(JfxJsEngine.EVENT_DATA, data));
        }
    }

    // --------------------------------------------------------------------
    //               frame  events
    // --------------------------------------------------------------------

    public void onEvent(final Delegates.Handler handler) {
        _eventHandlers.add(handler);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initialize(final AbstractWebView webview) {
        //-- initialize window controller --//
        webview.initialize(this);

        this.handleWebViewEvents(webview);

        // add shadow
        if (_app.getManifest().hasShadow()) {
            //_fxml.getStylesheets().add(this.getStyleSheet());
        }

        // register frame tools
        _libs_frame.registerFrameTools(this);
    }

    private Stage getStage() {
        if (null == __stage) {
            __stage = new Stage(StageStyle.TRANSPARENT);

            // event handlers
            this.handleStageEvents(__stage);

            // init stage bar
            this.initStageBar(__stage);

            // init stage and scene size
            this.initStageSize(__stage);
        }
        return __stage;
    }

    private void initStageBar(final Stage stage) {
        final AppManifest manifest = _app.getManifest();

        stage.setTitle(manifest.getTitle());

        // buttons
        stage.initStyle(StageStyle.DECORATED);

        // add icons
        stage.getIcons().addAll(this.getIcons());
    }

    private void initStageSize(final Stage stage) {
        final Rectangle2D rect = this.getRegistryRect();
        stage.setScene(this.createScene(_fxml, rect));

        //-- size --//
        this.setCurrRect(rect);

        //-- min size --//
        try {
            stage.setMinWidth(this.getMinWidth());
            stage.setMinHeight(this.getMinHeight());
        } catch (Throwable ignored) {

        }

    }

    private void openOrFocus() {
        final Stage stage = this.getStage();
        if (stage.isShowing()) {
            stage.toFront();
            if (stage.isIconified()) {
                stage.setIconified(false);
            }
        } else {
            stage.show();
            //-- notify open --//
            _eventHandlers.triggerAsync(EVENT_ON_OPEN, new FrameOpenEvent(this));

            //_app.getLogger().info("App Window Opened: " + _app.getId());
        }
    }

    private Scene createScene(final Parent parent, final Rectangle2D size) {
        //final AppBrowser parent = new AppBrowser(this);

        final Scene scene = new Scene(parent,
                size.getWidth() > 0 ? size.getWidth() : 300,
                size.getHeight() > 0 ? size.getHeight() : 300);

        scene.setFill(null);

        return scene;
    }

    private Rectangle2D getRegistryRect() {
        final AppRegistry registry = _app.getRegistry();
        final boolean fromFile = registry.isLoadedFromFile();
        final boolean resizable = _app.getManifest().isResizable();
        final boolean draggable = _app.getManifest().isDraggable();

        double x = registry.getX(this.getId());
        double y = registry.getY(this.getId());
        double width = registry.getWidth(this.getId());
        double height = registry.getHeight(this.getId());

        if (!fromFile || !resizable || !draggable) {
            final Rectangle2D screen = FX.getScreenSize();
            if (!fromFile || !resizable) {
                width = width + OFF_SET * 2;
                height = height + OFF_SET * 2;
            }
            if (!fromFile || !draggable) {
                x = x - OFF_SET + screen.getMinX();
                y = y - OFF_SET + screen.getMinY();
            }
        }

        return new Rectangle2D(x, y, width, height);
    }

    private Rectangle2D getCurrRect() {
        return new Rectangle2D(
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight());
    }

    private void setCurrRect(final Rectangle2D rect) {
        this.setY(rect.getMinY());
        this.setX(rect.getMinX());
        this.setHeight(rect.getHeight());
        this.setWidth(rect.getWidth());
    }

    private void setCurrRect(final Rectangle2D rect, final double offSet) {
        this.setY(rect.getMinY() - offSet);
        this.setX(rect.getMinX() - offSet);
        this.setHeight(rect.getHeight() + offSet * 2);
        this.setWidth(rect.getWidth() + offSet * 2);
    }

    private String getStyleSheet() {
        return AppResources.getAppFrameUri(STYLE_SHEET);
    }

    private List<Image> getIcons() {
        final List<Image> icons = new LinkedList<Image>();
        try {
            for (final String iconName : ICONS) {
                final String icon = _app.getAbsolutePath(iconName);
                if (PathUtils.exists(icon)) {
                    icons.add(new Image("file:".concat(icon)));
                }
            }
        } catch (Throwable t) {
            _app.getLogger().log(Level.WARNING, FormatUtils.format("Error searching Icon: '{0}'. Default Icone will be used.", t), t);
        }
        if (icons.isEmpty()) {
            icons.add(new Image(AppResources.getAppTemplateIcon()));
        }
        return icons;
    }

    private void handleStageEvents(final Stage stage) {
        final AppFrame self = this;

        //-- close event --//
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                _eventHandlers.trigger(EVENT_ON_CLOSE, new FrameCloseEvent(self));
            }
        });

        //-- hidden event --//
        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                _eventHandlers.triggerAsync(EVENT_ON_HIDDEN, new FrameHiddenEvent(self));
            }
        });
    }

    private void handleWebViewEvents(final AbstractWebView webview) {
        final AppFrame self = this;

        //-- onResize --//
        webview.onEvent(new Handlers.OnResize() {
            @Override
            public void handle(final FrameResizeEvent event) {
                _eventHandlers.triggerAsync(EVENT_ON_RESIZE, event);
            }
        });

        //-- key pressed --//
        webview.onEvent(new Handlers.OnKeyPressed() {
            @Override
            public void handle(final FrameKeyPressedEvent event) {
                _eventHandlers.triggerAsync(EVENT_ON_KEY_PRESSED, event);
                handleCtrlD(event);
            }
        });

        //-- onScroll --//
        webview.onEvent(new Handlers.OnScroll() {
            @Override
            public void handle(final FrameScrollEvent event) {
                _eventHandlers.triggerAsync(EVENT_ON_SCROLL, event);
            }
        });

        //-- drag dropped --//
        webview.onEvent(new Handlers.OnDragDropped() {
            @Override
            public void handle(final FrameDragEvent event) {
                _eventHandlers.triggerAsync(EVENT_ON_DRAG_DROPPED, event);
            }
        });
    }

    private void handleCtrlD(final FrameKeyPressedEvent event) {
        if (event.isControlDown() && event.getKeyCode().equalsIgnoreCase("D")) {
            if (this.getManifest().isDebug()) {
                final String page = _webview.getHttpIndex(false);
                Async.Action(new Delegates.Action() {
                    @Override
                    public void handle(Object... args) {
                        try {
                            SystemUtils.openURL(page);
                        } catch (Throwable t) {
                            getApp().getLogger().error(t.toString());
                        }
                    }
                });
            }
        }
    }

    private AbstractScriptEngine getScriptEngine() {
        return null != _webview ? _webview.getScriptEngine() : null;
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static Parent getContent(final FXMLLoader loader) {
        try {
            return (Parent) loader.load(AppFrame.class.getResource("frame.fxml").openStream());
        } catch (Throwable t) {
            Smartly.getLogger(AppFrame.class).severe(t);
            return new Pane(); // should return error message
        }
    }


}

package org.smartly.application.desktopgap.impl.app.applications.window.frame;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameCloseEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameOpenEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameResizeEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.AppRegistry;
import org.smartly.application.desktopgap.impl.app.applications.window.AppWindows;
import org.smartly.application.desktopgap.impl.app.applications.window.controller.AppWindowController;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.JsEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.application.desktopgap.impl.app.utils.fx.FX;
import org.smartly.application.desktopgap.impl.resources.AppResources;
import org.smartly.commons.event.Event;
import org.smartly.commons.event.EventEmitter;
import org.smartly.commons.event.IEventListener;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Window
 */
public final class AppFrame
        extends EventEmitter
        implements IEventListener {

    private static final double OFF_SET = 10; // border for shadow

    private final static String STYLE_SHEET = "window.css";

    private static final String[] ICONS = new String[]{
            "icon.png",
            "icon_16.png",
            "icon_32.png",
            "icon_64.png",
            "icon_125.png"
    };

    private final AppWindows _windows;
    private final AppWindowController _winctrl;
    private final AppInstance _app;
    private final FXMLLoader _loader;
    private final Parent _fxml;

    private final String _id;
    private Scene _scene;
    private String _title;
    private boolean _maximized;
    private Rectangle2D _old_rect; // size before maximize

    private Stage __stage;

    public AppFrame(final AppWindows windows,
                    final String id) {
        _windows = windows;
        _app = _windows.getApp();
        _loader = new FXMLLoader();
        _fxml = getContent(_loader);
        _winctrl = _loader.getController();
        _id = id;
        _title = _app.getManifest().getTitle();

        _maximized = false;
        _old_rect = this.getRegistryRect();

        this.initialize();
    }

    public String getId() {
        return _id;
    }

    public AppInstance getApp() {
        return _app;
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
    //               frame
    // --------------------------------------------------------------------

    public String getTitle() {
        return _title;
    }

    public void setTitle(final String title) {
        _title = null != title ? title : "";

        if (null != _winctrl) {
            _winctrl.getJsEngine().whenReady(JsSnippet.getSetElemValue("title", _title));
        }
    }

    public void close() {
        if (null != __stage) {
            // close stage and trigger event
            this.emit(new FrameCloseEvent(this));
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
        if (null != _winctrl) {
            _winctrl.getAreas().setArea(name, left, top, right, height);
        }
    }

    public void showHideElem(final String elementId, final boolean visible) {
        if (null != _winctrl) {
            _winctrl.getJsEngine().whenReady(JsSnippet.getShowHideElem(elementId, visible));
        }
    }

    /**
     * Pass custom arguments (data) to javascript.
     *
     * @param data Custom data to pass to javascript engine.
     */
    public void putArguments(final Object data) {
        if (null != _winctrl) {
            _winctrl.getJsEngine().whenReady(JsSnippet.getDispatchEvent(JsEngine.EVENT_DATA, data));
        }
    }

    // --------------------------------------------------------------------
    //               IEventListener
    // --------------------------------------------------------------------

    @Override
    public void on(final Event event) {
        if (event instanceof FrameResizeEvent) {
            // set width and height
            final JsonWrapper data = new JsonWrapper(new JSONObject());
            data.putSilent(IDesktopConstants.WIDTH, this.getWidth());
            data.putSilent(IDesktopConstants.HEIGHT, this.getHeight());
            event.setData(data.getJSONObject());

            this.emit(event);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initialize() {
        //-- initialize window controller --//
        _winctrl.initialize(this);

        // add shadow
        if (_app.getManifest().hasShadow()) {
            _fxml.getStylesheets().add(this.getStyleSheet());
        }
    }

    private Stage getStage() {
        if (null == __stage) {
            __stage = new Stage(StageStyle.UTILITY);
            // init stage
            __stage.setTitle(this.getTitle());
            __stage.initStyle(StageStyle.TRANSPARENT); // transparent by default
            __stage.getIcons().addAll(this.getIcons());

            final Rectangle2D rect = this.getRegistryRect();
            __stage.setScene(this.createScene(_fxml, rect));
            //-- size --//
            this.setCurrRect(rect);

            this.addStageHandlers();
        }
        return __stage;
    }

    private void openOrFocus() {
        final Stage stage = this.getStage();
        if (stage.isShowing()) {
            stage.toFront();
        } else {
            stage.show();
            //-- notify open --//
            this.onOpen();

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

    private void addStageHandlers() {
        /*final AppFrame self = this;
        //-- close event --//
        _stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                self.emit(new FrameCloseEvent(self));
            }
        }); */

    }

    private void onOpen() {
        this.emit(new FrameOpenEvent(this));
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

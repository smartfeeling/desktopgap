package org.smartly.application.desktopgap.impl.app.applications.window.frame;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameCloseEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameOpenEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.AppWindows;
import org.smartly.application.desktopgap.impl.app.applications.window.controller.AppWindowController;
import org.smartly.application.desktopgap.impl.app.utils.Size2D;
import org.smartly.application.desktopgap.impl.resources.AppResources;
import org.smartly.commons.event.EventEmitter;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Window
 */
public final class AppFrame extends EventEmitter {

    private final static String STYLE_SHEET = "window.css";

    private static final String[] ICONS = new String[]{
            "icon.png",
            "icon_16.png",
            "icon_32.png",
            "icon_64.png",
            "icon_125.png"
    };

    private final AppWindows _windows;
    private final AppInstance _app;
    private final FXMLLoader _loader;
    private final Parent _fxml;
    private final AppWindowController _winctrl;
    private final String _id;
    private Stage _stage;
    private Scene _scene;


    public AppFrame(final AppWindows windows, final String id) {
        _windows = windows;
        _app = _windows.getApp();
        _stage = new Stage(StageStyle.UTILITY);
        _loader = new FXMLLoader();
        _fxml = getContent(_loader);
        _winctrl = _loader.getController();
        _id = id;
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
                return _app.getManifest().getAbsoluteIndex();
            }
        } catch (Throwable ignored) {
        }
        return AppResources.getPageUri_BLANK();
    }

    public String getTitle() {
        return _app.getManifest().getTitle();
    }

    public AppManifest getManifest() {
        return null != _app ? _app.getManifest() : null;
    }

    public double getX() {
        if (null != _stage) {
            return _stage.getX();
        }
        return 0.0;
    }

    public double getY() {
        if (null != _stage) {
            return _stage.getY();
        }
        return 0.0;
    }

    public double getWidth() {
        if (null != _stage) {
            return _stage.getScene().getWidth();
        }
        return 0.0;
    }

    public double getHeight() {
        if (null != _stage) {
            return _stage.getScene().getHeight();
        }
        return 0.0;
    }

    public void open() {
        openOrFocus();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    // --------------------------------------------------------------------
    //               frame
    // --------------------------------------------------------------------

    public void close() {
        // close stage and trigger event
        this.emit(new FrameCloseEvent(this));
        _stage.close();
    }

    public void minimize() {
        _stage.setIconified(true);
    }

    public void setArea(final String name,
                        final double left, final double top, final double right, final double height) {
        if (null != _winctrl) {
            _winctrl.getAreas().setArea(name, left, top, right, height);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void openOrFocus() {
        if (_stage.isShowing()) {
            _stage.requestFocus();

        } else {
            final Size2D size = this.getSize();

            _stage.setTitle(this.getTitle());
            _stage.initStyle(StageStyle.TRANSPARENT); // transparent by default
            _stage.getIcons().addAll(this.getIcons());

            _stage.setScene(this.createScene(_fxml, size));

            //-- size --//
            if (size.getHeight() < 1 || size.getWidth() < 1) {
                final Rectangle2D screen = Screen.getPrimary().getVisualBounds();
                _stage.setHeight(screen.getHeight());
                _stage.setWidth(screen.getWidth());
                _stage.setX(screen.getMinX());
                _stage.setY(screen.getMinY());
            } else {
                this.setPosition(_stage);
            }

            _stage.setResizable(this.isResizable());

            this.addStageHandlers();

            //-- initialize window controller --//
            _winctrl.initialize(this);

            _stage.show();

            //-- notify open --//
            this.onOpen();

            _app.getLogger().info("App Window Opened: " + _app.getId());
        }

    }

    private Scene createScene(final Parent parent, final Size2D size) {
        //final AppBrowser parent = new AppBrowser(this);

        final Scene scene = new Scene(parent,
                size.getWidth() > 0 ? size.getWidth() : 300,
                size.getHeight() > 0 ? size.getHeight() : 300);

        scene.setFill(null);

        // add stylesheet
        parent.getStylesheets().add(this.getStyleSheet());

        return scene;
    }

    private boolean isResizable() {
        return _app.getManifest().isResizable();
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

    private Size2D getSize() {
        final double height = _app.getRegistry().getHeight(this.getId());
        final double width = _app.getRegistry().getWidth(this.getId());
        return new Size2D(height, width);
    }

    private void setPosition(final Stage stage) {
        double x = _app.getRegistry().getX(this.getId());
        double y = _app.getRegistry().getY(this.getId());
        if (x > -1) {
            stage.setX(x);
        }
        if (y > -1) {
            stage.setY(y);
        }
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

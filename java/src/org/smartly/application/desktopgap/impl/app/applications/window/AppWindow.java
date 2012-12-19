package org.smartly.application.desktopgap.impl.app.applications.window;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.utils.Size2D;
import org.smartly.application.desktopgap.impl.resources.AppResources;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * Window
 */
public final class AppWindow {

    private final static String STYLE_SHEET = "window.css";

    private static final String[] ICONS = new String[]{
            "icon.png",
            "icon_16.png",
            "icon_32.png",
            "icon_64.png",
            "icon_125.png"
    };

    private final AppInstance _app;
    private final FXMLLoader _loader;
    private final Parent _fxml;
    private final AppWindowController _winctrl;
    private Stage _stage;
    private Scene _scene;


    public AppWindow(final AppInstance app) {
        _app = app;
        _stage = new Stage(StageStyle.UTILITY);
        _loader = new FXMLLoader();
        _fxml = getContent(_loader);
        _winctrl = _loader.getController();
    }

    public AppInstance getApp() {
        return _app;
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

    public void open() {
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

        this.addHandlers(_stage);

        //-- initialize window controller --//
        _winctrl.initialize(this);

        _stage.show();

        //-- notify open --//
        this.onOpen();

        _app.getLogger().info("App Window Opened: " + _app.getId());
    }


    public void close() {
        // close stage and trigger event
        this.onClose(_stage);
        _stage.close();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

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


    private StageStyle getStyle() {
        try {
            //-- decoration --//
            final String style = _app.getManifest().getStyle();
            return StringUtils.hasText(style) ? StageStyle.valueOf(style.toUpperCase()) : StageStyle.DECORATED;
        } catch (Throwable t) {
            return StageStyle.DECORATED;
        }
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
        return new Size2D(_app.getManifest().getHeight(), _app.getManifest().getWidth());
    }

    private void setPosition(final Stage stage) {
        double x = _app.getManifest().getX();
        double y = _app.getManifest().getY();
        if (x > -1) {
            stage.setX(x);
        }
        if (y > -1) {
            stage.setY(y);
        }
    }


    private void addHandlers(final Stage stage) {
        //-- close event --//
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                onClose(stage);
            }
        });
    }

    private void onOpen() {
        _app.stageOpening();
    }

    private void onClose(final Stage stage) {
        _app.getManifest().setX(stage.getX());
        _app.getManifest().setY(stage.getY());
        _app.getManifest().setWidth(stage.getScene().getWidth());
        _app.getManifest().setHeight(stage.getScene().getHeight());

        _app.stageClosing();
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static Parent getContent(final FXMLLoader loader) {
        try {
            return (Parent) loader.load(AppWindow.class.getResource("window.fxml").openStream());
        } catch (Throwable t) {
            Smartly.getLogger(AppWindow.class).severe(t);
            return new Pane(); // should return error message
        }
    }

}

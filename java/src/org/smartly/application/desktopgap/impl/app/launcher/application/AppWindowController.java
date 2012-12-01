package org.smartly.application.desktopgap.impl.app.launcher.application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.smartly.application.desktopgap.impl.app.utils.fx.Draggable;
import org.smartly.application.desktopgap.impl.app.utils.fx.FX;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * FX Controller
 */
public class AppWindowController implements Initializable {

    // --------------------------------------------------------------------
    //               FX Components
    // --------------------------------------------------------------------

    @FXML
    private Pane container;

    @FXML
    private Pane win_top;

    @FXML
    private WebView win_browser;

    @FXML
    private Label win_title;

    // --------------------------------------------------------------------
    //               Constructor
    // --------------------------------------------------------------------

    public AppWindowController() {

    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        this.init(win_browser);

        FX.draggable(win_top);

        FX.sizable(container);
    }

    // --------------------------------------------------------------------
    //               Properties
    // --------------------------------------------------------------------

    public void navigate(final String url) {
        if (null != win_browser) {
            win_browser.getEngine().load("file:" + url);
        }
    }

    public void setTitle(final String title) {
        if (null != win_title) {
            win_title.setText(title);
        }
    }

    public void setStyle(final StageStyle style) {
        if (StageStyle.DECORATED.equals(style)) {

        } else if (StageStyle.UNDECORATED.equals(style)) {

        } else if (StageStyle.UTILITY.equals(style)) {

        } else if (StageStyle.TRANSPARENT.equals(style)) {

        }
    }



    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final WebView browser) {
        // disable context menu
        browser.setContextMenuEnabled(false);

        //-- handlers --//
        final WebEngine engine = browser.getEngine();
        this.handleLoading(engine);
        this.handlePopups(engine);
    }

    private void handleLoading(final WebEngine engine) {
        // process page loading
        engine.getLoadWorker().stateProperty().addListener(
                new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue<? extends Worker.State> ov,
                                        Worker.State oldState, Worker.State newState) {
                        // debug info
                        System.out.println(newState);

                        if (newState == Worker.State.SUCCEEDED) {

                        } else if (newState == Worker.State.READY) {
                            //-- get reference to javascript window object --//
                            final JSObject win = (JSObject) engine.executeScript("window");
                            // can add custom java objects
                        }
                    }
                }
        );

    }

    private void handlePopups(final WebEngine engine) {
        engine.setCreatePopupHandler(
                new Callback<PopupFeatures, WebEngine>() {

                    @Override
                    public WebEngine call(PopupFeatures config) {
                        /*final WebView smallView = new WebView();
                        smallView.setFontScale(0.8);
                        if (!toolBar.getChildren().contains(smallView)) {
                            toolBar.getChildren().add(smallView);
                        }
                        return smallView.getEngine();*/
                        return engine;
                    }

                }
        );

    }


}

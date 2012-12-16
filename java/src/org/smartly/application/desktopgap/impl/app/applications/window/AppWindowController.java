package org.smartly.application.desktopgap.impl.app.applications.window;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.web.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import netscape.javascript.JSObject;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.AppBridge;
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

    //-- win buttons --//
    @FXML
    private ImageView btn_close;

    // --------------------------------------------------------------------
    //               fields
    // --------------------------------------------------------------------

    private AppWindow _window;

    // --------------------------------------------------------------------
    //               Constructor
    // --------------------------------------------------------------------

    public AppWindowController() {

    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        FX.draggable(win_top);
        FX.sizable(container);

    }

    // --------------------------------------------------------------------
    //               event handlers
    // --------------------------------------------------------------------

    @FXML
    private void btn_close_click(MouseEvent event) {
        //System.out.println("You clicked me!");
        Stage stage = (Stage) btn_close.getScene().getWindow();
        //stage.close();
        if (null != _window) {
            _window.close();
        }
    }

    // --------------------------------------------------------------------
    //               Properties
    // --------------------------------------------------------------------

    public void initialize(final AppWindow window) {
        _window = window;

        this.initBrowser(win_browser);

        this.setTitle(_window.getTitle());
        this.navigate(_window.getRunPage());
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void navigate(final String url) {
        if (null != win_browser) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    win_browser.getEngine().load("file:///" + url);
                }
            });
        }
    }

    private void setTitle(final String title) {
        if (null != win_title) {
            win_title.setText(title);
        }
    }

    private void initBrowser(final WebView browser) {
        // disable context menu
        browser.setContextMenuEnabled(false);

        //-- handlers --//
        final WebEngine engine = browser.getEngine();
        this.initJavascript(engine);
        this.handleAlert(engine);
        this.handlePrompt(engine);
        this.handleLoading(engine);
        this.handlePopups(engine);

        final URL loading = getClass().getResource("loading.html");
        final String url = loading.toExternalForm();
        win_browser.getEngine().load(url);
    }

    private void initJavascript(final WebEngine engine){
        //-- get reference to javascript window object --//
        final JSObject win = (JSObject) engine.executeScript("window");
        // can add custom java objects
        win.setMember(AppBridge.NAME, new AppBridge());
    }

    private void handleAlert(final WebEngine engine) {
        engine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(final WebEvent<String> stringWebEvent) {
                if (null != stringWebEvent) {
                    final String data = stringWebEvent.getData();
                    System.out.println(data);
                }
            }
        });
    }

    private void handlePrompt(final WebEngine engine) {
        engine.setPromptHandler(new Callback<PromptData, String>() {
            @Override
            public String call(final PromptData promptData) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
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

                        if (newState == Worker.State.CANCELLED) {
                            final JSObject win = (JSObject) engine.executeScript("window");
                            System.out.println(newState + ": " + win.getMember(AppBridge.NAME));
                        } else if (newState == Worker.State.READY) {
                            final JSObject win = (JSObject) engine.executeScript("window");
                            System.out.println(newState + ": " + win.getMember(AppBridge.NAME));
                        } else if (newState == Worker.State.SCHEDULED) {
                            final JSObject win = (JSObject) engine.executeScript("window");
                            System.out.println(newState + ": " + win.getMember(AppBridge.NAME));
                        } else if (newState == Worker.State.RUNNING) {
                            final JSObject win = (JSObject) engine.executeScript("window");
                            System.out.println(newState + ": " + win.getMember(AppBridge.NAME));
                        } else if (newState == Worker.State.SUCCEEDED) {
                            initJavascript(engine);
                            engine.executeScript("initdg()");
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

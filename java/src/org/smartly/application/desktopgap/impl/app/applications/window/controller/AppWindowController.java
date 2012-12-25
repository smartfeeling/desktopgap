package org.smartly.application.desktopgap.impl.app.applications.window.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.smartly.application.desktopgap.impl.app.applications.window.AppWindow;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.JsEngine;
import org.smartly.application.desktopgap.impl.app.utils.fx.FX;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;

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
    private AnchorPane container;

    @FXML
    private WebView win_browser;

    //-- win buttons --//
    @FXML
    private ImageView btn_close;

    // --------------------------------------------------------------------
    //               fields
    // --------------------------------------------------------------------

    private AppWindow _window;
    private AppWindowAreaManager _areaManager;
    private JsEngine _jsengine;
    private String _location;
    private String _old_location;

    // --------------------------------------------------------------------
    //               Constructor
    // --------------------------------------------------------------------

    public AppWindowController() {

    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        // FX.draggable(win_top);
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
        _areaManager = new AppWindowAreaManager(container);

        this.initBrowser(win_browser);
        this.navigate(_window.getIndex());
    }

    public AppWindowAreaManager getAreas() {
        return _areaManager;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return _window.getApp().getLogger();
    }

    private void navigate(final String url) {
        if (null != win_browser) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    setLocation(url);
                }
            });
        }
    }

    private void setLocation(final String url) {
        if (null != win_browser) {
            try {
                // remove old
                AppWindowUrl.delete(_old_location);

                final AppWindowUrl uri = new AppWindowUrl(_window, url);
                // navigate page
                _location = uri.getUrl();
                win_browser.getEngine().load(_location);
            } catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, null, t);
            }
        }
    }


    private void initBrowser(final WebView browser) {
        // disable context menu
        browser.setContextMenuEnabled(false);

        //-- handlers --//
        final WebEngine engine = browser.getEngine();
        _jsengine = new JsEngine(_window, engine);
        _jsengine.init();

        this.handleAlert(engine);
        this.handlePrompt(engine);
        this.handleLoading(engine);
        this.handlePopups(engine);
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
                        //System.out.println(newState);

                        if (newState == Worker.State.CANCELLED) {
                            // navigation cancelled by user
                            //_location = _old_location;
                        } else if (newState == Worker.State.FAILED) {
                            // navigation failed
                            _location = _old_location;
                        } else if (newState == Worker.State.READY) {
                            // browser ready
                            //System.out.println(engine.getLocation());
                        } else if (newState == Worker.State.SCHEDULED) {
                            // browser scheduled navigation
                            //System.out.println(engine.getLocation());
                        } else if (newState == Worker.State.RUNNING) {
                            // browser is loading data
                            //System.out.println(engine.getLocation());
                            _old_location = _location;
                            _location = engine.getLocation();
                            if (!AppWindowUrl.equals(_old_location, _location)) {
                                //-- changing page --//
                                // System.out.println("FROM: " + _old_location + " TO: " + _location);

                                navigate(_location);
                            }
                        } else if (newState == Worker.State.SUCCEEDED) {
                            _jsengine.init();
                            _jsengine.dispatchReady();

                            //-- remove page --//
                            AppWindowUrl.delete(_location);
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

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------


}

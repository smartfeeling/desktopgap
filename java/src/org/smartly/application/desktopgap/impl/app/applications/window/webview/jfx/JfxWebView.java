package org.smartly.application.desktopgap.impl.app.applications.window.webview.jfx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.*;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameDragEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameKeyPressedEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameScrollEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractScriptEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractWebView;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractWebViewAreaManager;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AppWindowUrl;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * FX Controller
 */
public class JfxWebView extends AbstractWebView
        implements Initializable {

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

    private JfxWebViewAreaManager _areaManager;
    private JfxJsEngine _jsengine;
    private String _location;
    private String _old_location;

    // --------------------------------------------------------------------
    //               Constructor
    // --------------------------------------------------------------------

    public JfxWebView() {

    }

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        // ready
    }

    // --------------------------------------------------------------------
    //               event handlers
    // --------------------------------------------------------------------

    @FXML
    private void btn_close_click(MouseEvent event) {
        //System.out.println("You clicked me!");
        Stage stage = (Stage) btn_close.getScene().getWindow();
        //stage.close();
        if (null != super.frame()) {
            super.frame().close();
        }
    }

    // --------------------------------------------------------------------
    //               Properties
    // --------------------------------------------------------------------

    public void initialize(final AppFrame frame) {
        final AbstractWebView self = this;
        super.initialize(frame, new OnInitialize() {
            @Override
            public void handle() {
                _jsengine = new JfxJsEngine(frame);
                _areaManager = new JfxWebViewAreaManager(self, container);
                initBrowser(win_browser);
                navigate(frame().getIndex());
            }
        });
    }

    public AbstractWebViewAreaManager getAreas() {
        return _areaManager;
    }

    public AbstractScriptEngine getScriptEngine() {
        return _jsengine;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return super.frame().getApp().getLogger();
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
                // AppWindowUrl.delete(_old_location);

                // final AppWindowUrl uri = new AppWindowUrl(_frame, url);
                // navigate page
                _location = super.getHttpIndex(url, true, null);
                win_browser.getEngine().load(_location);
            } catch (Throwable t) {
                this.getLogger().log(Level.SEVERE, null, t);
            }
        }
    }


    private void initBrowser(final WebView browser) {
        final AppManifest manifest = super.frame().getManifest();

        // disable/enable context menu
        browser.setContextMenuEnabled(manifest.hasContextMenu());

        //-- handlers --//
        final WebEngine engine = browser.getEngine();
        _jsengine.handleLoading(engine);

        // disable alert(), prompt(), confirm()
        this.handleAlert(engine);  // replaced with console.warn()
        this.handlePrompt(engine);
        this.handleConfirm(engine);

        this.handleLoading(engine);
        this.handlePopups(engine);

        //-- events --//
        this.handle(browser);
    }

    private void handle(final WebView browser) {
        final AbstractWebView self = this;

        //-- events --//
        browser.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
                triggerOnScroll(new FrameScrollEvent(scrollEvent));
            }
        });

        browser.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(final KeyEvent keyEvent) {
                final FrameKeyPressedEvent event = new FrameKeyPressedEvent(self,
                        keyEvent.getCharacter(),
                        keyEvent.getText(),
                        keyEvent.getCode().toString(),
                        keyEvent.isShiftDown(),
                        keyEvent.isControlDown(),
                        keyEvent.isAltDown());
                triggerOnKeyPressed(event);
            }
        });

        browser.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(final DragEvent dragEvent) {
                Object source = null;
                final Dragboard db = dragEvent.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    // dragged a file
                    source = new ArrayList<String>();
                    success = true;
                    for (final File file:db.getFiles()) {
                        ((List)source).add(file.getAbsolutePath());
                    }
                } else {
                    // handle other objects
                      success = false;
                }
                dragEvent.setDropCompleted(success);
                dragEvent.consume();

                final FrameDragEvent event = new FrameDragEvent(self,
                        dragEvent.getX(),
                        dragEvent.getY(),
                        dragEvent.getScreenX(),
                        dragEvent.getScreenY(),
                        dragEvent.getSceneX(),
                        dragEvent.getSceneY(),
                        dragEvent.getTransferMode().name(),
                        source);

                triggerOnDragDropped(event);
            }
        });
    }

    private void handleAlert(final WebEngine engine) {
        engine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(final WebEvent<String> stringWebEvent) {
                if (null != stringWebEvent) {
                    final String data = stringWebEvent.getData();
                    _jsengine.whenReady(JsSnippet.getConsoleWarn(data));
                }
            }
        });
    }

    private void handlePrompt(final WebEngine engine) {
        engine.setPromptHandler(new Callback<PromptData, String>() {
            @Override
            public String call(final PromptData promptData) {
                return "";
            }
        });
    }

    private void handleConfirm(final WebEngine engine) {
        engine.setConfirmHandler(new Callback<String, Boolean>() {
            @Override
            public Boolean call(String s) {
                return false;
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
                        try {
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
                                //_jsengine.init();
                                //_jsengine.showHideElem(_frame.getManifest().getButtonsMap());
                                //_jsengine.dispatchReady();

                                //-- remove page --//
                                //AppWindowUrl.delete(_location);
                            }
                        } catch (Throwable t) {
                            getLogger().log(Level.SEVERE, null, t);
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

package org.smartly.application.desktopgap.impl.app;

import javafx.application.Application;
import javafx.stage.Stage;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.command.CommandHandler;
import org.smartly.application.desktopgap.impl.app.command.CommandSender;
import org.smartly.application.desktopgap.impl.app.launcher.AppLauncher;
import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.PathUtils;

import java.util.Set;

/**
 * Main Application Controller.
 */
public class AppController extends Application {

    private AppLauncher _launcher;

    public AppController() {
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        //primaryStage.initStyle(StageStyle.TRANSPARENT);

        /*Text text = new Text("Transparent!");
        text.setFont(new Font(40));
        StackPane root = new StackPane();
        //VBox box = new VBox();
        root.getChildren().add(text);
        final Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        scene.setFill(null);*/

        //primaryStage.setScene(scene);

        // primaryStage.show();

        this.startApplication();
    }

    @Override
    public void stop() throws Exception {
        try {
            this.closeApplication();
        } finally {
            super.stop();
        }
    }

    public void log(final Level level, final String msg, final Throwable t) {

    }

    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void closeApplication() {
        if (null != _launcher) {
            _launcher.stop();
            _launcher = null;
        }
    }

    private void startApplication() throws Exception {
        //-- init application launcher and start autorun applications --//
        _launcher = new AppLauncher(this);
    }


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static final AppControllerArgs s_app_args = new AppControllerArgs();
    private static CommandHandler s_handler;

    private static boolean isAlreadyRunning() {
        if (null == s_handler && !CommandSender.ping()) {
            // no instance is running
            try {
                s_handler = new CommandHandler();
                return false;
            } catch (Throwable ignored) {
            }
        }
        return true;
    }


    public static void open() throws Exception {
        //-- check if abother instance is already running and start main --//
        if (!isAlreadyRunning()) {
            AppController.launch(AppController.class);
        }

        //-- parse launch args --//
        s_app_args.parse(DesktopGap.getLauncherRemainArgs());
        final Set<String> files = s_app_args.getFiles();
        if(!files.isEmpty()){
            for(final String file:files){
                Utils.copyToInstallFolder(file, !s_app_args.isRuntime());
            }
        }
    }

    // --------------------------------------------------------------------
    //               p r i v a t e
    // --------------------------------------------------------------------



}

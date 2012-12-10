package org.smartly.application.desktopgap.impl.app.applications;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.autorun.AppAutorunManager;
import org.smartly.application.desktopgap.impl.app.applications.autorun.IAutorunListener;
import org.smartly.application.desktopgap.impl.app.command.CommandHandler;
import org.smartly.application.desktopgap.impl.app.command.CommandSender;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.io.FileObserver;
import org.smartly.commons.io.IFileObserverListener;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Main Application Controller.
 */
public class AppController
        extends Application
        implements IAppInstanceListener, IFileObserverListener, IAutorunListener {

    private static final String AUTORUN_DIR = IDesktopConstants.AUTORUN_DIR;
    private static final String INSTALLED_DIR = IDesktopConstants.INSTALLED_DIR;
    private static final String INSTALL_DIR = IDesktopConstants.INSTALL_DIR;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;
    private static final String APP_EXT = IDesktopConstants.APP_EXT;

    private final AppAutorunManager _autorun;
    private final String _root_install;     // auto-install root
    private final String _root_installed;   // installed apps
    private final String _root_temp;        // temp
    private final Map<String, AppInstance> _registry_running;
    private final Map<String, AppInstance> _registry_installed;
    private FileObserver _installObserver;

    private Text _text;

    public AppController() throws IOException {
        _root_install = Smartly.getAbsolutePath(INSTALL_DIR);
        _root_installed = Smartly.getAbsolutePath(INSTALLED_DIR);
        _root_temp = Smartly.getAbsolutePath(TEMP_DIR);
        _autorun = new AppAutorunManager();
        _registry_running = Collections.synchronizedMap(new HashMap<String, AppInstance>());
        _registry_installed = Collections.synchronizedMap(new HashMap<String, AppInstance>());
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {

        //primaryStage.initStyle(StageStyle.TRANSPARENT);

        /*
        _text = new Text("Transparent!");
        _text.setFont(new Font(40));
        StackPane root = new StackPane();
        //VBox box = new VBox();
        root.getChildren().add(_text);
        final Scene scene = new Scene(root, 400, 400);
        scene.setFill(null);

        primaryStage.setScene(scene);

        primaryStage.show();*/

        this.startApplication();
    }

    @Override
    public void stop() throws Exception {
        try {
            if (null != _installObserver) {
                _installObserver.stopWatching();
            }
        } finally {
            super.stop();
        }
    }


    /**
     * Install and Launch Application
     *
     * @param path Application file path. i.e. "c:\app\my_app.dga", or Application folder path.
     */
    public void launch(final String path) throws IOException {
        if (Utils.isPackage(path)) {
            this.launchPackage(path);
        } else {
            this.launchApp(path);
        }
    }

    // ------------------------------------------------------------------------
    //                      app listener
    // ------------------------------------------------------------------------

    @Override
    public void onOpen(final AppInstance app) {
        synchronized (_registry_running) {
            if (!_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.put(app.getId(), app);
            }
        }
    }

    @Override
    public void onClose(final AppInstance app) {
        synchronized (_registry_running) {
            if (_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.remove(app.getId());
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      IFileObserverListener (install dir)
    // ------------------------------------------------------------------------

    @Override
    public void onEvent(final int event, final String path) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final String clean = PathUtils.toUnixPath(path);
                    if(launchPackage(clean)){
                        // remove package
                        FileUtils.delete(clean);
                    }
                } catch (Throwable t) {
                    log(Level.SEVERE, null, t);
                }
            }
        });
    }

    // --------------------------------------------------------------------
    //               AppAutorunManager.IListener
    // --------------------------------------------------------------------

    @Override
    public void listen(final String appId) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    launchApp(appId);
                } catch (Throwable t) {
                    log(Level.SEVERE, null, t);
                }
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void log(final Level level, final String msg, final Throwable t) {
        if(null!=_text){
            _text.setText(msg);
        }
    }

    private void startApplication() throws IOException {
        //-- ensure for program files folder --//
        FileUtils.mkdirs(_root_install);
        FileUtils.mkdirs(_root_installed);
        FileUtils.mkdirs(_root_temp);

        //-- observer --//
        _installObserver = new FileObserver(_root_install, false, false, FileObserver.EVENT_CREATE, this);
        _installObserver.startWatching();

        //-- scan install folder for existing files --//
        final Set<String> to_install = Utils.getFiles(_root_install);
        for (final String file : to_install) {
            this.installPackage(file);
        }

        //-- scan installed folder and creates registry --//
        final Set<String> installed = Utils.getDirectories(_root_installed);
        for (final String file : installed) {
            final AppInstance app = new AppInstance(this, new AppManifest(file));
            _registry_installed.put(app.getId(), app);
        }

        //-- autorun --//
        _autorun.run(this);

        //-- launch args file --//
        launchArgFiles();
    }

    private AppInstance installPackage(final String packagePath) throws IOException {
        synchronized (_registry_installed) {
            final AppManifest manifest = new AppManifest(packagePath);
            final String appId = manifest.getAppId();
            // check if installed and update or install from scratch
            if (_registry_installed.containsKey(appId)) {
                //-- update --//
                final AppManifest old_manifest = new AppManifest(manifest.getInstallDir());
                if (manifest.isGreaterThan(old_manifest)) {
                    // close existing instance
                    _registry_installed.get(appId).close();
                    // overwrite
                    Utils.install(this, packagePath, manifest.getInstallDir());
                    _registry_installed.put(appId, new AppInstance(this, manifest)); // REGISTER NEW
                } else {
                    // returns existing app instance
                    return _registry_installed.get(appId);
                }

            } else {
                //-- install --//
                Utils.install(this, packagePath, manifest.getInstallDir());
                _registry_installed.put(appId, new AppInstance(this, manifest));  // REGISTER NEW
            }

            return _registry_installed.get(appId);
        }
    }

    /**
     * Apps can be singleton or multiple instance depends on MANIFEST.json file
     *
     * @param packagePath Application Package Path. i.e. "c:/myapp/app.dga"
     */
    private boolean launchPackage(final String packagePath) throws IOException {
        final AppInstance app_instance = this.installPackage(packagePath);

        //-- ready to run app --//
        return this.launchApp(app_instance.getId());
    }

    private boolean launchApp(final String appId) throws IOException {
        if (_registry_installed.containsKey(appId)) {
            _registry_installed.get(appId).open();
            return true;
        }
        return false;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


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

    private static void launchArgFiles() throws IOException {
        //-- parse launch args --//
        s_app_args.parse(DesktopGap.getLauncherRemainArgs());
        final Set<String> files = s_app_args.getFiles();
        if (!files.isEmpty()) {
            for (final String file : files) {
                Utils.copyToInstallFolder(PathUtils.toUnixPath(file), s_app_args.isInstall());
            }
        }
        // LoggingUtils.getLogger(AppController.class).info("FILES: " + files.toString());
    }

    public static void open() throws Exception {
        // LoggingUtils.getLogger(AppController.class).info("OPENING");
        //-- check if abother instance is already running and start main --//
        if (!isAlreadyRunning()) {
            AppController.launch(AppController.class);
        } else {
            launchArgFiles();
        }
    }




}

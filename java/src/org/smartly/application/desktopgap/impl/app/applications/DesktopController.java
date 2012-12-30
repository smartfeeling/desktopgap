package org.smartly.application.desktopgap.impl.app.applications;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.autorun.AppAutorunManager;
import org.smartly.application.desktopgap.impl.app.applications.autorun.IAutorunListener;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameCloseEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameOpenEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.command.CommandHandler;
import org.smartly.application.desktopgap.impl.app.command.CommandSender;
import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.event.Event;
import org.smartly.commons.event.IEventListener;
import org.smartly.commons.io.FileObserver;
import org.smartly.commons.io.IFileObserverListener;
import org.smartly.commons.logging.Level;
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
public class DesktopController
        extends Application
        implements IEventListener, IFileObserverListener, IAutorunListener {

    private static final String AUTORUN_DIR = IDesktopConstants.AUTORUN_DIR;
    private static final String INSTALLED_STORE_DIR = IDesktopConstants.INSTALLED_STORE_DIR;
    private static final String INSTALLED_SYSTEM_DIR = IDesktopConstants.INSTALLED_SYSTEM_DIR;
    private static final String INSTALL_DIR = IDesktopConstants.INSTALL_DIR;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;
    private static final String APP_EXT = IDesktopConstants.APP_EXT;

    private final AppAutorunManager _autorun;
    private final String _root_install;     // auto-install root
    private final String _root_installed_store;   // installed apps
    private final String _root_installed_system;
    private final String _root_temp;        // temp
    private final Map<String, AppInstance> _registry_running;
    private final Map<String, AppInstance> _registry_installed;
    private FileObserver _installObserver;

    private Text _text;

    public DesktopController() throws IOException {
        _root_install = Smartly.getAbsolutePath(INSTALL_DIR);
        _root_installed_store = Smartly.getAbsolutePath(INSTALLED_STORE_DIR);
        _root_installed_system = Smartly.getAbsolutePath(INSTALLED_SYSTEM_DIR);
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
    public AppFrame launch(final String path, final String winId) throws IOException {
        if (Utils.isPackage(path)) {
            return this.launchPackage(path, winId);
        } else {
            return this.launchApp(path, winId);
        }
    }

    // ------------------------------------------------------------------------
    //                      IEventListener
    // ------------------------------------------------------------------------

    @Override
    public void on(final Event event) {
        if (event instanceof FrameCloseEvent) {
            // FRAME CLOSE
            this.handleCloseApp((AppInstance) event.getSender());
        } else if (event instanceof FrameOpenEvent) {
            // FRAME OPEN
            this.handleOpenApp((AppInstance) event.getSender());
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
                    final String clean_path = PathUtils.toUnixPath(path);
                    if (null!=launchPackage(clean_path, null)) {
                        // remove package
                        FileUtils.delete(clean_path);
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
                    launchApp(appId, null);
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
        if (null != _text) {
            _text.setText(msg);
        }
    }

    private void startApplication() throws IOException {
        //-- ensure for program files folder --//
        FileUtils.mkdirs(_root_install);
        FileUtils.mkdirs(_root_installed_store);
        FileUtils.mkdirs(_root_installed_system);
        FileUtils.mkdirs(_root_temp);

        //-- init fonts--//
        DesktopFonts.init();

        //-- observer --//
        _installObserver = new FileObserver(_root_install, false, false, FileObserver.EVENT_CREATE, this);
        _installObserver.startWatching();

        //-- scan install folder for existing files --//
        final Set<String> to_install = Utils.getFiles(_root_install);
        for (final String file : to_install) {
            this.installPackage(file);
        }

        //-- STORE: scan installed folder and creates registry --//
        final Set<String> installed = Utils.getDirectories(_root_installed_store);
        for (final String file : installed) {
            final AppInstance app = this.createApp(new AppManifest(file, false));
            _registry_installed.put(app.getId(), app);
        }

        //-- SYSTEM: scan installed folder and creates registry --//
        final Set<String> installed_sys = Utils.getDirectories(_root_installed_system);
        for (final String file : installed_sys) {
            final AppInstance app = this.createApp(new AppManifest(file, true));
            _registry_installed.put(app.getId(), app);
        }

        //-- autorun --//
        _autorun.run(this);

        //-- launch args file --//
        launchArgFiles();
    }

    private AppInstance installPackage(final String packagePath) throws IOException {
        synchronized (_registry_installed) {
            final AppManifest manifest = new AppManifest(packagePath, false);
            final String appId = manifest.getAppId();
            // check if installed and update or install from scratch
            if (_registry_installed.containsKey(appId)) {
                //-- update --//
                final AppManifest old_manifest = new AppManifest(manifest.getInstallDir(), false);
                if (manifest.isGreaterThan(old_manifest)) {
                    // close existing instance
                    _registry_installed.get(appId).close();
                    // overwrite
                    Utils.install(packagePath, manifest.getInstallDir());
                    _registry_installed.put(appId, this.createApp(manifest)); // REGISTER NEW
                } else {
                    // returns existing app instance
                    return _registry_installed.get(appId);
                }

            } else {
                //-- install --//
                Utils.install(packagePath, manifest.getInstallDir());
                _registry_installed.put(appId, this.createApp(manifest));  // REGISTER NEW
            }

            return _registry_installed.get(appId);
        }
    }

    private AppInstance createApp(final AppManifest manifest) throws IOException {
        final AppInstance app = new AppInstance(this, manifest);
        app.addEventListener(this);
        return app;
    }

    /**
     * Apps can be singleton or multiple instance depends on MANIFEST.json file
     *
     * @param packagePath Application Package Path. i.e. "c:/myapp/app.dga"
     */
    private AppFrame launchPackage(final String packagePath,
                                      final String winId) throws IOException {
        final AppInstance app_instance = this.installPackage(packagePath);

        //-- ready to run app --//
        return this.launchApp(app_instance.getId(), winId);
    }

    private AppFrame launchApp(final String appId,
                                  final String winId) throws IOException {
        if (_registry_installed.containsKey(appId)) {
            return _registry_installed.get(appId).open(winId);
        }
        return null;
    }

    private void handleOpenApp(final AppInstance app) {
        synchronized (_registry_running) {
            if (!_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.put(app.getId(), app);
            }
        }
    }

    private void handleCloseApp(final AppInstance app) {
        synchronized (_registry_running) {
            if (_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.remove(app.getId());
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static final DesktopControllerArgs s_app_args = new DesktopControllerArgs();
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
            DesktopController.launch(DesktopController.class);
        } else {
            launchArgFiles();
        }
    }


}

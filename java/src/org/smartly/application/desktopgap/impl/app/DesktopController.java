package org.smartly.application.desktopgap.impl.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.applications.DesktopControllerApps;
import org.smartly.application.desktopgap.impl.app.applications.autorun.AppAutorunManager;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.server.WebServer;
import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.io.FileObserver;
import org.smartly.commons.io.IFileObserverListener;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main Application Controller.
 */
public final class DesktopController
        extends Application
        implements IFileObserverListener {

    private static final String INSTALLED_STORE_DIR = IDesktopConstants.INSTALLED_STORE_DIR;
    private static final String INSTALLED_SYSTEM_DIR = IDesktopConstants.INSTALLED_SYSTEM_DIR;
    private static final String INSTALL_DIR = IDesktopConstants.INSTALL_DIR;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;

    private final AppAutorunManager _autorun;
    private final String _root_install;     // auto-install root
    private final String _root_installed_store;   // installed apps
    private final String _root_installed_system;
    private final String _root_temp;        // temp
    private final DesktopControllerApps _applications;
    private FileObserver _installObserver;
    private boolean _closed;

    private Text _text;

    public DesktopController() throws IOException {
        _root_install = Smartly.getAbsolutePath(INSTALL_DIR);
        _root_installed_store = Smartly.getAbsolutePath(INSTALLED_STORE_DIR);
        _root_installed_system = Smartly.getAbsolutePath(INSTALLED_SYSTEM_DIR);
        _root_temp = Smartly.getAbsolutePath(TEMP_DIR);
        _applications = new DesktopControllerApps(this);
        _autorun = new AppAutorunManager(_applications);
        _closed = false;

        // inject controller into webserver
        _webserver.setDesktop(this);
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

    /**
     * Stop desktop and close all running applications.
     *
     * @throws Exception
     */
    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    public void close() {
        _closed = true;
        // stop internal webserver
        try {
            WebServer.getInstance().stop();
        } catch (Throwable ignored) {
        }
        try {
            if (null != _installObserver) {
                _installObserver.stopWatching();
            }
        } catch (Throwable ignored) {
        }
        // kill all running applications
        try {
            _applications.killRunning();
        } catch (Throwable ignored) {
        }
        Platform.exit();
    }

    public boolean isClosed() {
        return _closed;
    }

    /**
     * Install and Launch Application
     *
     * @param path Application file path. i.e. "c:\app\my_app.dga", or Application folder path.
     */
    public AppFrame launch(final String path, final String winId) throws IOException {
        if (this.isClosed()) {
            return null;
        }
        if (Utils.isPackage(path)) {
            return this.launchPackage(path, winId);
        } else {
            return this.launchApp(path, winId);
        }
    }

    public Collection<String> getApplicationNames() {
        return _applications.getAppNames();
    }

    public Map<String, List<AppManifest>> getApplicationManifests() {
        return _applications.getAppManifests();
    }

    public JSONObject getApplicationManifestsAsJSON() {
        return _applications.getAppManifestsAsJSON();
    }

    /**
     * Returns Manifest of installed application
     *
     * @param appId AppId
     * @return Manifest
     */
    public AppManifest getApplicationManifest(final String appId) {
        return _applications.getInstalled(appId);
    }

    // ------------------------------------------------------------------------
    //                      IFileObserverListener (install dir)
    // ------------------------------------------------------------------------

    @Override
    public void onEvent(final int event, final String path) {
        if (this.isClosed()) {
            return;
        }
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    final String clean_path = PathUtils.toUnixPath(path);
                    if (null != launchPackage(clean_path, null)) {
                        // remove package
                        FileUtils.delete(clean_path);
                    }
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
            _applications.addInstalled(file, false);
        }

        //-- SYSTEM: scan installed folder and creates registry --//
        final Set<String> installed_sys = Utils.getDirectories(_root_installed_system);
        for (final String file : installed_sys) {
            _applications.addInstalled(file, true);
        }

        //-- autorun --//
        this.initAutorun();

        //-- launch args file --//
        launchArgFiles();
    }

    private void initAutorun(){
        _autorun.onAutorun(new AppAutorunManager.OnAutorun() {
            @Override
            public void handle(final String appId) {
                launchAppLater(appId);
            }
        });
        _autorun.run();
    }

    private AppManifest installPackage(final String packagePath) throws IOException {
        synchronized (_applications) {
            final AppManifest manifest = new AppManifest(packagePath);
            final String appId = manifest.getAppId();
            // check if installed and update or install from scratch
            if (_applications.isInstalled(appId)) {
                //-- update --//
                final AppManifest old_manifest = new AppManifest(manifest.getInstallDir());
                if (manifest.isGreaterThan(old_manifest)) {
                    // close existing instance
                    _applications.closeApplication(appId);
                    // overwrite
                    Utils.install(packagePath, manifest.getInstallDir());
                    _applications.addInstalled(manifest); // REGISTER NEW
                } else {
                    // returns existing app instance
                    return _applications.getInstalled(appId);
                }

            } else {
                //-- install --//
                Utils.install(packagePath, manifest.getInstallDir());
                _applications.addInstalled(manifest);  // REGISTER NEW
            }

            return _applications.getInstalled(appId);
        }
    }

    /**
     * Apps can be singleton or multiple instance depends on MANIFEST.json file
     *
     * @param packagePath Application Package Path. i.e. "c:/myapp/app.dga"
     */
    private AppFrame launchPackage(final String packagePath,
                                   final String winId) throws IOException {
        if (this.isClosed()) {
            return null;
        }
        final AppManifest manifest = this.installPackage(packagePath);

        //-- ready to run app --//
        return this.launchApp(manifest.getAppId(), winId);
    }

    private void launchAppLater(final String appId) {
        if (this.isClosed()) {
            return;
        }
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

    private AppFrame launchApp(final String appId,
                               final String winId) throws IOException {
        if (this.isClosed()) {
            return null;
        }
        if (_applications.isInstalled(appId)) {
            return _applications.getApplication(appId).open(winId);
        }
        return null;
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static final DesktopControllerArgs s_app_args = new DesktopControllerArgs();
    private static WebServer _webserver;

    private static boolean isAlreadyRunning() {
        if (null == _webserver) {
            _webserver = WebServer.getInstance();
            try {
                _webserver.start();
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

    public static void open(final boolean allowMultiple) throws Exception {
        if (!allowMultiple) {
            open();
        } else {
            _webserver = WebServer.getInstance(true);
            _webserver.start();
            DesktopController.launch(DesktopController.class);
        }
    }

}

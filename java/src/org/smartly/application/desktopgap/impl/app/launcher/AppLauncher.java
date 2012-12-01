package org.smartly.application.desktopgap.impl.app.launcher;

import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.AppController;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.launcher.application.AppInstance;
import org.smartly.application.desktopgap.impl.app.launcher.application.AppManifest;
import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.logging.Level;
import org.smartly.commons.io.FileObserver;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class AppLauncher implements IAppInstanceListener {

    private static final String AUTORUN_DIR = IDesktopConstants.AUTORUN_DIR;
    private static final String INSTALLED_DIR = IDesktopConstants.INSTALLED_DIR;
    private static final String INSTALL_DIR = IDesktopConstants.INSTALL_DIR;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;
    private static final String APP_EXT = IDesktopConstants.APP_EXT;

    private final AppController _controller;
    private final AppAutorunManager _autorun;
    private final String _root_install;     // auto-install root
    private final String _root_installed;   // installed apps
    private final String _root_temp;        // temp
    private final Map<String, AppInstance> _registry_running;
    private final Map<String, AppInstance> _registry_installed;
    private FileObserver _installObserver;


    public AppLauncher(final AppController controller) throws IOException {
        _controller = controller;
        _root_install = Smartly.getAbsolutePath(INSTALL_DIR);
        _root_installed = Smartly.getAbsolutePath(INSTALLED_DIR);
        _root_temp = Smartly.getAbsolutePath(TEMP_DIR);
        _autorun = new AppAutorunManager();
        _registry_running = Collections.synchronizedMap(new HashMap<String, AppInstance>());
        _registry_installed = Collections.synchronizedMap(new HashMap<String, AppInstance>());

        this.init();
    }

    public void stop() {
        if (null != _installObserver) {
            _installObserver.stopWatching();
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
    public void open(final AppInstance app) {
        synchronized (_registry_running) {
            if (!_registry_running.containsKey(app.getId())) {
                // register new app instance
                _registry_running.put(app.getId(), app);
            }
        }
    }

    @Override
    public void close(final AppInstance app) {
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

    private void init() throws IOException {
        //-- ensure for program files folder --//
        FileUtils.mkdirs(_root_install);
        FileUtils.mkdirs(_root_installed);
        FileUtils.mkdirs(_root_temp);

        //-- observer --//
        _installObserver = new FileObserver(_root_install, false, false, FileObserver.EVENT_CREATE) {
            @Override
            protected void onEvent(int event, String path) {
                try {
                    final String clean = PathUtils.toUnixPath(path);
                    launchPackage(clean);
                } catch (Throwable t) {
                    _controller.log(Level.SEVERE, null, t);
                }
            }
        };

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
        _autorun.run(new AppAutorunManager.IListener() {
            @Override
            public void listen(final String appId) {
                try {
                    launchApp(appId);
                } catch (Throwable t) {
                    _controller.log(Level.SEVERE, null, t);
                }
            }
        });
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
    private void launchPackage(final String packagePath) throws IOException {
        final AppInstance app_instance = this.installPackage(packagePath);

        //-- ready to run app --//
        this.launchApp(app_instance.getId());
    }

    private void launchApp(final String appId) throws IOException {
        if (_registry_installed.containsKey(appId)) {
            _registry_installed.get(appId).open();
        }
    }


}

package org.smartly.application.desktopgap.impl.app.launcher.application;

import org.smartly.application.desktopgap.impl.app.launcher.IAppInstanceListener;
import org.smartly.commons.util.PathUtils;

import java.io.IOException;

/**
 * Application Wrapper
 */
public class AppInstance {

    private final IAppInstanceListener _listener;
    private final AppManifest _manifest;
    private AppWindow __window;


    public AppInstance(final IAppInstanceListener listener,
                       final AppManifest manifest) throws IOException {
        _listener = listener;
        _manifest = manifest;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("id: ").append(this.getId());
        result.append(", ");
        result.append("name: ").append(this.getName());
        result.append(", ");
        result.append("install_dir: ").append(this.getInstallDir());

        return result.toString();
    }

    public String getAbsolutePath(final String path) {
        if (PathUtils.isAbsolute(path)) {
            return path;
        }
        return PathUtils.merge(this.getInstallDir(), path);
    }

    public AppManifest getManifest() {
        return _manifest;
    }

    public String getId() {
        return _manifest.getAppId();
    }

    public boolean isValid() {
        return _manifest.isValid();
    }

    public String getName() {
        return _manifest.getAppName();
    }

    public String getInstallDir() {
        return _manifest.getInstallDir();
    }

    public void open() {
        this.getWindow().open();
    }

    public void close() {
        this.getWindow().close();
    }

    void stageClosing(){
        _listener.close(this);
    }

    void stageOpening(){
        _listener.open(this);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private AppWindow getWindow() {
        if (null == __window) {
            __window = new AppWindow(this);
        }
        return __window;
    }

}

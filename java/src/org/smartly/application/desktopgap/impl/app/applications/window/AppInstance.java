package org.smartly.application.desktopgap.impl.app.applications.window;

import org.smartly.application.desktopgap.impl.app.applications.AppController;
import org.smartly.application.desktopgap.impl.app.applications.events.AppCloseEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.AppOpenEvent;
import org.smartly.application.desktopgap.impl.app.applications.events.IDesktopGapEvents;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.event.Event;
import org.smartly.commons.event.EventEmitter;
import org.smartly.commons.event.IEventListener;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.PathUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Application Wrapper
 */
public class AppInstance
        extends EventEmitter
        implements IEventListener {

    private final AppController _controller;
    private final AppManifest _manifest;
    private final AppRegistry _registry;
    private final AppWindows _windows; // frames manager
    private final List<AppFrame> _children; // children frames


    public AppInstance(final AppController controller,
                       final AppManifest manifest) throws IOException {
        _controller = controller;
        _manifest = manifest;
        _registry = new AppRegistry(_manifest);
        _windows = new AppWindows(this);
        _windows.addEventListener(this);
        _children = new ArrayList<AppFrame>();

        this.initLogger();
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

    public AppRegistry getRegistry() {
        return _registry;
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

    public AppFrame open(final String winId) {
        return _windows.open(winId);
    }

    public AppInstance open() {
        _windows.open(null); // open main
        return this;
    }

    public void close() {
        _windows.close(null); // close all
    }

    public Logger getLogger() {
        return LoggingUtils.getLogger(this.getId());
    }

    /**
     * Launch another app and returns instance
     *
     * @param appId App identifier
     * @param winId (Optional) window instance identifier (only for multiple instance of same frame)
     * @return AppInstance
     */
    public AppFrame launchApp(final String appId,
                              final String winId,
                              final String title,
                              final boolean isChild) {
        AppFrame child = null;
        try {
            if (null != _controller) {
                child = _controller.launch(appId, winId);
            }
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }

        // add child to internal child list
        if (null != child && isChild) {
            child.setTitle(title);
            if(isChild){
                _children.add(child);
            }
        }

        return child;
    }

    // --------------------------------------------------------------------
    //                      IEventListener
    // --------------------------------------------------------------------

    @Override
    public void on(final Event event) {
        if (event.getName().equalsIgnoreCase(IDesktopGapEvents.FRAME_CLOSE)) {
            // CLOSE
            this.handleCloseFrame((AppFrame) event.getSender());
        } else if (event.getName().equalsIgnoreCase(IDesktopGapEvents.FRAME_OPEN)) {
            // OPEN
            this.handleOpenFrame((AppFrame) event.getSender());
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


    private void initLogger() {
        final String id = _manifest.getAppId();
        final String installDir = _manifest.getInstallDir();
        LoggingRepository.getInstance().setAbsoluteLogFileName(id, PathUtils.concat(installDir, "application.log"));
    }

    private void handleCloseFrame(final AppFrame frame) {
        // set frame properties (auto saved)
        this.getRegistry().setX(frame.getId(), frame.getX());
        this.getRegistry().setY(frame.getId(), frame.getY());
        this.getRegistry().setWidth(frame.getId(), frame.getWidth());
        this.getRegistry().setHeight(frame.getId(), frame.getHeight());

        // close children
        for(final AppFrame child:_children){
           try{
             child.close();
           }catch(Throwable ignored){
           }
        }

        // is last frame?
        if (_windows.isEmpty()) {
            this.emit(new AppCloseEvent(this));
        }
    }

    private void handleOpenFrame(final AppFrame frame) {
        if (frame.isMain()) {
            this.emit(new AppOpenEvent(this));
        }
    }

}

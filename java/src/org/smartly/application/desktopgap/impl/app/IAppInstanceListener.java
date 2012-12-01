package org.smartly.application.desktopgap.impl.app;

import org.smartly.application.desktopgap.impl.app.launcher.window.AppInstance;

/**
 *
 */
public interface IAppInstanceListener {

    void open(AppInstance app);
    void close(AppInstance app);

}

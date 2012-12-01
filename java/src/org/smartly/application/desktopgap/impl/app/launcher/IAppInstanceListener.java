package org.smartly.application.desktopgap.impl.app.launcher;

import org.smartly.application.desktopgap.impl.app.launcher.application.AppInstance;

/**
 *
 */
public interface IAppInstanceListener {

    void open(AppInstance app);
    void close(AppInstance app);

}

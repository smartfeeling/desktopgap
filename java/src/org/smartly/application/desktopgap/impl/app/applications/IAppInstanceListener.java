package org.smartly.application.desktopgap.impl.app.applications;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;

/**
 *
 */
public interface IAppInstanceListener {

    void open(AppInstance app);
    void close(AppInstance app);

}

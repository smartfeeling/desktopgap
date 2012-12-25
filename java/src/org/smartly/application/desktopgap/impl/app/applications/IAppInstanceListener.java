package org.smartly.application.desktopgap.impl.app.applications;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;

/**
 *
 */
public interface IAppInstanceListener {

    void onOpen(AppInstance app);

    void onClose(AppInstance app);

}

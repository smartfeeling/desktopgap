package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.runtime;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.JsonWrapper;

import java.util.Collection;

/**
 * Protected tool.
 * Only system applications can access runtime methods.
 */
public final class ToolRuntime {

    private static final String[] AUTHORIZED = IDesktopConstants.SYS_APPS;

    private final AppInstance _app;
    private final String _appId;

    public ToolRuntime(final AppFrame frame) {
        _app = frame.getApp();
        _appId = frame.getManifest().getAppId();
    }

    // --------------------------------------------------------------------
    //               A P P S
    // --------------------------------------------------------------------

    public boolean hasApps() {
        if (this.isAuthorized()) {
            return _app.getDesktop().getApplicationNames().size() > 0;
        }
        return false;
    }

    public String getAppNames() {
        if (this.isAuthorized()) {
            final Collection<String> names = _app.getDesktop().getApplicationNames();
            return JsonWrapper.toJSONArray(names).toString();
        }
        return "[]";
    }

    // --------------------------------------------------------------------
    //               C O M P I L E R
    // --------------------------------------------------------------------



    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private boolean isAuthorized() {
        return CollectionUtils.contains(AUTHORIZED, _appId);
    }
}

package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.runtime;

import org.json.JSONObject;
import org.smartly.IConstants;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.AbstractTool;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.frame.ToolFrame;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.SystemUtils;

import java.awt.*;
import java.io.File;
import java.util.Collection;

/**
 * Protected tool.
 * Only system applications can access runtime methods.
 */
public final class ToolRuntime extends AbstractTool {

    private static final String[] AUTHORIZED = IDesktopConstants.SYS_APPS;

    private final AppInstance _app;
    private final String _appId;

    public ToolRuntime(final AppFrame frame) {
        super(frame);
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

    public String getGroupedApps() {
        if (this.isAuthorized()) {
            final JSONObject grouped = _app.getDesktop().getApplicationManifestsAsJSON();
            return grouped.toString();
        }
        return "{}";
    }

    public ToolFrame runApp(final String appId) {
        final AppFrame frame = _app.launchApp(appId);
        return new ToolFrame(frame);
    }

    // --------------------------------------------------------------------
    //               S Y S T E M
    // --------------------------------------------------------------------

    public void exit() {
        try {
            if (this.isAuthorized()) {
                _app.close();
                _app.getDesktop().stop();
            }
        } catch (Throwable t) {
            super.getLogger().error(FormatUtils.format("Error exiting: {0}", t), t);
        }
    }

    public void OSShutDown() {
        if (this.isAuthorized()) {
            final String cmd;
            if (SystemUtils.isLinux() || SystemUtils.isMac()) {
                cmd = "shutdown -h now";
            } else {
                cmd = "shutdown -s -t 0"; //"shutdown -s -t 0";
            }
            this.exec(cmd);
            // exit system
            System.exit(0);
        }
    }

    public void openUserFolder() {
        this.openFolder(IConstants.USER_HOME);
    }

    public void openAppFolder() {
        this.openFolder(super.getFrame().getManifest().getAbsoluteAppPath(""));
    }

    public void openStoreFolder() {
        this.openFolder(Smartly.getAbsolutePath(IDesktopConstants.INSTALLED_STORE_DIR));
    }

    public void openFolder(final String path) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(path));
            }
        } catch (Throwable t) {
            super.getLogger().error(FormatUtils.format("Error Opening '{0}' folder: {1}", path, t), t);
        }
    }

    public void exec(final String command) {
        if (this.isAuthorized()) {
            try {
                final Runtime runtime = Runtime.getRuntime();
                final Process proc = runtime.exec(command);
            } catch (Throwable t) {
                super.getLogger().error(FormatUtils.format("Error Executing '{0}' command: {1}", command, t), t);
            }
        }
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

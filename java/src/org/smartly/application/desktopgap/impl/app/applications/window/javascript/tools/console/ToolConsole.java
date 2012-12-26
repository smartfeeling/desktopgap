package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.console;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.logging.LoggingRepository;
import org.smartly.commons.logging.util.LoggingUtils;
import org.smartly.commons.util.PathUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The console object is a helper tool for debug.<br>
 * Each Application creates its own console (singleton in application context).
 */
public class ToolConsole {

    private static final String APP_CONSOLE_ID = "system_console";

    private final AppInstance _app;
    private final String _id;

    private ToolConsole(final AppInstance app) {
        _app = app;
        _id = getConsoleId(app);
        this.initLogger();
    }

    public String getId() {
        return _id;
    }

    /**
     * Open console window
     */
    public void open(){
       _app.launchApp(APP_CONSOLE_ID);
    }

    // --------------------------------------------------------------------
    //                  L O G
    // --------------------------------------------------------------------

    public void log(final Object message) {
        if (null != message) {

            //-- delegate to logger --//
            this.getLogger().log(Level.INFO, this.toString(message));
        }
    }

    public void error(final Object message) {
        if (null != message) {

            //-- delegate to logger --//
            this.getLogger().log(Level.SEVERE, this.toString(message));
        }
    }

    public void warn(final Object message) {
        if (null != message) {

            //-- delegate to logger --//
            this.getLogger().log(Level.WARNING, this.toString(message));
        }
    }

    public void info(final Object message) {
        if (null != message) {

            //-- delegate to logger --//
            this.getLogger().log(Level.INFO, this.toString(message));
        }
    }

    public void debug(final Object message) {
        if (null != message) {

            //-- delegate to logger --//
            this.getLogger().log(Level.FINE, this.toString(message));
        }
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this.getId());
    }

    private void initLogger() {
        final String installDir = _app.getInstallDir();
        LoggingRepository.getInstance().setAbsoluteLogFileName(this.getId(), PathUtils.concat(installDir, "console.log"));
    }

    private String toString(final Object obj){
        if(null!=obj){

            return obj.toString();
        }
        return "";
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static final Map<String, ToolConsole> _repo = Collections.synchronizedMap(
            new HashMap<String, ToolConsole>());

    private static String getConsoleId(final AppInstance app) {
        return "console.".concat(app.getId());
    }

    public static ToolConsole getConsole(final AppInstance app) {
        synchronized (_repo) {
            final String consoleId = getConsoleId(app);
            if (!_repo.containsKey(consoleId)) {
                _repo.put(consoleId, new ToolConsole(app));
            }
            return _repo.get(consoleId);
        }
    }

}

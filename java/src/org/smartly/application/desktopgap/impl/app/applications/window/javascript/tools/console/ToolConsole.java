package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.console;

import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
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
    private final ConsoleMessages _messages;

    private AppFrame _frame;

    private ToolConsole(final AppInstance app) {
        _app = app;
        _id = getConsoleId(app); // multiple instances for same app
        _messages = new ConsoleMessages(this);

        this.initLogger(_id);
    }

    public String getId() {
        return _id;
    }

    /**
     * Wrap all messages into JSON structure
     *
     * @return JSON structure with messages
     */
    public JSONObject getMessages() {
        return _messages.getMessages();
    }

    /**
     * Wrap single message into messages structure
     *
     * @param message Message
     * @return JSON structure with message
     */
    public JSONObject getMessages(final ConsoleMessage message) {
        return _messages.getMessages(message);
    }

    /**
     * Open console window
     */
    public void open() {
        final String title = _app.getManifest().getTitle().concat(" (console)");
        _frame = _app.launchApp(APP_CONSOLE_ID, _id, title, this.getMessages(), true);
    }

    // --------------------------------------------------------------------
    //                  L O G
    // --------------------------------------------------------------------

    public void log(final Object message) {
        if (null != message) {
            final Level level = Level.INFO;
            final ConsoleMessage cm = _messages.put(level, message);
            //-- delegate to logger --//
            this.getLogger().log(level, cm.toString());
            //-- send to frame if any--//
            this.sendToFrame(cm);
        }
    }

    public void error(final Object message) {
        if (null != message) {
            final Level level = Level.SEVERE;
            final ConsoleMessage cm = _messages.put(level, message);
            //-- delegate to logger --//
            this.getLogger().log(level, cm.toString());
            //-- send to frame if any--//
            this.sendToFrame(cm);
        }
    }

    public void warn(final Object message) {
        if (null != message) {
            final Level level = Level.WARNING;
            final ConsoleMessage cm = _messages.put(level, message);
            //-- delegate to logger --//
            this.getLogger().log(level, cm.toString());
            //-- send to frame if any--//
            this.sendToFrame(cm);
        }
    }

    public void info(final Object message) {
        if (null != message) {
            final Level level = Level.INFO;
            final ConsoleMessage cm = _messages.put(level, message);
            //-- delegate to logger --//
            this.getLogger().log(level, cm.toString());
            //-- send to frame if any--//
            this.sendToFrame(cm);
        }
    }

    public void debug(final Object message) {
        if (null != message) {
            final Level level = Level.FINE;
            final ConsoleMessage cm = _messages.put(level, message);
            //-- delegate to logger --//
            this.getLogger().log(level, cm.toString());
            //-- send to frame if any--//
            this.sendToFrame(cm);
        }
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return LoggingUtils.getLogger(this.getId());
    }

    private void initLogger(final String id) {
        final String installDir = _app.getInstallDir();
        final String fileName = "console.log";
        LoggingRepository.getInstance().setAbsoluteLogFileName(id, PathUtils.concat(installDir, fileName));
    }

    private void sendToFrame(final ConsoleMessage message) {
        if (null != _frame) {
            _frame.putArguments(this.getMessages(message));
        }
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

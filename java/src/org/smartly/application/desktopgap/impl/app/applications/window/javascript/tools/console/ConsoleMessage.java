package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.console;

import org.json.JSONObject;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.DateUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.JsonWrapper;

import java.util.Date;

/**
 *
 */
public class ConsoleMessage {

    private static final String PROP_LEVEL = "level";
    private static final String PROP_MESSAGE = "message";
    private static final String PROP_DATE = "date";
    private static final String PROP_TIME = "time";

    private final ToolConsole _console;
    private final Level _level;
    private final String _original;
    private final Date _date;
    private final String _fmt_date;
    private final String _fmt_time;
    private final JsonWrapper _json;

    public ConsoleMessage(final ToolConsole console,
                          final Level level,
                          final Object message) {
        _console = console;
        _level = level;
        _original = toString(message);
        _date = DateUtils.now();
        _fmt_date = FormatUtils.formatDate(_date, DesktopGap.getLocale());
        _fmt_time = FormatUtils.formatDate(_date, FormatUtils.DEFAULT_TIMEFORMAT);
        _json = new JsonWrapper(new JSONObject());

        _json.putSilent(PROP_LEVEL, _level.toString());
        _json.putSilent(PROP_MESSAGE, _original);
        _json.putSilent(PROP_DATE, _fmt_date);
        _json.putSilent(PROP_TIME, _fmt_time);
        _json.putSilent(PROP_MESSAGE, _original);
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append("[").append(_console.getId()).append("]");
        result.append(" ");
        result.append(_original);

        return result.toString();
    }

    public JSONObject toJSON() {
        return _json.getJSONObject();
    }

    public Date getDate() {
        return _date;
    }

    public Level getLevel() {
        return _level;
    }

    public String getMessage() {
        return _original;
    }

    public String message() {
        return this.getMessage();
    }

    public String level() {
        return this.getLevel().toString();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private static String toString(final Object obj) {
        if (null != obj) {

            return obj.toString();
        }
        return "";
    }

}

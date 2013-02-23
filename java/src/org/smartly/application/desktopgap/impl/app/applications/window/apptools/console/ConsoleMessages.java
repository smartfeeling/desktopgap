package org.smartly.application.desktopgap.impl.app.applications.window.apptools.console;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.JsonWrapper;

/**
 *
 */
public final class ConsoleMessages {

    private static final String ALL = "ALL";

    private final ToolConsole _console;
    private final JsonWrapper _messages;

    public ConsoleMessages(final ToolConsole console) {
        _console = console;
        _messages = new JsonWrapper(new JSONObject());
    }

    public ConsoleMessage put(final Level level, final Object message) {
        final ConsoleMessage cm = new ConsoleMessage(_console, level, message);
        this.getList(ALL).put(cm.toJSON());
        this.getList(level.toString()).put(cm.toJSON());
        return cm;
    }

    public JSONObject getMessages() {
        return _messages.getJSONObject();
    }

    public JSONObject getMessages(final ConsoleMessage cm) {
        final JsonWrapper json = new JsonWrapper(new JSONObject());
        json.putSilent(ALL, new JSONArray());
        json.putSilent(cm.level(), new JSONArray());

        json.optJSONArray(ALL).put(cm.toJSON());
        json.optJSONArray(cm.level()).put(cm.toJSON());

        return json.getJSONObject();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JSONArray getList(final String key) {
        if (!_messages.has(key)) {
            _messages.putSilent(key, new JSONArray());
        }
        return _messages.optJSONArray(key);
    }

}

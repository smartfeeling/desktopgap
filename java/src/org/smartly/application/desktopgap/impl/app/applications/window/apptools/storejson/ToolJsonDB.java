package org.smartly.application.desktopgap.impl.app.applications.window.apptools.storejson;

import org.smartly.application.desktopgap.impl.app.applications.events.IDesktopGapEvents;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.commons.Delegates;
import org.smartly.commons.io.jsondb.JsonDB;
import org.smartly.commons.logging.Level;

/**
 * Simple JSON repository.
 */
public class ToolJsonDB
        extends AbstractTool
        implements Delegates.ExceptionCallback {

    public static final String NAME = "jsondb";

    private static final String DB_DIR = "JSON_DB";

    private final String _root;

    public ToolJsonDB(final AppInstance app) {
        super(app);

        _root = app.getAbsolutePath(DB_DIR);
    }

    public String getToolName() {
        return NAME;
    }

    public JsonDB create(final String name) {
        final JsonDB result = new JsonDB(_root);
        return result.open(name);
    }

    @Override
    public void handle(final String message, final Throwable exception) {
        // push event to javascript
        try {
            super.getFrame().scriptTriggerEvent(IDesktopGapEvents.EVENT_ERROR, super.getApp().getErrorsMap().get(exception));
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE, null, exception);
        }
    }
}

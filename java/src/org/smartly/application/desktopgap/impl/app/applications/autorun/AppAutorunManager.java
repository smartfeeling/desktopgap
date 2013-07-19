package org.smartly.application.desktopgap.impl.app.applications.autorun;

import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.applications.DesktopControllerApps;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.commons.Delegates;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Autorun controller
 */
public final class AppAutorunManager {

    private static final String APP_ID = "app_id";

    // --------------------------------------------------------------------
    //               e v e n t s
    // --------------------------------------------------------------------

    public static interface OnAutorun {
        void handle(final String appId);
    }

    private static final Class EVENT_ON_AUTORUN = OnAutorun.class;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private final DesktopControllerApps _applications;
    private final JsonWrapper _data;
    private final Delegates.Handlers _eventHandlers;


    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public AppAutorunManager(final DesktopControllerApps applications) throws IOException {
        final String file = PathUtils.concat(Smartly.getConfigurationPath(), "application/autorun.json");
        _data = new JsonWrapper(FileUtils.readFileToString(new File(file)));
        _applications = applications;
        _eventHandlers = new Delegates.Handlers();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            _eventHandlers.clear();
        } catch (Throwable ignore) {
        }
        super.finalize();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    public void onAutorun(final OnAutorun handler) {
        _eventHandlers.add(handler);
    }

    public void run() throws IOException {
        if (!_data.isEmpty()) {
            final List<Object> list = _data.values();
            for (final Object item : list) {
                this.run(item);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void run(final Object item) throws IOException {
        if (item instanceof JSONObject) {
            final String id = JsonWrapper.getString(item, APP_ID);
            if(_applications.isInstalled(id)){
                this.doAutorun(id);
            } else {
                // used app_name?
                final Collection<String> identifiers = _applications.getAppIdByName(id);
                if(!identifiers.isEmpty()){
                    for(final String appId:identifiers){
                        this.doAutorun(appId);
                    }
                }
            }
        }
    }

    public void doAutorun(final String appId){
        _eventHandlers.triggerAsync(EVENT_ON_AUTORUN, appId);
    }

}

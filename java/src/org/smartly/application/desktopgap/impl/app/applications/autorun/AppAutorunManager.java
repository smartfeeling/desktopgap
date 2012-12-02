package org.smartly.application.desktopgap.impl.app.applications.autorun;

import org.json.JSONObject;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Autorun controller
 */
public final class AppAutorunManager {

    private final JsonWrapper _data;

    public AppAutorunManager() throws IOException {
        final String file = PathUtils.concat(Smartly.getConfigurationPath(), "application/autorun.json");
        _data = new JsonWrapper(FileUtils.readFileToString(new File(file)));
    }

    public void run(final IAutorunListener listener) throws IOException {
        if (!_data.isEmpty()) {
            final List<Object> list = _data.values();
            for (final Object item : list) {
                this.run(listener, item);
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void run(final IAutorunListener listener, final Object item) throws IOException {
        if (null != listener && item instanceof JSONObject) {
            final String name = JsonWrapper.getString((JSONObject) item, "app");
            final AppManifest manifest = new AppManifest(name);
            listener.listen(manifest.getAppId());
        }
    }

}

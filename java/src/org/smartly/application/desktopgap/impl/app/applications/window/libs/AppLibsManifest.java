package org.smartly.application.desktopgap.impl.app.applications.window.libs;

import org.json.JSONArray;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Manifest for Libs
 */
public final class AppLibsManifest {

    private static final String LOAD = "load";
    private static final String REGISTER = "register";

    private final JsonWrapper _manifest;
    private final Set<String> _load;
    private final Set<String> _register;
    private boolean _valid;

    public AppLibsManifest(final String path) {
        _manifest = new JsonWrapper(this.read(path));
        _load = new HashSet<String>();
        _register = new HashSet<String>();

        this.init();
    }

    public boolean isValid() {
        return _load.size()>0 || _register.size()>0;
    }

    public Set<String> getLoad(){
        return _load;
    }

    public Set<String> getRegister(){
        return _register;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String read(final String path) {
        try {
            return FileUtils.readFileToString(new File(path));
        } catch (Throwable ignored) {
        }
        return "";
    }

    private void init() {
        // load
        final JSONArray load_array = _manifest.optJSONArray(LOAD);
        if (null != load_array && load_array.length() > 0) {
            for (int i = 0; i < load_array.length(); i++) {
                final String value = load_array.optString(i);
                _load.add(value);
            }
        }
        // register
        final JSONArray register_array = _manifest.optJSONArray(REGISTER);
        if (null != register_array && register_array.length() > 0) {
            for (int i = 0; i < register_array.length(); i++) {
                final String value = register_array.optString(i);
                _register.add(value);
            }
        }
    }

}

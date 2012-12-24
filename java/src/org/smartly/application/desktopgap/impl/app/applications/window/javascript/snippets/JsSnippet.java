package org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets;

import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.PathUtils;

import java.util.Map;

/**
 * Code snippet helper
 */
public class JsSnippet {

    private static final String _root = PathUtils.getPackagePath(JsSnippet.class);

    private static final String DISPATCH_EVENT = "dispatchEvent.js";

    private JsSnippet() {
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String getScript(final String name, final Map<String, Object> params){
          final String path = PathUtils.concat(_root, name);
        final String text =  ClassLoaderUtils.getResourceAsString(path);
    }

    // --------------------------------------------------------------------
    //                      S T A T I C
    // --------------------------------------------------------------------

    private static JsSnippet __instance;

    private static JsSnippet getInstance() {
        if (null == __instance) {
            __instance = new JsSnippet();
        }
        return __instance;
    }


}

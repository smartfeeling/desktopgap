package org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets;

import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Code snippet helper
 */
public class JsSnippet {

    private static final String _root = PathUtils.getPackagePath(JsSnippet.class);

    private static final String TPL_PREFIX = "[";
    private static final String TPL_SUFFIX = "]";

    //-- script template parameters--//
    private static final String PARAM_EVENT_NAME = "EVENT_NAME";

    //-- script shortcuts --//
    private static final String SCRIPT_DISPATCH_EVENT = "dispatchEvent.js";

    private JsSnippet() {
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String getScript(final String name, final Map<String, Object> params) {
        final String path = PathUtils.concat(_root, name);
        final String text = ClassLoaderUtils.getResourceAsString(path);
        return FormatUtils.formatTemplate(text, TPL_PREFIX, TPL_SUFFIX, params);
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

    public static String getDispatchEvent(final String eventName){
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_EVENT_NAME, eventName);

        return  getInstance().getScript(SCRIPT_DISPATCH_EVENT, params);
    }
}

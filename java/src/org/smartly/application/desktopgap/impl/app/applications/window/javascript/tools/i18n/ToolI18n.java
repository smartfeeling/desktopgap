package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.i18n;

import netscape.javascript.JSObject;
import org.json.JSONObject;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.AppLocalization;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.AbstractTool;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;

/**
 *
 */
public final class ToolI18n extends AbstractTool{

    public static final String NAME = "i18n";

    private static final String LANG_BASE = IDesktopConstants.LANG_BASE;

    private final AppLocalization _i18n;
    private final Logger _logger;
    private String _lang;

    public ToolI18n(final AppInstance app) {
        super(app);
        _i18n = app.getI18n();
        _lang = DesktopGap.getLang();
        _logger = app.getLogger();
    }

    public String getToolName(){
        return NAME;
    }

    public String getLang() {
        return _lang;
    }

    public void setLang(final String lang) {
        _lang = lang;
    }

    /**
     * Return localized resource using default system language.
     *
     * @param name_key Dot separated compound string including dictionary name and resource key.
     *                 i.e. "home.version"
     * @return Localized resource.
     */
    public String get(final String name_key) {
        return this.get(_lang, name_key);
    }

    /**
     * Return localized resource.
     * Accept 2 parameters with different usages.
     * If first parameter is language, second must be a compound
     * String like "dictionary.key" (i.e. "home.version").
     * If first parameter is dictionary name, second must be the key of resource (system language will be used).
     *
     * @param param1 Language, dictionary name or dictionary Object
     * @param param2 resource key or
     *               dot separated compound string including dictionary name and resource key ("home.version")
     * @return Localized resource.
     */
    public String get(final Object param1, final String param2) {
        if (param1 instanceof String) {
            return this.get((String) param1, param2);
        } else if (param1 instanceof JSObject) {
            return this.get((JSObject) param1, param2);
        }
        return "";
    }

    public String get(final String lang, final Object dictionary, final String key) {
        final String result;
        if (dictionary instanceof String) {
            if (!StringUtils.isJSON(dictionary)) {
                result = _i18n.get(StringUtils.hasText(lang) ? lang : _lang,
                        (String) dictionary, key);
            } else {
                // dictionary is JSONObject
                result = this.get(lang, JsonWrapper.wrap((String) dictionary).getJSONObject(), key);
            }
        } else if (dictionary instanceof JSObject) {
            result = this.get(lang, (JSObject) dictionary, key);
        } else {
            result = "";
        }
        return null!=result ? result.trim():"";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private String get(final String param1, final String param2) {
        if (StringUtils.isJSON(param1)) {
            // param1 is json object
            // param2 is property to translate
            return this.get(JsonWrapper.wrap((String) param1).getJSONObject(), param2);
        } else {
            if (param2.contains(".")) {
                // param1 is LANG
                // split dictionaryName and key
                final String[] tokens = StringUtils.split(param2, ".");
                if (tokens.length == 2) {
                    return this.get(param1, tokens[0], tokens[1]);
                }
            }
            // param1 is dictionary name
            return this.get(_lang, param1, param2);
        }
    }

    private String get(final JSObject object, final String param2) {
        return this.get(_lang, object, param2);
    }

    /**
     * @param lang         language code. i.e. "it"
     * @param dictionary   JSON object. i.e. {"description":{"base":"default description", "it":"descr. in italiano"}}
     * @param propertyName name of object property. i.e. "description"
     * @return
     */
    private String get(final String lang, final JSONObject dictionary, final String propertyName) {
        String result = "";
        try {
            final JsonWrapper json = new JsonWrapper(dictionary);
            result = json.deepString(StringUtils.concatDot(propertyName, lang));
            if (!StringUtils.hasText(result)) {
                result = json.deepString(StringUtils.concatDot(propertyName, LANG_BASE));
            }
        } catch (Throwable t) {
            _logger.error(FormatUtils.format("{0}", t));
        }
        return result;
    }

    private String get(final String lang, final JSObject dictionary, final String propertyName) {
        String result = "";
        try {
            final Object key_val = dictionary.getMember(propertyName);
            if (key_val instanceof String) {
                return (String) key_val;
            } else if (key_val instanceof JSObject) {
                result = (String) ((JSObject) key_val).getMember(lang);
                if (!StringUtils.hasText(result)) {
                    result = (String) ((JSObject) key_val).getMember(LANG_BASE);
                }
            }
        } catch (Throwable t) {
            _logger.error(FormatUtils.format("{0}", t));
        }
        return null != result ? result : "";
    }

}

package org.smartly.application.desktopgap.impl.app.applications.window;

import org.smartly.Smartly;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 *
 */
public final class AppLocalization {

    private static final String I18N_DIR = "i18n";
    private static final String EXT = ".properties";
    private static final String DEFAULT = "default";

    private final AppInstance _app;
    private final String _root;
    private final Map<String, Map<String, String>> _dictionaries;

    public AppLocalization(final AppInstance app) {
        _app = app;
        _root = app.getManifest().getAbsoluteAppPath(I18N_DIR);
        _dictionaries = new HashMap<String, Map<String, String>>();

        this.init();
    }

    /**
     * Return dictionary value
     *
     * @param lang           Language
     * @param dictionaryName Dictionary Name
     * @param key            Resource key
     * @return Dictionary resource or empty string. Never Null.
     */
    public String get(final String lang, final String dictionaryName, final String key) {
        final Map<String, String> dictionary = this.getDictionary(lang);
        final String name_key = StringUtils.concatDot(dictionaryName, key);
        final String result;
        if (dictionary.containsKey(name_key)) {
            result = dictionary.get(name_key);
        } else {
            result = this.getDictionary(DEFAULT).get(name_key);
        }
        return null != result ? result : "";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        if (PathUtils.exists(_root)) {
            final List<File> files = new LinkedList<File>();
            FileUtils.listFiles(files, new File(_root));
            this.init(files);
        }
    }

    private void init(final List<File> files) {
        for (final File file : files) {
            this.load(file);
        }
    }

    private boolean isPropertyFile(final String fileName) {
        return PathUtils.getFilenameExtension(fileName, true).equalsIgnoreCase(EXT);
    }

    private boolean isFile(final String resValue) {
        try {
            final File file = new File(PathUtils.concat(_root, resValue));
            return file.isFile() || file.exists();
        } catch (Throwable ignored) {
            return false;
        }
    }

    private String[] splitNameLang(final String fileName) {
        final String name = PathUtils.getFilename(fileName, false);
        final String[] tokens = StringUtils.splitFirst(name, "_");
        if (tokens.length == 2) {
            return new String[]{tokens[0], LocaleUtils.getLanguage(tokens[1])};
        }
        return new String[]{fileName, DEFAULT};
    }

    private void load(final File file) {
        try {
            if (this.isPropertyFile(file.getName())) {
                final String[] name_lang = this.splitNameLang(file.getName());
                final String name = name_lang[0];
                final String lang = name_lang[1];
                final Map<String, String> dictionary = this.getDictionary(lang);
                final Properties props = new Properties();
                props.load(new InputStreamReader(new FileInputStream(file), Smartly.getCharset()));
                final Set<Object> keys = props.keySet();
                for (final Object key : keys) {
                    if (null != key) {
                        try {
                            this.load(dictionary,
                                    name,
                                    key.toString(),
                                    props.getProperty(key.toString()));
                        } catch (Throwable t) {
                            _app.getLogger().log(Level.SEVERE, null, t);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            _app.getLogger().log(Level.SEVERE, null, t);
        }
    }

    private void load(final Map<String, String> lang,
                      final String name,
                      final String key,
                      final String value) throws IOException {
        final String name_key = StringUtils.concatDot(name, key);
        final String res_value;
        if (this.isFile(value)) {
            res_value = FileUtils.readFileToString(new File(PathUtils.concat(_root, value)), Smartly.getCharset());
        } else {
            res_value = value;
        }
        lang.put(name_key, res_value);
    }

    private Map<String, String> getDictionary(final String lang) {
        if (!_dictionaries.containsKey(lang)) {
            _dictionaries.put(lang, new HashMap<String, String>());
        }
        return _dictionaries.get(lang);
    }
}

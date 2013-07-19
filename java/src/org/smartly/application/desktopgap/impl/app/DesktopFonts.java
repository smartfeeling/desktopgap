package org.smartly.application.desktopgap.impl.app;

import javafx.scene.text.Font;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.application.desktopgap.impl.resources.AppResources;
import org.smartly.commons.util.*;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Font loader
 */
public final class DesktopFonts {

    private static final double SIZE = 10.0;
    private static final String PATH_STORE_FONTS = PathUtils.concat(DesktopGap.getStoreDir(), "/fonts");

    private static final String[] FONTS = new String[]{
            "AIRBORNERegular.ttf",
            "Bazar.ttf",
            "OpenSans-Semibold.ttf",
            "Oxygen-Light.ttf",
            "Oxygen-Regular.ttf",
            "Oxygen-Bold.ttf"
    };

    private final Set<String> _fonts;
    private final Set<String> _dgfonts;

    private DesktopFonts() {
        _fonts = new HashSet<String>();
        _dgfonts = new HashSet<String>();
        this.initFonts();
    }

    /**
     * Load Default fonts from AppResource folder
     */
    public void loadDefaultFonts() {
        for (final String font : FONTS) {
            try {
                final String path = AppResources.getFont(font);
                this.loadFont(path);
            } catch (Throwable t) {
                Smartly.getLogger().severe(this, FormatUtils.format("Error loading '{0}': {1}", font, t));
            }
        }
    }

    public void loadStoreFonts() {
        final List<File> fonts = new LinkedList<File>();
        FileUtils.listFiles(fonts, new File(PATH_STORE_FONTS));
        if (!fonts.isEmpty()) {
            for (final File font : fonts) {
                if (StringUtils.hasText(PathUtils.getFilename(font.getAbsolutePath(), false))) {
                    final String path = "file:".concat(font.getAbsolutePath());
                    this.loadFont(path);
                }
            }
        }
    }

    public Font loadFont(final String fontPath) {
        try {
            final Font font = Font.loadFont(fontPath, SIZE);
            final String name = font.getName();
            _fonts.add(name);
            _dgfonts.add(name);
            return font;
        } catch (Throwable t) {
            return null;
        }
    }

    public Font getFont(final String fontName, final double size) {
        if (_fonts.contains(fontName)) {
            final Font font = Font.font(fontName, size);
            return font;
        }
        return Font.getDefault();
    }

    public String[] getInternalFontNames() {
        return _dgfonts.toArray(new String[_dgfonts.size()]);
    }

    public String[] getSystemFontNames() {
        return _dgfonts.toArray(new String[_fonts.size()]);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void initFonts() {
        try {
            final List<String> fonts = Font.getFontNames();
            if (!CollectionUtils.isEmpty(fonts)) {
                _fonts.addAll(fonts);
            }
        } catch (Throwable t) {
            Smartly.getLogger().severe(this, t);
        }
    }

    // --------------------------------------------------------------------
    //                      S T A T I C
    // --------------------------------------------------------------------

    private static DesktopFonts __instance;

    private static DesktopFonts getInstance() {
        if (null == __instance) {
            __instance = new DesktopFonts();
        }
        return __instance;
    }

    public static void init() {
        getInstance().loadDefaultFonts();
        getInstance().loadStoreFonts();
    }

    public static String[] getFontNames() {
        return getInstance().getInternalFontNames();
    }

    public static String[] getAllFontNames() {
        return getInstance().getSystemFontNames();
    }
}

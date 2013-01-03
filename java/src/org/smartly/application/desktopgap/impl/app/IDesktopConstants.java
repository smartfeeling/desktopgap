package org.smartly.application.desktopgap.impl.app;

import org.smartly.commons.util.StringUtils;

/**
 *
 */
public interface IDesktopConstants {

    public static final String VERSION = "0.0.1";

    public static final String ARG_INSTALL = "install"; // install run argument (remove source)

    //-- system apps --//
    public static final String SYS_APP_HOME = "system_home";
    public static final String[] SYS_APPS = new String[]{
            SYS_APP_HOME
    };

    //-- paths --//
    public static final String AUTORUN_DIR = "./app_autorun"; // auto-run folder
    public static final String INSTALLED_STORE_DIR = "./app_installed/store"; // STORE program files
    public static final String INSTALLED_SYSTEM_DIR = "./app_installed/system"; // SYSTEM program files
    public static final String INSTALL_DIR = "./app_install"; // install app and run
    public static final String TEMP_DIR = "./app_tmp";
    public static final String APP_EXT = ".dga";
    public static final String FILE_MANIFEST = "manifest.json";
    public static final String FILE_REGISTRY = "registry.json";
    public static final String PAGE_FRAME = "frame.html";

    //-- frame types --//
    public static final String FRAME_STANDARD = "standard";
    public static final String FRAME_TOOL = "tool";

    //-- button names --//
    public static final String BTN_FULLSCREEN = "fullscreen";
    public static final String BTN_CLOSE = "close";
    public static final String BTN_MAXIMIZE = "maximize";
    public static final String BTN_MINIMIZE = "minimize";

    //-- manifest --//
    public static final String MF_FRAME = "frame";
    public static final String MF_FRAME_SHADOW = StringUtils.concatDot(MF_FRAME, "shadow");
    public static final String MF_FRAME_DRAGGABLE = StringUtils.concatDot(MF_FRAME, "draggable");
    public static final String MF_FRAME_RESIZABLE = StringUtils.concatDot(MF_FRAME, "resizable");
    public static final String MF_FRAME_MAXIMIZED = StringUtils.concatDot(MF_FRAME, "maximized");
    public static final String MF_FRAME_WIDTH = StringUtils.concatDot(MF_FRAME, "width");
    public static final String MF_FRAME_HEIGHT = StringUtils.concatDot(MF_FRAME, "height");
    public static final String MF_FRAME_X = StringUtils.concatDot(MF_FRAME, "x");
    public static final String MF_FRAME_Y = StringUtils.concatDot(MF_FRAME, "y");

    public static final String MF_BUTTONS = "buttons";
    public static final String MF_BUTTONS_CLOSE = StringUtils.concatDot(MF_BUTTONS, BTN_CLOSE);
    public static final String MF_BUTTONS_FULLSCREEN = StringUtils.concatDot(MF_BUTTONS, BTN_FULLSCREEN);
    public static final String MF_BUTTONS_MINIMIZE = StringUtils.concatDot(MF_BUTTONS, BTN_MINIMIZE);
    public static final String MF_BUTTONS_MAXIMIZE = StringUtils.concatDot(MF_BUTTONS, BTN_MAXIMIZE);

    //-- PRE-COMPILE --//
    public static final String PRE_INDEX_PAGE = "[INDEX_PAGE]";

}

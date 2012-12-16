package org.smartly.application.desktopgap.impl.app;

/**
 *
 */
public interface IDesktopConstants {

    public static final String ARG_INSTALL = "install"; // install run argument (remove source)

    public static final String AUTORUN_DIR = "./app_autorun"; // auto-run folder
    public static final String INSTALLED_STORE_DIR = "./app_installed/store"; // STORE program files
    public static final String INSTALLED_SYSTEM_DIR = "./app_installed/system"; // SYSTEM program files
    public static final String INSTALL_DIR = "./app_install"; // install app and run
    public static final String TEMP_DIR = "./app_tmp";
    public static final String APP_EXT = ".dga";
    public static final String MANIFEST = "manifest.json";
    public static final String RUN_PAGE = "frame.html";

    public static final String FRAME_STANDARD = "standard";
    public static final String FRAME_TOOL = "tool";

    //-- PRE-COMPILE --//
    public static final String PRE_INDEX_PAGE = "[INDEX_PAGE]";

}

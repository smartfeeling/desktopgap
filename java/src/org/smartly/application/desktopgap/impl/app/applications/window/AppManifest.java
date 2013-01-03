package org.smartly.application.desktopgap.impl.app.applications.window;

import org.smartly.IConstants;
import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.cryptograph.GUID;
import org.smartly.commons.cryptograph.MD5;
import org.smartly.commons.util.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manifest wrapper
 */
public class AppManifest {

    private static final String MANIFEST = IDesktopConstants.FILE_MANIFEST;
    private static final String PAGE_FRAME = IDesktopConstants.PAGE_FRAME;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;
    private static final String INSTALLED_DIR = IDesktopConstants.INSTALLED_STORE_DIR;
    private static final String APP_DIR = "./app";

    private static final String MF_SYS_ID = "sys_id";
    private static final String MF_NAME = "name";
    private static final String MF_TITLE = "title";
    private static final String MF_VERSION = "version";
    private static final String MF_INDEX = "index";
    private static final String MF_FRAME = IDesktopConstants.MF_FRAME;
    private static final String MF_FRAME_TYPE = StringUtils.concatDot(MF_FRAME, "type");  // standard, tool
    private static final String MF_FRAME_SHADOW = IDesktopConstants.MF_FRAME_SHADOW;
    private static final String MF_FRAME_RESIZABLE = IDesktopConstants.MF_FRAME_RESIZABLE;
    private static final String MF_FRAME_DRAGGABLE = IDesktopConstants.MF_FRAME_DRAGGABLE;
    private static final String MF_FRAME_MAXIMIZED = IDesktopConstants.MF_FRAME_MAXIMIZED;
    private static final String MF_FRAME_WIDTH = IDesktopConstants.MF_FRAME_WIDTH;
    private static final String MF_FRAME_HEIGHT = IDesktopConstants.MF_FRAME_HEIGHT;
    private static final String MF_FRAME_X = IDesktopConstants.MF_FRAME_X;
    private static final String MF_FRAME_Y = IDesktopConstants.MF_FRAME_Y;
    private static final String MF_BUTTONS = IDesktopConstants.MF_BUTTONS;
    private static final String MF_BUTTONS_CLOSE = IDesktopConstants.MF_BUTTONS_CLOSE;
    private static final String MF_BUTTONS_FULLSCREEN = IDesktopConstants.MF_BUTTONS_FULLSCREEN;
    private static final String MF_BUTTONS_MAXIMIZE = IDesktopConstants.MF_BUTTONS_MAXIMIZE;
    private static final String MF_BUTTONS_MINIMIZE = IDesktopConstants.MF_BUTTONS_MINIMIZE;

    private static final String MF_FRAME_STANDARD = IDesktopConstants.FRAME_STANDARD;
    private static final String MF_FRAME_TOOL = IDesktopConstants.FRAME_TOOL;

    private final boolean _is_system;
    private final String _temp_dir;
    private final JsonWrapper _manifest;
    private final String _install_dir;
    private final String _app_docroot;
    private final String _appName;
    private String _install_root; // system or store
    private String _filePath;
    private String _appId;


    /**
     * Generate manifest
     *
     * @param path App Path or Package Name
     * @throws IOException
     */
    public AppManifest(final String path, final boolean system) throws IOException {
        _is_system = system;
        _temp_dir = PathUtils.concat(Smartly.getAbsolutePath(TEMP_DIR), GUID.create(false, true));
        _manifest = this.getManifest(path);
        if (!_manifest.isEmpty()) {
            _appName = _manifest.optString(MF_NAME);
            _install_dir = PathUtils.concat(_install_root, _appName);
            _app_docroot = PathUtils.merge(_install_dir, APP_DIR);
            if (system) {
                _appId = _manifest.optString(MF_SYS_ID, null);
            }
        } else {
            _appName = "";
            _install_dir = "";
            _app_docroot = "";
        }
    }

    public JsonWrapper getJson() {
        return _manifest;
    }

    public boolean isSystem() {
        return _is_system;
    }

    public boolean isValid() {
        return null != _manifest && !_manifest.isEmpty();
    }

    public String getAbsolutePath(final String path) {
        return PathUtils.concat(_install_dir, path);
    }

    public String getAbsoluteAppPath(final String path) {
        if (PathUtils.isHttp(path)) {
            return path;
        }
        return PathUtils.concat(_app_docroot, path);
    }

    public String getAppId() {
        if (!StringUtils.hasText(_appId)) {
            this.generateId();
        }
        return _appId;
    }

    public String getAppName() {
        return _appName;
    }

    public String getInstallDir() {
        return _install_dir;
    }

    public String getAppIndex() {
        return PathUtils.concat(APP_DIR, this.getIndex());
    }

    public String getAbsolutePageFrame() {
        return this.getAbsoluteAppPath(PAGE_FRAME);
    }

    public String getAbsoluteIndex() {
        return this.getAbsoluteAppPath(this.getIndex());
    }

    public boolean isGreaterThan(final AppManifest other) {
        return this.isGreaterThan(other.getVersion());
    }

    public boolean isGreaterThan(final String other_version) {
        return CompareUtils.greater(this.getVersion(), other_version);
    }

    public boolean save() {
        try {
            FileUtils.copy(_manifest.toString().getBytes(), new File(_filePath));
        } catch (Throwable t) {
            return false;
        }
        return true;
    }


    // --------------------------------------------------------------------
    //               P r o p e r t i e s
    // --------------------------------------------------------------------

    public String getIndex() {
        return _manifest.optString(MF_INDEX);
    }

    public void setIndex(final String value) {
        _manifest.putSilent(MF_INDEX, value);
        this.generateId();
        this.save();
    }

    public String getName() {
        return _manifest.optString(MF_NAME);
    }

    public void setName(final String value) {
        _manifest.putSilent(MF_NAME, value);
        this.generateId();
        this.save();
    }

    public String getTitle() {
        return _manifest.optString(MF_TITLE);
    }

    public void setTitle(final String value) {
        _manifest.putSilent(MF_TITLE, value);
        this.generateId();
        this.save();
    }

    public String getVersion() {
        return _manifest.optString(MF_VERSION);
    }

    public void setVersion(final String version) {
        _manifest.putSilent(MF_VERSION, version);
        this.generateId();
        this.save();
    }

    public boolean hasShadow() {
        return _manifest.deepBoolean(MF_FRAME_SHADOW);
    }

    public void setShadow(final boolean value) {
        _manifest.putDeep(MF_FRAME_SHADOW, value);
        this.save();
    }

    public boolean isResizable() {
        return _manifest.deepBoolean(MF_FRAME_RESIZABLE);
    }

    public void setResizable(final boolean value) {
        _manifest.putDeep(MF_FRAME_RESIZABLE, value);
        this.save();
    }

    public boolean isDraggable() {
        return _manifest.deepBoolean(MF_FRAME_DRAGGABLE);
    }

    public void setDraggable(final boolean value) {
        _manifest.putDeep(MF_FRAME_DRAGGABLE, value);
        this.save();
    }

    public String getFrameType() {
        return _manifest.deepString(MF_FRAME_TYPE, MF_FRAME_STANDARD);
    }

    public void setFrameType(final String value) {
        _manifest.putSilent(MF_FRAME_TYPE, value);
        this.save();
    }


    //-- registry --//

    public boolean getMaximized() {
        return _manifest.deepBoolean(MF_FRAME_MAXIMIZED);
    }

    public void setMaximized(final boolean value) {
        _manifest.putDeep(MF_FRAME_MAXIMIZED, value);
        this.save();
    }

    public double getWidth() {
        return _manifest.deepDouble(MF_FRAME_WIDTH);
    }

    public void setWidth(final double value) {
        _manifest.putDeep(MF_FRAME_WIDTH, value);
        this.save();
    }

    public double getHeight() {
        return _manifest.deepDouble(MF_FRAME_HEIGHT);
    }

    public void setHeight(final double value) {
        _manifest.putDeep(MF_FRAME_HEIGHT, value);
        this.save();
    }

    public double getX() {
        return _manifest.deepDouble(MF_FRAME_X);
    }

    public void setX(final double value) {
        _manifest.putDeep(MF_FRAME_X, value);
        this.save();
    }

    public double getY() {
        return _manifest.deepDouble(MF_FRAME_Y);
    }

    public void setY(final double value) {
        _manifest.putDeep(MF_FRAME_Y, value);
        this.save();
    }

    //-- buttons --//

    public Map<String, Boolean> getButtonsMap() {
        final Map<String, Boolean> buttons = new HashMap<String, Boolean>();
        buttons.put(IDesktopConstants.BTN_CLOSE, getButtonClose());
        buttons.put(IDesktopConstants.BTN_FULLSCREEN, getButtonFullscreen());
        buttons.put(IDesktopConstants.BTN_MAXIMIZE, getButtonMaximize());
        buttons.put(IDesktopConstants.BTN_MINIMIZE, getButtonMinimize());

        return buttons;
    }

    public boolean getButtonClose() {
        return _manifest.deepBoolean(MF_BUTTONS_CLOSE);
    }

    public void setButtonClose(final boolean value) {
        _manifest.putSilent(MF_BUTTONS_CLOSE, value);
        this.save();
    }

    public boolean getButtonFullscreen() {
        return _manifest.deepBoolean(MF_BUTTONS_FULLSCREEN);
    }

    public void setButtonFullscreen(final boolean value) {
        _manifest.putSilent(MF_BUTTONS_FULLSCREEN, value);
        this.save();
    }

    public boolean getButtonMaximize() {
        return _manifest.deepBoolean(MF_BUTTONS_MAXIMIZE);
    }

    public void setButtonMaximize(final boolean value) {
        _manifest.putSilent(MF_BUTTONS_MAXIMIZE, value);
        this.generateId();
        this.save();
    }

    public boolean getButtonMinimize() {
        return _manifest.deepBoolean(MF_BUTTONS_MINIMIZE);
    }

    public void setButtonsMinimize(final boolean value) {
        _manifest.putSilent(MF_BUTTONS_MINIMIZE, value);
        this.generateId();
        this.save();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private JsonWrapper getManifest(final String path) throws IOException {
        _install_root = Smartly.getAbsolutePath(INSTALLED_DIR); // default
        final String manifestJson;
        if (Utils.isPackage(path)) {
            // PACKAGE
            // unzip temp
            ZipUtils.unzip(path, _temp_dir);
            _filePath = PathUtils.concat(_temp_dir, MANIFEST);
            manifestJson = FileUtils.readFileToString(new File(_filePath));
            // remove temp
            FileUtils.delete(_temp_dir);
        } else if (Utils.isManifest(path)) {
            // MANIFEST FILE
            _filePath = path;
            manifestJson = FileUtils.readFileToString(new File(_filePath));
            _install_root = PathUtils.getParent(PathUtils.getParent(_filePath)); // overwrite default
        } else if (Utils.isAppFolder(path)) {
            // APP FOLDER
            _filePath = PathUtils.concat(path, MANIFEST);
            manifestJson = FileUtils.readFileToString(new File(_filePath));
            _install_root = PathUtils.getParent(path);  // overwrite default
        } else if (path.indexOf(IConstants.FOLDER_SEPARATOR) > 0) {
            _filePath = PathUtils.concat(path, MANIFEST);
            manifestJson = FileUtils.readFileToString(new File(_filePath));
            _install_root = PathUtils.getParent(path);  // overwrite default
        } else {
            // INVALID PATH
            _filePath = "";
            manifestJson = "{}";
        }
        return new JsonWrapper(manifestJson);
    }

    private void generateId() {
        final String index = _manifest.optString(MF_INDEX);
        final String name = _manifest.optString(MF_NAME);
        final String title = _manifest.optString(MF_TITLE);
        final String version = _manifest.optString(MF_VERSION);
        _appId = MD5.encode(StringUtils.concatDot(index, name, title, version));
    }


}

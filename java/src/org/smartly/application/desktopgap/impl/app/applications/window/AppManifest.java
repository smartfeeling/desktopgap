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

/**
 * Manifest wrapper
 */
public class AppManifest {

    private static final String MANIFEST = IDesktopConstants.MANIFEST;
    private static final String PAGE_FRAME = IDesktopConstants.PAGE_FRAME;
    private static final String TEMP_DIR = IDesktopConstants.TEMP_DIR;
    private static final String INSTALLED_DIR = IDesktopConstants.INSTALLED_STORE_DIR;
    private static final String APP_DIR = "./app";

    private static final String MF_SYS_ID = "sys_id";
    private static final String MF_NAME = "name";
    private static final String MF_TITLE = "title";
    private static final String MF_VERSION = "version";
    private static final String MF_RESIZABLE = "resizable";
    private static final String MF_INDEX = "index";
    private static final String MF_FRAME = "frame";  // standard, tool
    private static final String MF_STAGE = "stage";
    private static final String MF_STAGE_STYLE = StringUtils.concatDot(MF_STAGE, "style");
    private static final String MF_REGISTRY = "registry";
    private static final String MF_REGISTRY_WIDTH = StringUtils.concatDot(MF_REGISTRY, "width");
    private static final String MF_REGISTRY_HEIGHT = StringUtils.concatDot(MF_REGISTRY, "height");
    private static final String MF_REGISTRY_X = StringUtils.concatDot(MF_REGISTRY, "x");
    private static final String MF_REGISTRY_Y = StringUtils.concatDot(MF_REGISTRY, "y");

    private static final String MF_FRAME_STANDARD = IDesktopConstants.FRAME_STANDARD;
    private static final String MF_FRAME_TOOL = IDesktopConstants.FRAME_TOOL;


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

    public boolean isValid() {
        return null != _manifest && !_manifest.isEmpty();
    }

    public String getAbsolutePath(final String path) {
        return PathUtils.concat(_install_dir, path);
    }

    public String getAbsoluteAppPath(final String path) {
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

    public boolean isResizable() {
        return _manifest.optBoolean(MF_RESIZABLE);
    }

    public void setResizable(final boolean value) {
        _manifest.putSilent(MF_RESIZABLE, value);
        this.generateId();
        this.save();
    }

    public String getFrame() {
        return _manifest.optString(MF_FRAME, MF_FRAME_STANDARD);
    }

    public void setFrame(final String value) {
        _manifest.putSilent(MF_FRAME, value);
        this.generateId();
        this.save();
    }

    public String getIndex() {
        return _manifest.optString(MF_INDEX);
    }

    public void setIndex(final String value) {
        _manifest.putSilent(MF_INDEX, value);
        this.generateId();
        this.save();
    }

    //-- stage --//

    public String getStyle() {
        return _manifest.deepString(MF_STAGE_STYLE);
    }

    public void setStyle(final String value) {
        _manifest.putDeep(MF_STAGE_STYLE, value);
        this.save();
    }

    //-- registry --//

    public double getWidth() {
        return _manifest.deepDouble(MF_REGISTRY_WIDTH);
    }

    public void setWidth(final double value) {
        _manifest.putDeep(MF_REGISTRY_WIDTH, value);
        this.save();
    }

    public double getHeight() {
        return _manifest.deepDouble(MF_REGISTRY_HEIGHT);
    }

    public void setHeight(final double value) {
        _manifest.putDeep(MF_REGISTRY_HEIGHT, value);
        this.save();
    }

    public double getX() {
        return _manifest.deepDouble(MF_REGISTRY_X);
    }

    public void setX(final double value) {
        _manifest.putDeep(MF_REGISTRY_X, value);
        this.save();
    }

    public double getY() {
        return _manifest.deepDouble(MF_REGISTRY_Y);
    }

    public void setY(final double value) {
        _manifest.putDeep(MF_REGISTRY_Y, value);
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
        final String name = _manifest.optString(MF_NAME);
        final String title = _manifest.optString(MF_TITLE);
        final String version = _manifest.optString(MF_VERSION);
        _appId = MD5.encode(StringUtils.concatDot(name, title, version));
    }

}

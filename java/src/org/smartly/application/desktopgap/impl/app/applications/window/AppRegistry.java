package org.smartly.application.desktopgap.impl.app.applications.window;

import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;
import org.smartly.commons.util.StringUtils;

import java.io.File;

/**
 * App Registry is a persistent repository.
 */
public final class AppRegistry {

    private static final String FILE_NAME = IDesktopConstants.FILE_REGISTRY;
    private static final String MF_FRAME = IDesktopConstants.MF_FRAME;
    private static final String MF_FRAME_MAXIMIZED = IDesktopConstants.MF_FRAME_MAXIMIZED;
    private static final String MF_FRAME_WIDTH = IDesktopConstants.MF_FRAME_WIDTH;
    private static final String MF_FRAME_HEIGHT = IDesktopConstants.MF_FRAME_HEIGHT;
    private static final String MF_FRAME_X = IDesktopConstants.MF_FRAME_X;
    private static final String MF_FRAME_Y = IDesktopConstants.MF_FRAME_Y;

    private final AppManifest _manifest;
    private final String _appId;
    private final JsonWrapper _repository;
    private final String _file_name;

    private boolean _loadedFromFile;

    public AppRegistry(final AppManifest manifest) {
        _manifest = manifest;
        _appId = manifest.getAppId();
        _repository = new JsonWrapper(new JSONObject());
        _file_name = PathUtils.concat(manifest.getInstallDir(), FILE_NAME);

        this.init(manifest);
    }

    /**
     * Returns true is registry has been loaded from existing file.
     *
     * @return Boolean
     */
    public boolean isLoadedFromFile() {
        return _loadedFromFile;
    }

    public void set(final String key, final Object value){
        if(StringUtils.hasText(key) && null!=value){
            _repository.putDeep(key, value);
            this.save();
        }
    }

    public Object get(final String key){
        if(StringUtils.hasText(key)){
            return _repository.deep(key);
        }
        return null;
    }

    public Object remove(final String key){
        final Object result;
        if(StringUtils.hasText(key)){
            result = _repository.deepRemove(key);
            this.save();
        } else {
            result = null;
        }

        return result;
    }

    public boolean getMaximized(final String winId) {
        if (!_repository.has(StringUtils.concatDot(winId, MF_FRAME_MAXIMIZED))) {
            return _repository.deepBoolean(StringUtils.concatDot(_appId, MF_FRAME_MAXIMIZED));
        }
        return _repository.deepBoolean(StringUtils.concatDot(winId, MF_FRAME_MAXIMIZED));
    }

    public void setMaximized(final String winId, final boolean value) {
        _repository.putDeep(StringUtils.concatDot(winId, MF_FRAME_MAXIMIZED), value);
        this.save();
    }

    public double getWidth(final String winId) {
        if (_manifest.isResizable()) {
            if (!_repository.has(StringUtils.concatDot(winId, MF_FRAME_WIDTH))) {
                return _repository.deepDouble(StringUtils.concatDot(_appId, MF_FRAME_WIDTH));
            }
            return _repository.deepDouble(StringUtils.concatDot(winId, MF_FRAME_WIDTH));
        }
        return _manifest.getWidth();
    }

    public void setWidth(final String winId, final double value) {
        _repository.putDeep(StringUtils.concatDot(winId, MF_FRAME_WIDTH), value);
        this.save();
    }

    public double getHeight(final String winId) {
        if (_manifest.isResizable()) {
            if (!_repository.has(StringUtils.concatDot(winId, MF_FRAME_HEIGHT))) {
                return _repository.deepDouble(StringUtils.concatDot(_appId, MF_FRAME_HEIGHT));
            }
            return _repository.deepDouble(StringUtils.concatDot(winId, MF_FRAME_HEIGHT));
        }
        return _manifest.getHeight();
    }

    public void setHeight(final String winId, final double value) {
        _repository.putDeep(StringUtils.concatDot(winId, MF_FRAME_HEIGHT), value);
        this.save();
    }

    public double getX(final String winId) {
        if (_manifest.isDraggable()) {
            if (!_repository.has(StringUtils.concatDot(winId, MF_FRAME_X))) {
                return _repository.deepDouble(StringUtils.concatDot(_appId, MF_FRAME_X));
            }
            return _repository.deepDouble(StringUtils.concatDot(winId, MF_FRAME_X));
        }
        return _manifest.getX();
    }

    public void setX(final String winId, final double value) {
        _repository.putDeep(StringUtils.concatDot(winId, MF_FRAME_X), value);
        this.save();
    }

    public double getY(final String winId) {
        if (_manifest.isDraggable()) {
            if (!_repository.has(StringUtils.concatDot(winId, MF_FRAME_Y))) {
                return _repository.deepDouble(StringUtils.concatDot(_appId, MF_FRAME_Y));
            }
            return _repository.deepDouble(StringUtils.concatDot(winId, MF_FRAME_Y));
        }
        return _manifest.getY();
    }

    public void setY(final String winId, final double value) {
        _repository.putDeep(StringUtils.concatDot(winId, MF_FRAME_Y), value);
        this.save();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final AppManifest manifest) {
        //-- load content from disk --//
        final String content = this.read();
        if (null != content) {
            // parse existing
            _repository.parse(content);
            _loadedFromFile = true;
        } else {
            // creates new
            this.setMaximized(_appId, manifest.getMaximized());
            this.setHeight(_appId, manifest.getHeight());
            this.setWidth(_appId, manifest.getWidth());
            this.setX(_appId, manifest.getX());
            this.setY(_appId, manifest.getY());
            _loadedFromFile = false;
        }
    }

    private String read() {
        try {
            if (PathUtils.exists(_file_name)) {
                return FileUtils.readFileToString(new File(_file_name));
            }
        } catch (Throwable t) {

        }
        return null;
    }

    private boolean save() {
        try {
            FileUtils.copy(_repository.toString().getBytes(), new File(_file_name));
        } catch (Throwable t) {
            return false;
        }
        return true;
    }
}

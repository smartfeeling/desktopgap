package org.smartly.application.desktopgap.impl.app.applications.window;

import org.json.JSONObject;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.PathUtils;

import java.io.File;

/**
 * App Registry is a persistent repository.
 */
public class AppRegistry {

    private static final String FILE_NAME = IDesktopConstants.FILE_REGISTRY;
    private static final String MF_FRAME = IDesktopConstants.MF_FRAME;
    private static final String MF_FRAME_WIDTH = IDesktopConstants.MF_FRAME_WIDTH;
    private static final String MF_FRAME_HEIGHT = IDesktopConstants.MF_FRAME_HEIGHT;
    private static final String MF_FRAME_X = IDesktopConstants.MF_FRAME_X;
    private static final String MF_FRAME_Y = IDesktopConstants.MF_FRAME_Y;

    private final String _root;
    private final JsonWrapper _repository;
    private final String _file_name;

    public AppRegistry(final AppManifest manifest) {
        _root = manifest.getInstallDir();
        _repository = new JsonWrapper(new JSONObject());
        _file_name = PathUtils.concat(_root, FILE_NAME);

        this.init(manifest);
    }

    public double getWidth() {
        return _repository.deepDouble(MF_FRAME_WIDTH);
    }

    public void setWidth(final double value) {
        _repository.putDeep(MF_FRAME_WIDTH, value);
        this.save();
    }

    public double getHeight() {
        return _repository.deepDouble(MF_FRAME_HEIGHT);
    }

    public void setHeight(final double value) {
        _repository.putDeep(MF_FRAME_HEIGHT, value);
        this.save();
    }

    public double getX() {
        return _repository.deepDouble(MF_FRAME_X);
    }

    public void setX(final double value) {
        _repository.putDeep(MF_FRAME_X, value);
        this.save();
    }

    public double getY() {
        return _repository.deepDouble(MF_FRAME_Y);
    }

    public void setY(final double value) {
        _repository.putDeep(MF_FRAME_Y, value);
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
        } else {
            // creates new
            this.setHeight(manifest.getHeight());
            this.setWidth(manifest.getWidth());
            this.setX(manifest.getX());
            this.setY(manifest.getY());
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

package org.smartly.application.desktopgap.impl.app.applications.window.apptools.file;

import org.smartly.commons.util.JsonWrapper;

/**
 * This object is used to supply arguments to the DirectoryEntry getFile and getDirectory methods, which look up or create files and directories, respectively.
 * <p/>
 * Properties
 * <p/>
 * create: Used to indicate that the file or directory should be created, if it does not exist. (boolean)
 * exclusive: By itself, exclusive has no effect. Used with create, it causes the file or directory creation to fail if the target path already exists. (boolean)
 * <h2>Example:</h2>
 * <code>
 * // Get the data directory, creating it if it doesn't exist.<br>
 * dataDir = fileSystem.root.getDirectory("data", {create: true}); <br>
 * </code>
 * <p/>
 * <code>
 * // Create the lock file, if and only if it doesn't exist.<br>
 * lockFile = dataDir.getFile("lockfile.txt", {create: true, exclusive: true});<br>
 * </code>
 */
public class Flags {

    private static final String CREATE = "create";
    private static final String EXCLUSIVE = "exclusive";

    private boolean _create;
    private boolean _exclusive;

    public Flags() {
    }

    public Flags(final String json) {

    }

    public boolean isCreate() {
        return _create;
    }

    public void setCreate(boolean value) {
        _create = value;
    }

    public boolean isExclusive() {
        return _exclusive;
    }

    public void setExclusive(boolean value) {
        _exclusive = value;
    }

    public void create(final boolean value) {
        _create = value;
    }

    public boolean create() {
        return _create;
    }

    public boolean exclusive() {
        return _exclusive;
    }

    public void exclusive(boolean value) {
        _exclusive = value;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init(final String json) {
        final JsonWrapper jsonWrapper = new JsonWrapper(json);
        _create = jsonWrapper.optBoolean(CREATE);
        _exclusive = jsonWrapper.optBoolean(EXCLUSIVE);
    }
}

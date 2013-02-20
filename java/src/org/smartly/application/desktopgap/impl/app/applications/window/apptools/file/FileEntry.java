package org.smartly.application.desktopgap.impl.app.applications.window.apptools.file;

/**
 * This object represents a file on a file system. It is defined in the W3C Directories and Systems specification.
 * Properties
 * <p/>
 * isFile: Always true. (boolean)
 * isDirectory: Always false. (boolean)
 * name: The name of the FileEntry, excluding the path leading to it. (DOMString)
 * fullPath: The full absolute path from the root to the FileEntry. (DOMString)
 *
 * NOTE: The following attributes are defined by the W3C specification, but are not supported:
 * <p/>
 * filesystem: The file system on which the FileEntry resides. (FileSystem)
 *
 * Methods
 * <p/>
 * getMetadata: Look up metadata about a file.
 * setMetadata: Set metadata on a file.
 * moveTo: Move a file to a different location on the file system.
 * copyTo: Copy a file to a different location on the file system.
 * toURL: Return a URL that can be used to locate a file.
 * remove: Delete a file.
 * getParent: Look up the parent directory.
 * createWriter: Creates a FileWriter object that can be used to write to a file.
 * file: Creates a File object containing file properties.
 */
public class FileEntry {

    public FileEntry() {

    }

    public boolean isFile(){
        return true;
    }

    public boolean isDirectory(){
        return false;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

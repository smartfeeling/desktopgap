package org.smartly.application.desktopgap.impl.app.applications.window.apptools.file;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.CollectionUtils;
import org.smartly.commons.util.PathUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
public class ToolFileUtils
        extends AbstractTool {

    public static final String NAME = "fileUtils";

    // --------------------------------------------------------------------
    //               c o n s t r u c t o r
    // --------------------------------------------------------------------

    public ToolFileUtils(final AppInstance app) {
        super(app);
    }

    public String getToolName() {
        return NAME;
    }

    // --------------------------------------------------------------------
    //               p u b l i c
    // --------------------------------------------------------------------

    public String select() {
        try {
            final List<File> files = openFileChooser(super.getApp().getDesktop().getStage(), PathUtils.USER_HOME);
            if (!CollectionUtils.isEmpty(files)) {
                final Set<String> names = new HashSet<String>();
                for (final File file : files) {
                    names.add(file.getAbsolutePath());
                }
                final JSONArray json = new JSONArray(names);
                return json.toString();
            }
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE, null, t);
        }
        return "";
    }

    public String selectDir() {
        try {
            final File dir = openDirectoryChooser(super.getApp().getDesktop().getStage(), PathUtils.USER_HOME);
            if (null != dir) {
                return dir.getAbsolutePath();
            }
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE, null, t);
        }
        return "";
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    private static List<File> openFileChooser(final Stage stage,
                                              final String startPath) {
        FileChooser chooser = new FileChooser();

        chooser.setInitialDirectory(new File(startPath));

        //Set extension filter
        //FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("All files (*.*)", "*.*");
        //fileChooser.getExtensionFilters().add(extFilter);

        //Show open file dialog
        return chooser.showOpenMultipleDialog(stage);
    }

    private static File openDirectoryChooser(final Stage stage,
                                             final String startPath) {
        DirectoryChooser chooser = new DirectoryChooser();

        chooser.setInitialDirectory(new File(startPath));

        //Show open file dialog
        return chooser.showDialog(stage);
    }

}

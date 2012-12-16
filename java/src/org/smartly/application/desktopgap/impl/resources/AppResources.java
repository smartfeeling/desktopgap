package org.smartly.application.desktopgap.impl.resources;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppManifest;
import org.smartly.commons.io.repository.deploy.FileDeployer;
import org.smartly.commons.util.PathUtils;

import java.io.InputStream;

/**
 *
 */
public final class AppResources {

    private static final String FRAME_STANDARD = IDesktopConstants.FRAME_STANDARD;
    private static final String FRAME_TOOL = IDesktopConstants.FRAME_STANDARD;

    private static final String PATH_FRAMES = "/frames";
    private static final String PATH_FRAMES_FRAME_STANDARD = PathUtils.concat(PATH_FRAMES, FRAME_STANDARD);
    private static final String PATH_FRAMES_FRAME_TOOL = PathUtils.concat(PATH_FRAMES, FRAME_TOOL);
    private static final String PATH_APP_TEMPLATE = "/app_template";
    private static final String PATH_BLANK = "/blank";



    private AppResources() {
    }

    public InputStream getResourceAsStream(final String resourceName) {
        return this.getClass().getResourceAsStream(resourceName);
    }

    public String toExternalForm(final String resourceName) {
        return this.getClass().getResource(resourceName).toExternalForm();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // --------------------------------------------------------------------
    //                      S T A T I C
    // --------------------------------------------------------------------

    private static AppResources __instance;

    private static AppResources getInstance() {
        return __instance;
    }

    //-- PAGES --//

    public static String getPageUri_BLANK() {
        return getInstance().toExternalForm(PathUtils.concat(PATH_BLANK, "blank.html"));
    }

    //-- TEMPLATE --//

    public static String getAppTemplateUri(final String resource) {
        return getInstance().toExternalForm(PathUtils.concat(PATH_APP_TEMPLATE, resource));
    }

    public static InputStream getAppTemplateIcon() {
        return getInstance().getResourceAsStream(PathUtils.concat(PATH_APP_TEMPLATE, "/icon.png"));
    }

    //-- DEPLOYERS --//

    public static void deploy_FRAMES(final String frame,
                                     final String target) {
        final String source = PathUtils.concat(PATH_FRAMES, frame);
        final FileDeployer deployer = new FileDeployer(source, target, true, false, false, false) {
            @Override
            public byte[] compress(byte[] data, String filename) {
                return null;
            }
            @Override
            public byte[] compile(byte[] data, String filename) {
                return null;
            }
        };
        deployer.setOverwrite(true);
        deployer.getSettings().clear();
        deployer.getSettings().getExcludeFiles().add(".class");
        deployer.deployChildren();
    }
}

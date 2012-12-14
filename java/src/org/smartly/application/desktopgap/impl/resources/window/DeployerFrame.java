package org.smartly.application.desktopgap.impl.resources.window;

import org.smartly.commons.io.repository.deploy.FileDeployer;
import org.smartly.packages.htmlparser.impl.HtmlParser;

/**
 *
 */
public final class DeployerFrame extends FileDeployer {

    public DeployerFrame(final String targetFolder,
                         final boolean silent) {
        super("", targetFolder,
                silent, false, false, false);
        super.setOverwrite(true);

        // reset settings
        super.getSettings().clear();
    }


    @Override
    public byte[] compress(byte[] data, String filename) {
        return null;
    }

    @Override
    public byte[] compile(byte[] data, String filename) {
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    public static String wrap(final String html, final String baseUri) {
        //-- deploy frame --//
        final DeployerFrame deployer = new DeployerFrame(baseUri, true);
        deployer.deploy();

        //-- wrap frame --//
        final HtmlParser parser = new HtmlParser(html, baseUri);

        return parser.getDocument().outerHtml();
    }

}

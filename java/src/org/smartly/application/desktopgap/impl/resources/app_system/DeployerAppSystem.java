package org.smartly.application.desktopgap.impl.resources.app_system;

import org.smartly.commons.io.repository.deploy.FileDeployer;

/**
 *
 */
public class DeployerAppSystem extends FileDeployer  {

    public DeployerAppSystem(final String targetFolder, final boolean silent) {
        super("", targetFolder,
                silent, false, false, false);
        super.setOverwrite(false);
    }

    @Override
    public byte[] compile(final byte[] data, final String filename) {
        return data;
    }

    @Override
    public byte[] compress(final byte[] data, final String filename) {
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

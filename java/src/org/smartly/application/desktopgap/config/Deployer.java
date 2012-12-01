package org.smartly.application.desktopgap.config;

import org.smartly.commons.io.repository.deploy.FileDeployer;

public class Deployer extends FileDeployer {

    public Deployer(final String targetFolder, final boolean silent) {
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
}


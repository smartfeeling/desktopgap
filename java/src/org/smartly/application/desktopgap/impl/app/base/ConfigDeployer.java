package org.smartly.application.desktopgap.impl.app.base;

import org.smartly.Smartly;
import org.smartly.commons.io.repository.deploy.FileDeployer;

/**
 * Deploy Configuration files Overwriting existing by default.
 *
 */
public class ConfigDeployer
        extends FileDeployer {

    private static final String CONFIG_DIR = Smartly.getConfigurationPath();
    private static final boolean SILENT = Smartly.isSilent();

    public ConfigDeployer() {
        this(true);
    }

    public ConfigDeployer(final boolean overwrite) {
        super("", CONFIG_DIR,
                SILENT, false, false, false);
        super.setOverwrite(overwrite);
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

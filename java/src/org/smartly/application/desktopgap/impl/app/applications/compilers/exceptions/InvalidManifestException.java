package org.smartly.application.desktopgap.impl.app.applications.compilers.exceptions;

import org.smartly.commons.util.FormatUtils;

/**
 *
 */
public class InvalidManifestException extends Exception {

    public InvalidManifestException(final String manifestPath) {
         super(FormatUtils.format("Invalid Manifest: '{0}'", manifestPath));
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

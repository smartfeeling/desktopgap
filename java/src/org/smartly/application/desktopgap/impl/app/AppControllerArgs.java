package org.smartly.application.desktopgap.impl.app;

import org.smartly.application.desktopgap.impl.app.utils.Utils;
import org.smartly.commons.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Start args parser
 */
public final class AppControllerArgs {

    private static final String ARG_RUNTIME = IDesktopConstants.ARG_RUNTIME;

    private final Set<String> _files;
    private boolean _runtime;

    public AppControllerArgs() {
        _runtime = false;
        _files = new HashSet<String>();
    }

    public void parse(final String[] args){
        if (!CollectionUtils.isEmpty(args)) {
            //-- move packages to install folder --//
            for (final String arg : args) {
                if (ARG_RUNTIME.equalsIgnoreCase(arg)) {
                    _runtime = true;
                } else if (Utils.isPackage(arg)) {
                    _files.add(arg);
                }
            }
        }
    }

    public boolean isRuntime() {
        return _runtime;
    }

    public Set<String> getFiles() {
        return _files;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

package org.smartly.application.desktopgap.impl.app.base;

import org.smartly.Smartly;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.compilers.AppCompiler;
import org.smartly.commons.io.repository.deploy.FileDeployer;
import org.smartly.commons.lang.compilers.CompilerRegistry;
import org.smartly.commons.lang.compilers.ICompiler;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.FormatUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.packages.htmldeployer.impl.compilers.CompilerLess;
import org.smartly.packages.velocity.impl.compilers.CompilerVelocity;

import java.util.HashMap;
import java.util.Map;

/**
 * Deploy HTML5 application into runtime store (app_store or system_store)
 */
public abstract class StoreDeployer
        extends FileDeployer {

    public static final String STORE_DIR = Smartly.getAbsolutePath(IDesktopConstants.INSTALLED_STORE_DIR);
    private static final boolean SILENT = Smartly.isSilent();

    public final Map<String, Object> _context;

    public StoreDeployer() {
        super("", STORE_DIR,
                SILENT, false, false, false);
        super.setOverwrite(true);

        _context = new HashMap<String, Object>();

        this.init();
    }

    @Override
    public void deploy() {
        //-- deploy all files --//
        super.deploy();

        //-- "make" deployed files if needed --//
        try {
            AppCompiler.make(super.getTargetFolder());
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    @Override
    public byte[] compile(final byte[] data, final String filename) {
        try {
            final String ext = PathUtils.getFilenameExtension(filename, true);
            final ICompiler compiler = CompilerRegistry.get(ext);
            if (null != compiler) {
                _context.put(CompilerVelocity.ARG_FILE, filename);
                return compiler.compile(data, _context);
            } else {
                super.getLogger().log(Level.FINE,
                        FormatUtils.format("COMPILER NOT FOUND FOR '{0}'", filename));
            }
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE,
                    FormatUtils.format("ERROR COMPILING '{0}': {1}", filename, t), t);
        }
        return null;
    }

    @Override
    public byte[] compress(final byte[] data, final String filename) {
        return null;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // pre-process
        this.getSettings().getPreProcessorFiles().add(".less");
        this.getSettings().getPreProcessorFiles().add(".js");
        this.getSettings().getPreProcessorFiles().add(".css");
        this.getSettings().getPreProcessorFiles().add(".html");
        // compile
        this.getSettings().getCompileFiles().put(".less", ".css");    // less-js compiler
        this.getSettings().getCompileFiles().put(".js", ".js");       // closure compiler
        this.getSettings().getCompileFiles().put(".html", ".html");   // template compiler
        // compress
        this.getSettings().getCompressFiles().add(".js");
        this.getSettings().getCompressFiles().add(".css");

        //-- add compilers --//
        CompilerRegistry.register(".less", CompilerLess.class);
        CompilerRegistry.register(".html", CompilerVelocity.class);
    }

}

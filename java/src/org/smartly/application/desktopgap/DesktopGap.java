package org.smartly.application.desktopgap;

import org.smartly.Smartly;
import org.smartly.application.desktopgap.config.Deployer;
import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.DesktopController;
import org.smartly.application.desktopgap.impl.app.applications.compilers.AppCompiler;
import org.smartly.application.desktopgap.impl.app_store.DeployerAppStore;
import org.smartly.application.desktopgap.impl.app_system.DeployerAppSystem;
import org.smartly.commons.logging.Level;
import org.smartly.commons.util.LocaleUtils;
import org.smartly.packages.AbstractPackage;

import java.util.Locale;
import java.util.Map;

/**
 * DesktopGAp main package class
 */
public class DesktopGap extends AbstractPackage {

    public static final String NAME = "DESKTOP_GAP";

    public DesktopGap() {
        super(NAME, 100);
        super.setDescription("Desktop Gap");
        super.setMaintainerName("Gian Angelo Geminiani");
        super.setMaintainerMail("angelo.geminiani@gmail.com");
        super.setMaintainerUrl("http://www.smartfeeling.org");

        //-- module dependencies --//
        /*super.addDependency(SmartlyHtmlDeployer.NAME, "");
        super.addDependency(SmartlyHttp.NAME, "");
        super.addDependency(SmartlyHttpCms.NAME, "");
        super.addDependency(SmartlyMail.NAME, "");
        super.addDependency(SmartlyMongo.NAME, "");
        super.addDependency(SmartlyRemoting.NAME, "");
        super.addDependency(SmartlyVelocity.NAME, ""); // all versions
        super.addDependency(SmartlyHtmlAsset.NAME, "");
        */

        //-- lib dependencies --//
        /*
        super.addDependency("org.eclipse.jetty.aggregate:jetty-all:8.1.4.v20120524", "");
        super.addDependency("com.sun.mail:javax.mail:1.4.5", "");
        super.addDependency("org.mongodb:mongo-java-driver:2.7.3", "");
        super.addDependency("org.apache.velocity:velocity:1.7", "");*/
    }

    @Override
    public void load() {
        //-- register configuration deployer --//
        Smartly.register(new Deployer(Smartly.getConfigurationPath(), Smartly.isSilent()));
    }

    @Override
    public void ready() {
        this.init();
        try {
            DesktopController.open();
        } catch (Throwable t) {
            super.getLogger().log(Level.SEVERE, null, t);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void init() {
        // deploy html files
        this.deploySysApps();
        this.deployDefaultStoreApps();

        this.initDictionary();

        // init velocity tools
        this.initVelocity();
    }

    private void deploySysApps() {
        final String sysDir = Smartly.getAbsolutePath(IDesktopConstants.INSTALLED_SYSTEM_DIR);
        //-- deploy system applications --//
        final DeployerAppSystem deployer = new DeployerAppSystem(sysDir, true);
        deployer.deploy();

        //-- make system applications (creates run.html page) --//
        try {
            AppCompiler.make(sysDir);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }

    private void deployDefaultStoreApps() {
        final String storeDir = Smartly.getAbsolutePath(IDesktopConstants.INSTALLED_STORE_DIR);
        //-- deploy system applications --//
        final DeployerAppStore deployer = new DeployerAppStore(storeDir, true);
        deployer.deploy();

        //-- make system applications (creates run.html page) --//
        try {
            AppCompiler.make(storeDir);
        } catch (Throwable t) {
            this.getLogger().log(Level.SEVERE, null, t);
        }
    }


    private void initDictionary() {
        /**/
        // DictionaryRegistry.register(i18nWidgets.class);

    }

    private void initVelocity() {
        // App
        //VLCManager.getInstance().getToolbox().replace(AppTool.NAME, AppTool.class, null, true);

        // Db
        //VLCManager.getInstance().getToolbox().replace(DbTool.NAME, DbTool.class, null, true);

        // System
        //VLCManager.getInstance().getToolbox().replace(SysTool.NAME, SysTool.class, null, true);

        // Dictionary
        //VLCManager.getInstance().getToolbox().replace(DicTool.NAME, DicTool.class, null, true);
    }


    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    /**
     * Returns port for internal communication
     *
     * @return internal socket port
     */
    public static int getPort() {
        return Smartly.getConfiguration().getInt("application.desktopgap.port");
    }

    public static Map<String, Object> getLauncherArgs() {
        return Smartly.getLauncherArgs();
    }

    public static String[] getLauncherRemainArgs() {
        return Smartly.getLauncherRemainArgs();
    }

    public static String getLang() {
        final Locale locale = getLocale();
        return locale.getLanguage();
    }

    public static Locale getLocale() {
        return LocaleUtils.getCurrent();
    }
}

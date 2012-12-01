package org.smartly.application.desktopgap.launcher;

import org.smartly.application.desktopgap.DesktopGap;
import org.smartly.launcher.SmartlyLauncher;
import org.smartly.packages.SmartlyPackageLoader;


public class Main extends SmartlyLauncher {

    public Main(final String[] args) {
        super(args);
    }

    @Override
    protected void onLoadPackage(final SmartlyPackageLoader loader) {
        //-- main package --//
        loader.register(new DesktopGap());

        //-- system packages --//
        /*loader.register(new SmartlyVelocity());
        loader.register(new SmartlyRemoting());
        loader.register(new SmartlyHttp());
        loader.register(new SmartlyHttpCms());
        loader.register(new SmartlyHtmlDeployer());
        loader.register(new SmartlyMail());
        loader.register(new SmartlyMongo());
        loader.register(new SmartlyHtmlAsset());*/
    }

    // ------------------------------------------------------------------------
    //                      S T A T I C
    // ------------------------------------------------------------------------

    /**
     * Launcher main method.
     * Use this only for debug or testing purpose
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final Main main = new Main(args);
        main.run();
    }


}

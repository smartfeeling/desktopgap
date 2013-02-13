package org.smartly.application.desktopgap.impl.app.applications.window.libs;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.AppBridge;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.AbstractTool;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.launcher.SmartlyClassLoader;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Load custom libraries and expose objects to javascript
 */
public class AppLibs {

    private final AppInstance _app;
    private final AppBridge _bridge;
    private final String _path_manifest;
    private final AppLibsManifest _manifest;

    public AppLibs(final AppInstance app, final AppBridge bridge) {
        _app = app;
        _bridge = bridge;
        _path_manifest = PathUtils.concat(app.getManifest().getPathLibs(), IDesktopConstants.FILE_MANIFEST);
        _manifest = new AppLibsManifest(_path_manifest);

        this.init();
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Logger getLogger() {
        return _app.getLogger();
    }

    private void init() {
        if (_manifest.isValid()) {
            final URL[] urls = this.getUrls(_manifest.getLoad());
            this.registerTools(this.getClassLoader(urls), _manifest.getRegister());
        }
    }

    private URL[] getUrls(final Set<String> items){
        final List<URL> urls = new LinkedList<URL>();
        final List<File> files = new LinkedList<File>();
        // retrieve files to load
        for(final String jarFile:items){
           FileUtils.listFiles(files, (new File(_path_manifest)).getParentFile(), jarFile);
        }
        for(final File file:files){
            try{
                urls.add(file.toURI().toURL());
            }catch(Throwable t){
                this.getLogger().log(Level.SEVERE, null, t);
            }
        }

        return urls.toArray(new URL[urls.size()]);
    }

    private ClassLoader getClassLoader(final URL[] urls){
        final ClassLoader cl = new SmartlyClassLoader(urls, this.getClass().getClassLoader());
        return cl;
    }

    private void registerTools(final ClassLoader classLoader, final Set<String> items){
        for(final String className:items){
            try{
                final Class aclass = ClassLoaderUtils.forName(className, classLoader);
                final AbstractTool instance = (AbstractTool)ClassLoaderUtils.newInstance(aclass, new Object[]{_app});
                if(null!=instance){
                     _bridge.register(instance.getToolName(), instance);
                }
            }catch(Throwable t){
                this.getLogger().log(Level.SEVERE, null, t);
            }
        }
    }

    // --------------------------------------------------------------------
    //               S T A T I C
    // --------------------------------------------------------------------

    public static AppLibs register(final AppInstance app, final AppBridge bridge){
        final AppLibs instance = new AppLibs(app, bridge);
        return instance;
    }
}

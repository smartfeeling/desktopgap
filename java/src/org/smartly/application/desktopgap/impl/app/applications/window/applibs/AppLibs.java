package org.smartly.application.desktopgap.impl.app.applications.window.applibs;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.appbridge.AppBridge;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppBridgeFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.commons.logging.Level;
import org.smartly.commons.logging.Logger;
import org.smartly.commons.util.ClassLoaderUtils;
import org.smartly.commons.util.FileUtils;
import org.smartly.commons.util.PathUtils;
import org.smartly.launcher.SmartlyClassLoader;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.HashSet;
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
    private final Set<Constructor> _frameTools;

    public AppLibs(final AppInstance app,
                      final AppBridge bridge) {
        _app = app;
        _bridge = bridge;
        _path_manifest = PathUtils.concat(app.getManifest().getPathLibs(), IDesktopConstants.FILE_MANIFEST);
        _manifest = new AppLibsManifest(_path_manifest);
        _frameTools = new HashSet<Constructor>();

        this.init();
    }

    protected AppLibs(final AppLibs parent,
                      final AppBridgeFrame bridge) {
        _app = parent._app;
        _path_manifest = parent._path_manifest; // PathUtils.concat(_app.getManifest().getPathLibs(), IDesktopConstants.FILE_MANIFEST);
        _manifest = parent._manifest; // new AppLibsManifest(_path_manifest);
        _frameTools = parent._frameTools; //new HashSet<Constructor>();
        _bridge = bridge;
    }


    // ------------------------------------------------------------------------
    //                      p r o t e c t e d
    // ------------------------------------------------------------------------

    protected Logger getLogger() {
        return _app.getLogger();
    }

    protected Set<Constructor> getFrameTools(){
        return _frameTools;
    }

    protected void registerTool(final AbstractTool instance){
        if(null!=instance){
            _bridge.register(instance.getToolName(), instance);
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

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
        return new SmartlyClassLoader(urls, this.getClass().getClassLoader());
    }

    private void registerTools(final ClassLoader classLoader, final Set<String> items){
        for(final String className:items){
            try{
                final Class aclass = ClassLoaderUtils.forName(className, classLoader);
                Constructor constructor = this.getAppConstructor(aclass);
                if(null!=constructor){
                    final AbstractTool instance = (AbstractTool)constructor.newInstance(new Object[]{_app});
                    this.registerTool(instance);
                } else {
                    constructor = this.getFrameConstructor(aclass);
                    if(null!=constructor){
                        _frameTools.add(constructor);
                    }
                }
                //final AbstractTool instance = (AbstractTool)ClassLoaderUtils.newInstance(aclass, new Object[]{_app});
                //this.registerTool(instance);
            }catch(Throwable t){
                this.getLogger().log(Level.SEVERE, null, t);
            }
        }
    }



    private Constructor getAppConstructor(final Class aclass){
        try{
            return aclass.getConstructor(_app.getClass());
        } catch(Throwable ignored){
        }
        return null;
    }

    private Constructor getFrameConstructor(final Class aclass){
        try{
            return aclass.getConstructor(AppFrame.class);
        } catch(Throwable ignored){
        }
        return null;
    }

}

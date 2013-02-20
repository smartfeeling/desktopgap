package org.smartly.application.desktopgap.impl.app.applications.window.frame;

import org.smartly.application.desktopgap.impl.app.applications.window.applibs.AppLibs;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.commons.logging.Level;

import java.lang.reflect.Constructor;
import java.util.Set;

/**
 *
 */
public class AppLibsFrame extends AppLibs {

    private final Set<Constructor> _frameTools;

    public AppLibsFrame(final  AppLibs parent,
                         final AppBridgeFrame bridge) {
        super(parent, bridge);

        _frameTools = super.getFrameTools();
    }

    public void registerFrameTools(final AppFrame frame){
        if(!_frameTools.isEmpty()){
            for(final Constructor ctr:_frameTools){
                try{
                    final AbstractTool instance = (AbstractTool)ctr.newInstance(new Object[]{frame});
                    super.registerTool(instance);
                }catch(Throwable t){
                    super.getLogger().log(Level.SEVERE, null, t);
                }
            }
        }
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

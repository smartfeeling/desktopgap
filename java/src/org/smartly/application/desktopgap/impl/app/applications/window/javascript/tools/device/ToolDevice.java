package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.device;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.JsEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.commons.util.BeanUtils;

/**
 * The device object describes the device's hardware and software
 * <p/>
 * Properties
 * <p/>
 * <ul>
 *     <li>device.name</li>
 *     <li>device.platform</li>
 *     <li>device.uuid</li>
 *     <li>device.version</li>
 * </ul>
 * Variable Scope
 * <p/>
 * Since device is assigned to the window object, it is implicitly in the global scope.
 * <p/>
 * // These reference the same `device`
 * var phoneName = window.device.name;
 * var phoneName = device.name;
 */
public class ToolDevice {

    public static final String NAME = "device";

    public ToolDevice() {

    }

    public Object get(final String property){
       return BeanUtils.getValueIfAny(this, property, JsEngine.UNDEFINED);
    }

    public String getName(){
        return "";
    }

    public String getPlatform(){
        return "";
    }

    public String getUuid(){
        return "";
    }

    public String getVersion(){
        return "";
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

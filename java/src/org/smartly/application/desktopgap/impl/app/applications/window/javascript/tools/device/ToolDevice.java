package org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.device;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.JsEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.snippets.JsSnippet;
import org.smartly.commons.network.NetworkUtils;
import org.smartly.commons.util.BeanUtils;
import org.smartly.commons.util.SystemUtils;

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
public final class ToolDevice {

    public static final String NAME = "device";

    private final String _os_name;
    private final String _os_version;
    private final String _os_arch;
    private final String _machine_id;
    
    public ToolDevice() {
        _os_name = SystemUtils.getOperatingSystem();
        _os_version = SystemUtils.getOSVersion();
        _os_arch = SystemUtils.getOSAchitecture();
        _machine_id = NetworkUtils.getHostVirtualMachineId();
    }

    public Object get(final String property){
       return BeanUtils.getValueIfAny(this, property, JsEngine.UNDEFINED);
    }

    public String getName(){
        return _os_name;
    }

    public String getPlatform(){
        return _os_arch;
    }

    public String getUuid(){
        return _machine_id;
    }

    public String getVersion(){
        return _os_version;
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

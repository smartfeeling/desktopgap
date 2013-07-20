package org.smartly.application.desktopgap.impl.app.applications.window.apptools.device;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.jfx.JfxJsEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.commons.network.NetworkUtils;
import org.smartly.commons.util.BeanUtils;
import org.smartly.commons.util.ConversionUtils;
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
public final class ToolDevice extends AbstractTool {

    public static final String NAME = "device";

    private final String _os_name;
    private final String _os_version;
    private final String _os_arch;
    private final String _machine_id;
    
    public ToolDevice(final AppInstance app) {
        super(app);
        _os_name = SystemUtils.getOperatingSystem();
        _os_version = SystemUtils.getOSVersion();
        _os_arch = SystemUtils.getOSAchitecture();
        _machine_id = NetworkUtils.getHostVirtualMachineId();
    }

    public Object get(final String property){
       return BeanUtils.getValueIfAny(this, property, JfxJsEngine.UNDEFINED);
    }

    public String getToolName(){
        return NAME;
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

    // --------------------------------------------------------------------
    //               P R O C E S S O R S
    // --------------------------------------------------------------------

    public int countProcessors(){
        return Runtime.getRuntime().availableProcessors();
    }

    // --------------------------------------------------------------------
    //               M E M O R Y
    // --------------------------------------------------------------------

    /* Total amount of free memory available to the JVM */
    public double getFreeMemory(){
        return ConversionUtils.bytesToMbyte(this.getFreeMemoryBytes());
    }

    public long getFreeMemoryBytes(){
        final long bytes = Runtime.getRuntime().freeMemory();
        return bytes;
    }

    /* Total memory currently in use by the JVM */
    public double getTotalMemory(){
        return ConversionUtils.bytesToMbyte(this.getTotalMemoryBytes());
    }

    public long getTotalMemoryBytes(){
        final long bytes = Runtime.getRuntime().totalMemory();
        return bytes;
    }

    /* Maximum amount of memory the JVM will attempt to use */
    public double getMaxMemory(){
        return ConversionUtils.bytesToMbyte(this.getMaxMemoryBytes());
    }

    public long getMaxMemoryBytes(){
        final long bytes = Runtime.getRuntime().maxMemory();
        return bytes;
    }

    public double getUsedMemory(){
        return ConversionUtils.bytesToMbyte(this.getUsedMemoryBytes());
    }

    public long getUsedMemoryBytes(){
        final long free_bytes = this.getFreeMemoryBytes();
        final long total_bytes = this.getTotalMemoryBytes();
        return total_bytes - free_bytes;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

package org.smartly.sample;

import org.smartly.application.desktopgap.impl.app.applications.window.AppInstance;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.tools.AbstractTool;

/**
 *
 */
public class SampleTool extends AbstractTool {

    public SampleTool(final AppInstance app) {
        super(app);
    }

    public String sayHello(){
        return "HELLO!";
    }

    public String getToolName(){
        return "sample";
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

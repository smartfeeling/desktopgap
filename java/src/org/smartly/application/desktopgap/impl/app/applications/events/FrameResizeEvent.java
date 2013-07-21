package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.event.Event;

/**
 * Frame "resize" event
 */
public class FrameResizeEvent extends Event {

    public static final String NAME = IDesktopGapEvents.FRAME_RESIZE;

    private static final String FLD_WIDTH = IDesktopConstants.WIDTH;
    private static final String FLD_HEIGHT = IDesktopConstants.HEIGHT;

    
    public FrameResizeEvent(final Object sender, final double width, final double height) {
        super(sender, NAME);
        this.setWidth(width);
        this.setHeight(height);
    }

    public void setWidth(final double value){
       super.put(FLD_WIDTH, value);
    }

    public double getWidth(){
        return super.getInt(FLD_WIDTH);
    }

    public void setHeight(final double value){
        super.put(FLD_HEIGHT, value);
    }

    public double getHeight(){
        return super.getInt(FLD_HEIGHT);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

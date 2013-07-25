package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.event.Event;
import org.smartly.commons.io.serialization.json.JsonSerializer;
import org.smartly.commons.util.JsonWrapper;
import org.smartly.commons.util.StringUtils;

/**
 * Frame "DragOver" event
 */
public class FrameDragEvent extends Event {

    public static final String NAME = IDesktopGapEvents.FRAME_DRAG_DROPPED;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private static final String FLD_X = "X";
    private static final String FLD_Y = "Y";
    private static final String FLD_SCREEN_X = "screenX";
    private static final String FLD_SCREEN_Y = "screenY";
    private static final String FLD_SCENE_X = "sceneX";
    private static final String FLD_SCENE_Y = "sceneY";
    private static final String FLD_MODE = "mode";
    private static final String FLD_SOURCE = "source";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FrameDragEvent(final Object sender,
                          final double x,
                          final double y,
                          final double screenX,
                          final double screenY,
                          final double sceneX,
                          final double sceneY,
                          final String mode,
                          final Object source) {
        super(sender, NAME);
        super.put(FLD_X, x);
        super.put(FLD_Y, y);
        super.put(FLD_SCREEN_X, screenX);
        super.put(FLD_SCREEN_Y, screenY);
        super.put(FLD_SCENE_X, sceneX);
        super.put(FLD_SCENE_Y, sceneY);
        super.put(FLD_MODE, mode);

        this.setSource(source);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    // ------------------------------------------------------------------------
    //                      p u b l i c
    // ------------------------------------------------------------------------

    @Override
    public AppFrame getSender() {
        return (AppFrame) super.getSender();
    }

    public final double getX() {
        return super.getDouble(FLD_X);
    }

    public final double getY() {
        return super.getDouble(FLD_Y);
    }

    public final double getScreenX() {
        return super.getDouble(FLD_SCREEN_X);
    }

    public final double getScreenY() {
        return super.getDouble(FLD_SCREEN_Y);
    }

    public final double getSceneX() {
        return super.getDouble(FLD_SCENE_X);
    }

    public final double getSceneY() {
        return super.getDouble(FLD_SCENE_Y);
    }

    public final String getMode() {
        return super.getString(FLD_MODE);
    }

    public final Object getSource() {
        return super.get(FLD_SOURCE);
    }

    public void setSource(final Object source) {
        if (null != source) {
            super.put(FLD_SOURCE, this.serialize(source));
        }
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private Object serialize(final Object item) {
        final String serialized = JsonSerializer.serialize(item);
        if (StringUtils.isJSONObject(serialized)) {
            final JsonWrapper jw = new JsonWrapper(serialized);
            final Object value = jw.get("jvalue");
            if(StringUtils.isJSON(value)){
                final JsonWrapper jsonValue = new JsonWrapper((String)value);
                return jsonValue.getObject();
            }
        }
        return serialized;
    }

}

package org.smartly.application.desktopgap.impl.app.applications.events;

import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.commons.event.Event;

/**
 * Frame "KeyPressed" event
 */
public class FrameKeyPressedEvent extends Event {

    public static final String NAME = IDesktopGapEvents.FRAME_KEY_PRESSED;

    // ------------------------------------------------------------------------
    //                      f i e l d s
    // ------------------------------------------------------------------------

    private static final String FLD_CHARACTER = "character";
    private static final String FLD_TEXT = "text";
    private static final String FLD_KEY_CODE = "keyCode";
    private static final String FLD_SHIFT = "shiftDown";
    private static final String FLD_CTRL = "controlDown";
    private static final String FLD_ALT = "altDown";

    // ------------------------------------------------------------------------
    //                      c o n s t r u c t o r
    // ------------------------------------------------------------------------

    public FrameKeyPressedEvent(final Object sender,
                                final String character,
                                final String text,
                                final String keyCode,
                                final boolean shiftDown,
                                final boolean ctrlDown,
                                final boolean altDown) {
        super(sender, NAME);
        super.put(FLD_CHARACTER, character);
        super.put(FLD_TEXT, text);
        super.put(FLD_KEY_CODE, keyCode);
        super.put(FLD_SHIFT, shiftDown);
        super.put(FLD_CTRL, ctrlDown);
        super.put(FLD_ALT, altDown);
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

    public final String getCharacter() {
        return super.getString(FLD_CHARACTER);
    }

    public final String getText() {
        return super.getString(FLD_TEXT);
    }

    public final String getKeyCode() {
        return super.getString(FLD_KEY_CODE);
    }

    public final boolean isShiftDown() {
        return super.getBoolean(FLD_SHIFT);
    }

    public final boolean isControlDown() {
        return super.getBoolean(FLD_CTRL);
    }

    public final boolean isAltDown() {
        return super.getBoolean(FLD_ALT);
    }


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

}

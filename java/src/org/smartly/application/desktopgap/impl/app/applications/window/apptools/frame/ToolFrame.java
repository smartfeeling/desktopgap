package org.smartly.application.desktopgap.impl.app.applications.window.apptools.frame;

import org.smartly.application.desktopgap.impl.app.IDesktopConstants;
import org.smartly.application.desktopgap.impl.app.applications.window.frame.AppFrame;
import org.smartly.application.desktopgap.impl.app.applications.window.javascript.JsEngine;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.application.desktopgap.impl.app.applications.window.apptools.AbstractTool;
import org.smartly.commons.util.ConversionUtils;

/**
 *
 */
public final class ToolFrame extends AbstractTool {

    public static final String NAME = "frame";

    private static final String BUTTON_CLOSE = IDesktopConstants.BTN_CLOSE;
    private static final String BUTTON_MINIMIZE = IDesktopConstants.BTN_MINIMIZE;
    private static final String BUTTON_MAXIMIZE = IDesktopConstants.BTN_MAXIMIZE;
    private static final String BUTTON_FULLSCREEN = IDesktopConstants.BTN_FULLSCREEN;

    private static final String UNDEFINED = JsEngine.UNDEFINED;

    private final AppFrame _frame;

    public ToolFrame(final AppFrame frame) {
        super(frame);
        _frame = frame;
    }

    public String getToolName(){
        return NAME;
    }

    public void buttonClicked(final Object name) {
        if (name instanceof String) {
            final String button_name = (String) name;
            if (BUTTON_CLOSE.equalsIgnoreCase(button_name)) {
                _frame.close();
            } else if (BUTTON_MINIMIZE.equalsIgnoreCase(button_name)) {
                _frame.minimize();
            } else if (BUTTON_MAXIMIZE.equalsIgnoreCase(button_name)) {
                _frame.maximize();
            }
        }
    }

    public void setArea(final Object name,
                        final Object left, final Object top, final Object right, final Object height) {
        if (name instanceof String && !name.toString().equalsIgnoreCase(UNDEFINED)) {
            final double d_left = ConversionUtils.toDouble(left);
            final double d_top = ConversionUtils.toDouble(top);
            final double d_right = ConversionUtils.toDouble(right);
            final double d_height = ConversionUtils.toDouble(height);
            _frame.setArea((String) name, d_left, d_top, d_right, d_height);
        }
    }

    public void minimize() {
        _frame.minimize();
    }
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}

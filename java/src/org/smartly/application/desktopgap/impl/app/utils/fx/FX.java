package org.smartly.application.desktopgap.impl.app.utils.fx;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import org.smartly.commons.event.IEventListener;

/**
 *
 */
public class FX {

    public static Draggable draggable(final Node node) {
        return new Draggable(node);
    }

    public static Sizable sizable(final Pane node,
                                  final double minWidth,
                                  final double minHeight,
                                  final double maxWidth,
                                  final double maxHeight) {
        return new Sizable(node, minWidth, minHeight, maxWidth, maxHeight);
    }

    public static Node getRoot(final Node node) {
        if (null == node) {
            return null;
        }
        if (null != node.getParent()) {
            return getRoot(node.getParent());
        } else {
            return node;
        }
    }

    public static Rectangle2D getScreenSize() {
        return Screen.getPrimary().getVisualBounds();
    }

}

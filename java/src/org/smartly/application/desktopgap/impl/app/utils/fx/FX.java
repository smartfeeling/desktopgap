package org.smartly.application.desktopgap.impl.app.utils.fx;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 */
public class FX {

    public static Draggable draggable(final Node node) {
        return new Draggable(node);
    }

    public static Sizable sizable(final Pane node) {
        return new Sizable(node);
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

}

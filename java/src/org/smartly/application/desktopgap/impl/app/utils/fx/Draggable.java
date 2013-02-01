package org.smartly.application.desktopgap.impl.app.utils.fx;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 *
 */
public class Draggable {

    private static final Cursor DRAGGABLE = Cursor.MOVE;
    private static final Cursor DRAGGING = Cursor.DEFAULT;

    private double initialX;
    private double initialY;

    public Draggable(final Node node) {
        addDraggableNode(node);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void addDraggableNode(final Node node) {
        if (null == node) {
            return;
        }
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                node.setCursor(DRAGGABLE);
            }
        });

        node.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                node.setCursor(DRAGGABLE);
            }
        });

        node.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                try {
                    if (me.getButton() != MouseButton.MIDDLE) {
                        node.setCursor(DRAGGING);
                        initialX = me.getSceneX();
                        initialY = me.getSceneY();
                    }
                } catch (Throwable ignored) {
                }
            }
        });

        node.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                try {
                    if (me.getButton() != MouseButton.MIDDLE) {
                        node.getScene().getWindow().setX(me.getScreenX() - initialX);
                        node.getScene().getWindow().setY(me.getScreenY() - initialY);
                        node.setCursor(DRAGGING);
                    }
                } catch (Throwable ignored) {
                }
            }
        });
    }
}

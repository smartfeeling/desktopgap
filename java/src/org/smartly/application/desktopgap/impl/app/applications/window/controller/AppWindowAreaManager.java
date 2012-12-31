package org.smartly.application.desktopgap.impl.app.applications.window.controller;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.smartly.application.desktopgap.impl.app.utils.fx.FX;
import org.smartly.commons.event.Event;
import org.smartly.commons.event.EventEmitter;
import org.smartly.commons.event.Events;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper to manage window area
 */
public class AppWindowAreaManager
        extends EventEmitter {

    private static final String DRAG_AREA = "dragbar";

    private final AnchorPane _main_area;
    private final Map<String, StackPane> _areas;
    private final boolean _sizable;
    private final boolean _draggable;

    public AppWindowAreaManager(final boolean sizable,
                                final boolean draggable,
                                final AnchorPane root) {
        _main_area = root;
        _areas = new HashMap<String, StackPane>();
        _draggable = draggable;
        _sizable = sizable;

        if(_sizable){
            FX.sizable(root);
        }
    }

    public void setArea(final String name,
                        final double left, final double top, final double right, final double height) {
        if (!_areas.containsKey(name)) {
            final StackPane area = this.createArea(name);
            _main_area.getChildren().add(area);
            this.attachToRoot(area, left, top, right, height);
            if (DRAG_AREA.equalsIgnoreCase(name) && _draggable) {
                // DRAGGABLE AREA
                FX.draggable(area);
            } else {
                // HANDLE CLICK
                this.handleClick(area);
            }
            _areas.put(name, area);
        }
    }

    @Override
    public int emit(final Event event) {
        return super.emit(event);
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private StackPane createArea(final String name) {
        final StackPane pane = new StackPane();
        pane.setId(name);
        pane.setFocusTraversable(true);

        return pane;
    }

    private void attachToRoot(final StackPane stack,
                              final double left, final double top, final double right, final double height) {
        AnchorPane.setLeftAnchor(stack, left);
        AnchorPane.setTopAnchor(stack, top);
        AnchorPane.setRightAnchor(stack, right);
        if (height > 0) {
            stack.setPrefHeight(height);
        } else {
            AnchorPane.setBottomAnchor(stack, 0.0);
        }
    }

    private void handleClick(final StackPane area) {
        area.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent mouseEvent) {
                final Object source = mouseEvent.getSource();
                if (source instanceof Node) {
                    final Event event = new Event(source, Events.ON_CLICK, mouseEvent);
                    emit(event);
                }
            }
        });
    }

}

package org.smartly.application.desktopgap.impl.app.applications.window.webview.jfx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.smartly.application.desktopgap.impl.app.applications.events.FrameResizeEvent;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractWebView;
import org.smartly.application.desktopgap.impl.app.applications.window.webview.AbstractWebViewAreaManager;
import org.smartly.application.desktopgap.impl.app.utils.fx.FX;
import org.smartly.application.desktopgap.impl.app.utils.fx.Sizable;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper to manage window area
 */
public class JfxWebViewAreaManager
        extends AbstractWebViewAreaManager {

    private static final String DRAG_AREA = "dragbar";

    private final AbstractWebView _baseView;
    private final Pane _main_area;
    private final Map<String, StackPane> _areas;
    private final boolean _draggable;

    public JfxWebViewAreaManager(final AbstractWebView baseView,
                                 final Pane root) {
        _baseView = baseView;
        _main_area = root;
        _areas = new HashMap<String, StackPane>();
        _draggable = baseView.frame().isDraggable();

        if (baseView.frame().isResizable()) {
            /*
            FX.sizable(root,
                    baseView.frame().getMinWidth(),
                    baseView.frame().getMinHeight(),
                    baseView.frame().getMaxWidth(),
                    baseView.frame().getMaxHeight())
                    .onResize(new Sizable.OnResize() {
                        @Override
                        public void handle(final FrameResizeEvent event) {
                            _baseView.triggerOnResize(event);
                        }
                    });
                    */
        }
    }

    @Override
    public void setArea(final String name,
                        final double left, final double top, final double right, final double height) {
        if (!_areas.containsKey(name)) {
            final StackPane area = this.createArea(name);
            _main_area.getChildren().add(area);
            this.attachToRoot(area, left, top, right, height);

            // dragarea
            if (DRAG_AREA.equalsIgnoreCase(name) && _draggable) {
                // DRAGGABLE AREA
                FX.draggable(area);
            }
            // ... add here more options ...

            _areas.put(name, area);
        }
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

}

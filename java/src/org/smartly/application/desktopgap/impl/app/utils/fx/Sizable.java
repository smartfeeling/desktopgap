package org.smartly.application.desktopgap.impl.app.utils.fx;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 */
public class Sizable {

    private static final double AREA_HEIGHT = 10;
    private static final int CORNER_HEIGHT = 15;

    private static final int AXIS_NONE = 0;
    private static final int AXIS_X = 1;
    private static final int AXIS_Y = 2;

    private static final String ID_TOP = "__top_stack__";
    private static final String ID_RIGHT = "__right_stack__";
    private static final String ID_BOTTOM = "__bottom_stack__";
    private static final String ID_LEFT = "__left_stack__";

    private static enum Direction {
        UNDEFINED,
        N,
        NE,
        E,
        SE,
        S,
        SW,
        W,
        NW
    }

    private final Pane _parent;
    private double _initialScreenX;
    private double _initialScreenY;
    private double _initialSceneX;
    private double _initialSceneY;
    private double _initialHeight;
    private double _initialWidth;
    private Direction _direction;
    private boolean _dragging;

    public Sizable(final Pane pane) {
        if (null != pane) {
            if (pane instanceof AnchorPane) {
                _parent = pane;
                makeSizable((AnchorPane) pane);
            } else {
                // add new AnchorPane on top of pane
                _parent = null;
            }
        } else {
            _parent = null;
        }

    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    private void makeSizable(final AnchorPane anchorPane) {
        if (null == anchorPane) {
            return;
        }

        this.addRight(anchorPane);
        this.addLeft(anchorPane);
        this.addTop(anchorPane);
        this.addBottom(anchorPane);
    }

    public double getThresholdX(){
        if(null!=_parent){
            return (_parent.getLayoutX() - _parent.getScene().getX())*2;
        }
        return 0.0;
    }

    public double getThresholdY(){
        if(null!=_parent){
            return (_parent.getLayoutY() - _parent.getScene().getY())*2;
        }
        return 0.0;
    }

    private Direction getDirection(final Node stack, double sceneX, double sceneY) {
        final String id = stack.getId();
        final double width = _parent.getWidth();
        final double height = _parent.getHeight();
        if (ID_TOP.equalsIgnoreCase(id)) {
            if (sceneX < CORNER_HEIGHT) {
                return Direction.NW;
            } else if (sceneX > (width - CORNER_HEIGHT)) {
                return Direction.NE;
            }
            return Direction.N;
        } else if (ID_RIGHT.equalsIgnoreCase(id)) {

            return Direction.E;
        } else if (ID_BOTTOM.equalsIgnoreCase(id)) {
            if (sceneX < CORNER_HEIGHT) {
                return Direction.SW;
            } else if (sceneX > (width - CORNER_HEIGHT)) {
                return Direction.SE;
            }
            return Direction.S;
        } else if (ID_LEFT.equalsIgnoreCase(id)) {

            return Direction.W;
        }
        return Direction.UNDEFINED;
    }

    private void setCursor(final Node stack, final Direction direction){
        if (Direction.N.equals(direction)) {
            stack.setCursor(Cursor.N_RESIZE);
        } else if (Direction.NE.equals(direction)) {
            stack.setCursor(Cursor.NE_RESIZE);
        } else if (Direction.E.equals(direction)) {
            stack.setCursor(Cursor.E_RESIZE);
        }else if (Direction.SE.equals(direction)) {
            stack.setCursor(Cursor.SE_RESIZE);
        }else if (Direction.S.equals(direction)) {
            stack.setCursor(Cursor.S_RESIZE);
        }else if (Direction.SW.equals(direction)) {
            stack.setCursor(Cursor.SW_RESIZE);
        }else if (Direction.W.equals(direction)) {
            stack.setCursor(Cursor.W_RESIZE);
        }else if (Direction.NW.equals(direction)) {
            stack.setCursor(Cursor.NW_RESIZE);
        }
    }

    private void handleFocus(final Node stack,
                               final MouseEvent me){
        if(!_dragging){
            final double x = me.getSceneX();
            final double y = me.getSceneY();
            _direction = getDirection(stack, x, y);
            setCursor(stack, _direction);
        }
    }

    private void handlePressed(final Node stack, final MouseEvent me) {
        _dragging = true;
        try {
            if (me.getButton() != MouseButton.MIDDLE) {
                _initialScreenX = me.getScreenX();
                _initialScreenY = me.getScreenY();
                _initialSceneX = me.getSceneX();
                _initialSceneY = me.getSceneY();
                _initialHeight = stack.getScene().getHeight();
                _initialWidth = stack.getScene().getWidth();
            }
        } catch (Throwable ignored) {
        }
    }

    private void handleReleased(final Node stack, final MouseEvent me) {
        _dragging = false;
    }

    private void handleDragged(final Node stack,
                               final MouseEvent me) {
        try {
            if (me.getButton() != MouseButton.MIDDLE) {
                //-- delta --//
                final double deltaX = me.getScreenX() - _initialScreenX;
                final double deltaY = me.getScreenY() - _initialScreenY;
                if (Direction.N.equals(_direction)) {
                    //-- NORTH --//
                    stack.getScene().getWindow().setHeight(_initialHeight - deltaY);
                    // move window on Y axis
                    stack.getScene().getWindow().setY(me.getScreenY() - _initialSceneY);
                } else if (Direction.NE.equals(_direction)) {
                    //-- NORTH-EAST --//
                    stack.getScene().getWindow().setHeight(_initialHeight - deltaY);
                    stack.getScene().getWindow().setWidth(_initialWidth + deltaX);
                    // move window on Y axis
                    stack.getScene().getWindow().setY(me.getScreenY() - _initialSceneY);
                } else if (Direction.E.equals(_direction)) {
                    //-- EAST --//
                    stack.getScene().getWindow().setWidth(_initialWidth + deltaX);
                }else if (Direction.SE.equals(_direction)) {
                    //-- SOUTH-EAST --//
                    stack.getScene().getWindow().setHeight(_initialHeight + deltaY);
                    stack.getScene().getWindow().setWidth(_initialWidth + deltaX);
                    // move window on Y axis
                    //stack.getScene().getWindow().setX(me.getScreenX() - _initialSceneX);
                }else if (Direction.S.equals(_direction)) {
                    //-- SOUTH --//
                    stack.getScene().getWindow().setHeight(_initialHeight + deltaY);
                }else if (Direction.SW.equals(_direction)) {
                    //-- SOUTH_WEST --//
                    stack.getScene().getWindow().setHeight(_initialHeight + deltaY);
                    stack.getScene().getWindow().setWidth(_initialWidth - deltaX);
                    // move window on Y axis
                    stack.getScene().getWindow().setX(me.getScreenX() - _initialSceneX);
                }else if (Direction.W.equals(_direction)) {
                    //-- WEST --//
                    final double x = this.getThresholdX();
                    // move window on X axis
                    stack.getScene().getWindow().setX(me.getScreenX()-x + _initialSceneX);
                    // resize Width
                    stack.getScene().getWindow().setWidth(_initialWidth - deltaX);
                }else if (Direction.NW.equals(_direction)) {
                    //-- NORTH-WEST --//
                    final double x = this.getThresholdX();
                    // move window on X axis
                    stack.getScene().getWindow().setX(me.getScreenX()-x + _initialSceneX);
                    // move window on Y axis
                    stack.getScene().getWindow().setY(me.getScreenY() - _initialSceneY);
                    // resize
                    stack.getScene().getWindow().setHeight(_initialHeight - deltaY);
                    stack.getScene().getWindow().setWidth(_initialWidth - deltaX);
                }
            }
        } catch (Throwable ignored) {
        }
    }
    // ------------------------------------------------------------------------
    //                      TOP
    // ------------------------------------------------------------------------

    private void addTop(final AnchorPane anchorPane) {
        final StackPane stack = new StackPane();
        stack.setId(ID_TOP);
        stack.setPrefHeight(AREA_HEIGHT);
        stack.setAlignment(Pos.TOP_CENTER);

        AnchorPane.setTopAnchor(stack, -(AREA_HEIGHT/2));
        AnchorPane.setRightAnchor(stack, 0.0);
        AnchorPane.setLeftAnchor(stack, 0.0);
        anchorPane.getChildren().add(stack);

        //-- handle --//
        stack.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handlePressed(stack, me);
            }
        });

        stack.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleDragged(stack, me);
            }
        });

        stack.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleReleased(stack, me);
            }
        });

    }

    // ------------------------------------------------------------------------
    //                      RIGHT
    // ------------------------------------------------------------------------

    private void addRight(final AnchorPane anchorPane) {
        final StackPane stack = new StackPane();
        stack.setId(ID_RIGHT);
        stack.setPrefWidth(AREA_HEIGHT);
        stack.setAlignment(Pos.CENTER);

        AnchorPane.setRightAnchor(stack, -5.0);
        AnchorPane.setTopAnchor(stack, 0.0);
        AnchorPane.setBottomAnchor(stack, 0.0);
        anchorPane.getChildren().add(stack);

        //-- handle --//
        stack.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handlePressed(stack, me);
            }
        });

        stack.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleDragged(stack, me);
            }
        });

        stack.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleReleased(stack, me);
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      BOTTOM
    // ------------------------------------------------------------------------

    private void addBottom(final AnchorPane anchorPane) {
        final StackPane stack = new StackPane();
        stack.setId(ID_BOTTOM);
        stack.setPrefHeight(AREA_HEIGHT);
        stack.setAlignment(Pos.BOTTOM_CENTER);

        AnchorPane.setBottomAnchor(stack, -5.0);
        AnchorPane.setRightAnchor(stack, 0.0);
        AnchorPane.setLeftAnchor(stack, 0.0);
        anchorPane.getChildren().add(stack);

        //-- handle --//
        stack.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handlePressed(stack, me);
            }
        });

        stack.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleDragged(stack, me);
            }
        });

        stack.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleReleased(stack, me);
            }
        });
    }

    // ------------------------------------------------------------------------
    //                      LEFT
    // ------------------------------------------------------------------------

    private void addLeft(final AnchorPane anchorPane) {
        final StackPane stack = new StackPane();
        stack.setId(ID_LEFT);
        stack.setPrefWidth(AREA_HEIGHT);
        stack.setAlignment(Pos.CENTER);
        AnchorPane.setLeftAnchor(stack, 0.0);
        AnchorPane.setTopAnchor(stack, 0.0);
        AnchorPane.setBottomAnchor(stack, 0.0);
        anchorPane.getChildren().add(stack);

        //-- handle --//
        stack.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleFocus(stack, me);
            }
        });

        stack.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handlePressed(stack, me);
            }
        });

        stack.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleDragged(stack, me);
            }
        });

        stack.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent me) {
                handleReleased(stack, me);
            }
        });
    }

}

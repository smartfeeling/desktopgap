package org.smartly.application.desktopgap.impl.app.utils;

/**
 *
 */
public class Size2D {

    private double _width;
    private double _height;

    public Size2D() {

    }

    public Size2D(double height, double width) {
        _height = height;
        _width = width;
    }

    public double getWidth() {
        return _width;
    }

    public void setWidth(double width) {
        _width = width;
    }

    public double getHeight() {
        return _height;
    }

    public void setHeight(double height) {
        _height = height;
    }

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------


}

(function (window) {

    var defined = window['defined'] || function(){return false}; // func defined()

    // ------------------------------------------------------------------------
    //                      F R A M E
    // ------------------------------------------------------------------------

    var Frame = function (frame_bridge) {
        this['_frame'] = frame_bridge;
        this.buttons = {};
    };

    Frame.prototype.buttonClicked = function (name) {
        if (defined('bridge')) {
            var frame = this['_frame'] || desktopgap['bridge'].frame();
            frame.buttonClicked(name);
        }
    };

    Frame.prototype.setArea = function (name, left, top, right, height) {
        if (defined('bridge')) {
            name = name || 'undefined';
            left = parseFloat(left);
            top = parseFloat(top);
            right = parseFloat(right);
            height = parseFloat(height);
            var frame = this['_frame'] || desktopgap['bridge'].frame();
            frame.setArea(name, left, top, right, height);
        }
    };

    Frame.prototype.minimize = function () {
        if (defined('bridge')) {
            var frame = this['_frame'] || desktopgap['bridge'].frame();
            frame.minimize();
        }
    };

    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = new Frame(null);
    exports.Frame = Frame;

    return exports;

})(this);
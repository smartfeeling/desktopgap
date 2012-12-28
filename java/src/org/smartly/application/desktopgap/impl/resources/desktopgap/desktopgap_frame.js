(function (window) {

    var defined = window.defined; // func defined()

    // ------------------------------------------------------------------------
    //                      F R A M E
    // ------------------------------------------------------------------------

    var frame = {

        init: function () {

            return this;
        },

        buttons: {},

        buttonClicked: function (name) {
            if (defined('bridge')) {
                desktopgap['bridge'].buttonClicked(name);
            }
        },

        setArea: function (name, left, top, right, height) {
            if (defined('bridge')) {
                name = name || 'undefined';
                left = parseFloat(left);
                top = parseFloat(top);
                right = parseFloat(right);
                height = parseFloat(height);
                desktopgap['bridge'].setArea(name, left, top, right, height);
            }
        }

    };

    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = frame.init();

    return exports;

})(this);
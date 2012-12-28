(function(window){

    var defined = window.defined; // func defined()

    // ------------------------------------------------------------------------
    //                      C O N S O L E
    // ------------------------------------------------------------------------

    var console = {

        init: function () {

            return this;
        },

        open: function () {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().open();
            }
        },

        log: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().log(message);
            }
        },

        error: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().error(message);
            }
        },

        warn: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().warn(message);
            }
        },

        info: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().info(message);
            }
        },

        debug: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().debug(message);
            }
        }
    };

    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = console.init();

    return exports;

})(this);
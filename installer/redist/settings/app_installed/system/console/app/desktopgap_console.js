(function (window) {

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

        log: function () {
            var bridge = defined('bridge');
            if (!!bridge) {
                var args = toArray(arguments)
                    , type
                    , message
                    ;
                if (args.length === 1) {
                    type = 'info';
                    message = args[0];
                } else if (args.length === 2) {
                    type = args[0];
                    message = args[1];
                }
                bridge.console().log(type, message);
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
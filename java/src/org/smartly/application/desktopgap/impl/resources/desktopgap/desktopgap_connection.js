(function (window) {

    var defined = window.defined; // func defined()

    // ------------------------------------------------------------------------
    //                      C O N N E C T I O N
    // ------------------------------------------------------------------------

    var Connections = {
        UNKNOWN: 'UNKNOWN',
        ETHERNET: 'ETHERNET',
        NONE: 'NONE'
    };

    var connection = {

        init: function () {
            var self = this;
            document.addEventListener('deviceready', function () {
                self.refresh();
            }, false);
            return self;
        },

        type: Connections.UNKNOWN,

        refresh: function () {
            var bridge = defined('bridge');
            if (!!bridge) {
                this['type'] = bridge.connection().getType();
            }
        }

    };

    function getConnection() {
        var bridge = defined('bridge');
        if (!!bridge) {
            return bridge.connection().getType();
        }
        return Connections.UNKNOWN;
    }

    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = connection.init();

    exports.Connections = Connections;

    return exports;

})(this);
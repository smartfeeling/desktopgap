(function (window) {

    var defined = window.defined; // func defined()

    // ------------------------------------------------------------------------
    //                      D E V I C E
    // ------------------------------------------------------------------------

    var module = {

        init: function () {

            return this;
        },

        hasApps: function(){
            if (defined('bridge')) {
                return desktopgap['bridge'].runtime().hasApps();
            }
            return false;
        },

        appNames: function () {
            if (defined('bridge')) {
                var array = desktopgap['bridge'].runtime().getAppNames();
                if (!!array) {
                    return JSON.parse(array);
                }
            }
            return [];
        }
    };


    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module.init();

    return exports;

})(this);
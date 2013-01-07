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
        },

        appGroups: function () {
            if (defined('bridge')) {
                var json = desktopgap['bridge'].runtime().getGroupedApps();
                if (!!json) {
                    return JSON.parse(json);
                }
            }
            return {};
        }
    };


    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module.init();

    return exports;

})(this);
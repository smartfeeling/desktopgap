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
        },

        /**
         * Run Application and returns js frame wrapper
         * @param appId  ID of Application to run
         * @return {*}
         */
        runApp: function (appId) {
            try{
                if (defined('bridge')) {
                    var Frame = window.desktopgap.frame.Frame;
                    return new Frame(desktopgap['bridge'].runtime().runApp(appId));
                }
            }catch(err){
                console.error('desktopgap_runtime() runApp("'+appId+'"): ' + err);
            }
            return null;
        },

        /**
         * Protected Method: Exit DesktopGap runtime and close all applications.
         */
        exit: function () {
            try{
                if (defined('bridge')) {
                    desktopgap['bridge'].runtime().exit();
                }
            }catch(err){
                console.error('desktopgap_runtime() exit:(): ' + err);
            }
        },

        /**
         * protected Method: Shutdown PC
         */
        shutdown: function () {
            try{
                if (defined('bridge')) {
                    desktopgap['bridge'].runtime().OSShutDown();
                }
            }catch(err){
                console.error('desktopgap_runtime() shutdown:(): ' + err);
            }
        },

        openStoreFolder: function () {
            try{
                if (defined('bridge')) {
                    desktopgap['bridge'].runtime().openStoreFolder();
                }
            }catch(err){
                console.error('desktopgap_runtime() openStoreFolder(): ' + err);
            }
        }
    };


    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module.init();

    return exports;

})(this);
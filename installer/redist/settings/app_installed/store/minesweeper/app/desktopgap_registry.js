(function (window) {

    var defined = window.defined; // func defined()

    // ------------------------------------------------------------------------
    //                      D E V I C E
    // ------------------------------------------------------------------------

    var module = {

        init: function () {

            return this;
        },

        get: function (key) {
            if (defined('bridge')) {
                var result = desktopgap['bridge'].registry().get(key);
                try{
                    return JSON.parse(result);
                } catch(err){
                   return result;
                }
            }
            return '';
        },

        put: function (key, value) {
            if (defined('bridge')) {
                var result = desktopgap['bridge'].registry().put(key, JSON.stringify(value));
                try{
                    return JSON.parse(result);
                } catch(err){
                    return result;
                }
            }
            return null;
        },

        remove: function (key) {
            if (defined('bridge')) {
                var result = desktopgap['bridge'].registry().remove(key);
                try{
                    return JSON.parse(result);
                } catch(err){
                    return result;
                }
            }
            return '';
        }

    };


    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module.init();

    return exports;

})(this);
(function (window) {

    var defined = window.defined; // func defined()

    // ------------------------------------------------------------------------
    //                      L O C A L S T O R E
    // ------------------------------------------------------------------------

    /**
     * Utility to handle retrieving and setting data in LocalStorage
     *
     * Usage:
     *  var item = new LocalStore('my_stored_item', {defaultVal:null});
     *  var val = item.get();
     *
     *  // change value
     *  item.set('new value');
     *
     *  // remove item
     *  item.remove();
     *
     */
    function LocalStore (key, options) {
        this.options = {
            defaultVal: null
        };

        for (item in options) {
            this.options[item] = options[item];
        }

        this.key = key;
        this.defaultVal = this.options.defaultVal;
    }

    LocalStore.prototype.get = function(){
        var item = window.localStorage.getItem(this.key);
        try {
            return item !== null ? JSON.parse(item) : (this.defaultVal ? this.defaultVal : null);
        } catch (err) {
            return item !== null ? item : (this.defaultVal ? this.defaultVal : null);
        }
    };

    LocalStore.prototype.set = function (value) {
        window.localStorage.setItem(this.key, JSON.stringify(value));
    };

    LocalStore.prototype.remove = function () {
        var old_value = this.get();
        window.localStorage.removeItem(this.key);
        return old_value;
    };

    // ------------------------------------------------------------------------
    //                      M O D U L E
    // ------------------------------------------------------------------------

    var module = {

        init: function () {

            return LocalStore;
        }
    };



    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module.init();

    return exports;

})(this);
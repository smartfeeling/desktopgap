(function (window) {

    var defined = window.defined // func defined()
        , getAllElementsWithAttribute = window.getAllElementsWithAttribute
        , attr = 'data-i18n'
        ;
    // ------------------------------------------------------------------------
    //                      D E V I C E
    // ------------------------------------------------------------------------

    var module = {

        init: function () {

            return this;
        },

        /**
         * Call with 1, 2 or 3 arguments.
         * i.e.:
         * get('lang', 'dictionary', 'key')
         * get('dictionary', 'key')
         *  get('dictionary.key')
         * @return {*}
         */
        get: function () {
            try {
                var args = slice.call(arguments);
                if (defined('bridge')) {
                    if (args.length === 3) {
                        return desktopgap['bridge'].i18n().get(args[0], args[1], args[2]);
                    } else if (args.length === 2) {
                        return desktopgap['bridge'].i18n().get(args[0], args[1]);
                    }
                }
            } catch (err) {
            }
            return "";
        },

        /**
         * Translate a document with tags containing [data-i18n] attribute.
         * @param lang
         */
        translate: function (lang) {
            if (defined('bridge')) {
                var elems = getAllElementsWithAttribute('data-i18n');
                for (var i = 0; i < elems.length; i++) {
                    var elem = elems[i];
                    var key = elem.getAttribute('data-i18n');
                    elem.innerHTML = desktopgap['bridge'].i18n().get(lang||'', key);
                }
            }
        }
    };


    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module.init();

    return exports;

})(this);
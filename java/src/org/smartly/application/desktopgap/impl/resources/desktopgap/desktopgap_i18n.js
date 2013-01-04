(function (window) {

    var defined = window.defined // func defined()
        , getAllElementsWithAttribute = window.getAllElementsWithAttribute
        , isString = window.isString
        , isElement = window.isElement
        , toArray = window.toArray
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
                if (defined('bridge')) {
                    var args = toArray(arguments);
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
         * Arguments:
         * - lang {string} (optional): Language for translation. Default is system language
         * - startElem {HTMLElement} (optional): element to translate. Can be any container. Default is document object
         */
        translate: function () {
            if (defined('bridge')) {
                try {
                    // parse args
                    var args =  toArray(arguments)
                        , lang
                        , startElem
                        ;
                    if (args.length > 0) {
                        if (isString(args[0])) {
                            lang = args[0];
                        } else if (isElement(args[0])) {
                            startElem = args[0];
                        }
                        if (args.length === 2) {
                            if (isString(args[1])) {
                                lang = lang || args[1];
                            } else if (isElement(args[1])) {
                                startElem = startElem || args[1];
                            }
                        }
                    }
                    // console.log('startElem: ' + startElem);
                    var elems = getAllElementsWithAttribute('data-i18n', startElem);
                    for (var i = 0; i < elems.length; i++) {
                        var elem = elems[i];
                        var key = elem.getAttribute('data-i18n');
                        elem.innerHTML = desktopgap['bridge'].i18n().get(lang || '', key);
                    }
                } catch (err) {
                    console.error('(desktopgap_i18n.js) transalte(): ' + err);
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
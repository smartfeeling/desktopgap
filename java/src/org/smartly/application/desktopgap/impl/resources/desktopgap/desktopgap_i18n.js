(function (window) {

    'use strict';

    var defined = window.defined // func defined()
        , getAllElementsWithAttribute = window.getAllElementsWithAttribute
        , isString = window.isString
        , isElement = window.isElement
        , toArray = window.toArray
        , attr = 'data-i18n'
        ;

    var EVENT_CHANGE = 'change';

    // ------------------------------------------------------------------------
    //                      D E V I C E
    // ------------------------------------------------------------------------

    var module = {

        init: function () {
            this['_listeners'] = {};
            return this;
        },

        on: function (eventname, func) {
            if (!!eventname && isFunction(func)) {
                this['_listeners'][eventname] = this['_listeners'][eventname] || [];
                this['_listeners'][eventname].push(func);
            }
        },

        off: function (eventname) {
            if (!!eventname ) {
                this['_listeners'][eventname] = [];
            } else {
                // remove all handlers
                this['_listeners'] = {};
            }
        },

        trigger: function () {
            var args = toArray(arguments);
            if (args.length > 0) {
                var listeners = this['_listeners'][args[0]] || [];
                if (listeners.length > 0) {
                    for (var i = 0; i < listeners.length; i++) {
                        var func = listeners[i];
                        if (isFunction(func)) {
                            try{
                                func.apply(this, args.splice(1));
                            }catch(ignored){
                            }
                        }
                    }
                }
            }
        },

        lang: function (opt_lang) {
            try {
                if (defined('bridge')) {
                    if (isString(opt_lang)) {
                        desktopgap['bridge'].i18n().setLang(opt_lang);
                        this.trigger(EVENT_CHANGE, opt_lang);
                    }
                    return desktopgap['bridge'].i18n().getLang();
                }
            } catch (err) {
            }
            return "en";
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
                    } else if (args.length === 1) {
                        return desktopgap['bridge'].i18n().get(args[0]);
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
                    var args = toArray(arguments)
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
                    //-- translate data-i18n --//
                    var elems = getAllElementsWithAttribute(attr, startElem);
                    for (var i = 0; i < elems.length; i++) {
                        var elem = elems[i];
                        var key = elem.getAttribute(attr);
                        elem.innerHTML = desktopgap['bridge'].i18n().get(lang || '', key);
                    }

                    //-- translate placeholder --//
                    var elems = getAllElementsWithAttribute('placeholder', startElem);
                    for (var i = 0; i < elems.length; i++) {
                        var elem = elems[i];
                        var key = elem.getAttribute('placeholder');
                        var text = desktopgap['bridge'].i18n().get(lang || '', key);
                        if (!!text) {
                            elem.setAttribute('placeholder', text);
                        }
                    }
                } catch (err) {
                    console.error('(desktopgap_i18n.js) translate(): ' + err);
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
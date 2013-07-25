!(function (window) {

    var document = window.document;


    // ------------------------------------------------------------------------
    //                      js document events
    // ------------------------------------------------------------------------

    var EVENT_ERROR = 'error';
    var EVENT_DATA = 'data';
    var EVENT_READY = 'ready';
    var EVENT_PLUGIN_READY = 'pluginready';
    var EVENT_DEVICEREADY = 'deviceready';
    var EVENT_RESIZE = 'frame.onResize';
    var EVENT_SCROLL = 'frame.onScroll';
    var EVENT_DRAG_OVER = 'frame.onDragDropped';

    // ------------------------------------------------------------------------
    //                      utility - private
    // ------------------------------------------------------------------------

    function _extend(target, source) {
        var name, copy;
        if (source) {
            for (name in source) {
                if (!!name) {
                    copy = source[ name ];
                    if (copy !== undefined) {
                        target[ name ] = copy;
                    }
                }
            }
        }
    }

    function _evalScript(text) {
        if (!!text) {
            return window[ "eval" ].call(window, text);
        }
        return {};
    }

    function _getUrl(url, callback) {
        var xhr = new XMLHttpRequest(),
            async = isFunction(callback);
        xhr.open("GET", url, async);
        if (async) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState === 4) {
                    callback(xhr.responseText);
                }
            };
        }
        xhr.send();
        return xhr.responseText;
    }

    function _getJavaTool(name){
        if(_defined('bridge')){
           return desktopgap['bridge'].get(name);
        }
        return null;
    }

    function _getAllElementsWithAttribute(attribute, startElem) {
        var matchingElements = [];
        try {
            startElem = startElem || document;
            var allElements = startElem.getElementsByTagName('*');
            for (var i = 0; i < allElements.length; i++) {
                if (allElements[i].getAttribute(attribute)) {
                    // Element exists with attribute. Add to array.
                    matchingElements.push(allElements[i]);
                }
            }
        } catch (err) {
            console.error('(desktopgap.js) getAllElementsWithAttribute(): ' + err);
        }
        return matchingElements;
    }

    function _require(url) {
        try {
            var script = _getUrl(url);
            if (!!script) {
                return _evalScript(script);
            }
        } catch (err) {
            return {
                error: err
            };
        }
        return {};
    }

    function _defined(property) {
        if (typeof desktopgap != 'undefined') {
            return !!property ? desktopgap[property] : desktopgap;
        }
        return false;
    }

    function _load(url) {
        // console.log('load: ' + url);
        try {
            var text = _getUrl(url);
            if (!!text) {
                return text;
            }
        } catch (err) {
            return err.toString();
        }
        return '';
    }

    function _isElement(obj) {
        return !!(obj && obj.nodeType == 1);
    }

    function _has(obj, key) {
        return Object.prototype.hasOwnProperty.call(obj, key);
    }

    function _values(obj) {
        var values = [];
        for (var key in obj) if (has(obj, key)) values.push(obj[key]);
        return values;
    }

    function _toArray(obj) {
        if (!obj) return [];
        if (obj.length === +obj.length) return Array.prototype.slice.call(obj);
        return values(obj);
    }

    function _isFunction(obj) {
        return Object.prototype.toString.call(obj) == '[object Function]';
    }

    function _isString(obj) {
        return Object.prototype.toString.call(obj) == '[object String]';
    }

    function _trigger(target, eventName){
        target=target||document;
        if(!!eventName){
            var args = _toArray(arguments);
            var evt = document.createEvent('Event');
            // define that the event name is '[EVENT_NAME]'
            evt.initEvent(eventName, true, true);

            if (args.length>2) {
                evt.data = evt.data || [];
                evt.data.push(args.splice(0, 2));
            }

            // dispatch the Event
            target.dispatchEvent(evt);
        }
    }

    // ------------------------------------------------------------------------
    //                      utility
    // ------------------------------------------------------------------------

    window.has = _has;

    window.values = _values;

    window.toArray = _toArray;

    window.isFunction = _isFunction;

    window.isString = _isString;

    window.isElement = _isElement;

    window.getAllElementsWithAttribute = _getAllElementsWithAttribute;

    /**
     * Load script and evaluate returning the result.
     * @param url Script to load.
     * @return {*} Result of script evaluation
     */
    window.require = _require;

    /**
     * Load text file (html, css, ...) and returns the content.
     * @param url
     * @return {string} Content of text file, or empty string.
     */
    window.load = _load;

    window.defined = _defined;

    /**
     * get a java tool registered in java bridge.
     * usage: java('toolname');
     */
    window.javaTool = _getJavaTool;

    /**
     * Trigger Event on document.
     * Usage:
     *  trigger(target, 'click', arg1, arg2);   // event on target
     *  trigger(null, 'click', arg1, arg2);     // event on document
     */
    window.trigger = _trigger;

    // ------------------------------------------------------------------------
    //                      initialization
    // ------------------------------------------------------------------------

    //-- buttons --//

    var normal = 'NORMAL'
        , clicked = 'CLICKED'
        ;

    function initButtons() {
        try {
            var buttons = desktopgap.frame.buttons
                , close = buttons.close = document.getElementById('close')
                , fullscreen = buttons.fullscreen = document.getElementById('fullscreen')
                , minimize = buttons.minimize = document.getElementById('minimize')
                , maximize = buttons.maximize = document.getElementById('maximize')
                , maximize_glyph = document.getElementById('maximize-glyph');

            //-- fix focus bug --//
            fixButtonFocus(fullscreen, minimize, maximize);

            //-- handle buttons click --//
            if (!!close) {
                close.onclick = makeClickHandler('close');
            }

            if (!!minimize) {
                minimize.onclick = makeClickHandler('minimize');
            }

            if (!!maximize) {
                maximize['data-state'] = normal; // initial style
                maximize.onclick = makeClickHandler('maximize', function () {
                    if (maximize['data-state'] == normal) {
                        maximize['data-state'] = clicked;
                        maximize_glyph.src = './frame/glyph-exit-maximize.png';
                    } else {
                        maximize['data-state'] = normal;
                        maximize_glyph.src = 'frame/glyph-maximize.png';
                    }
                });
            }

            if (!!fullscreen) {
                fullscreen['data-state'] = normal; // initial style
                fullscreen.onclick = makeClickHandler('fullscreen', function () {
                    if (fullscreen['data-state'] == normal) {
                        fullscreen['data-state'] = clicked;
                        fullscreen.classList.remove('fullscreen');
                        fullscreen.classList.add('fullscreen-clicked');
                    } else {
                        fullscreen['data-state'] = normal;
                        fullscreen.classList.remove('fullscreen-clicked');
                        fullscreen.classList.add('fullscreen');
                    }
                });
            }
        } catch (err) {
            console.error('(desktopgap.js) Error on "initButtons()": ' + err);
        }
    }

    function makeClickHandler(buttonName, callback) {
        return function (event, element) {
            event.cancelBubble = true;
            if (event.stopPropagation)
                event.stopPropagation();
            desktopgap.frame.buttonClicked(buttonName);
            if (!!callback) {
                callback();
            }
        };
    }

    function fixButtonFocus(fullscreen, minimize, maximize) {
        // fix focus bug
        if (!!fullscreen) {
            fullscreen.addEventListener('click', function () {
                fullscreen.classList.remove('fullscreen');
                document.body.addEventListener('mouseover', function () {
                    fullscreen.classList.add('fullscreen');
                    document.body.removeEventListener('mouseover', arguments.callee, false);
                }, false);
            }, false);
        }

        if (!!minimize) {
            minimize.addEventListener('click', function () {
                minimize.classList.remove('minimize');
                document.body.addEventListener('mouseover', function () {
                    minimize.classList.add('minimize');
                    document.body.removeEventListener('mouseover', arguments.callee, false);
                }, false);
            }, false);
        }

        if (maximize) {
            maximize.addEventListener('click', function () {
                maximize.classList.remove('maximize');
                document.body.addEventListener('mouseover', function () {
                    maximize.classList.add('maximize');
                    document.body.removeEventListener('mouseover', arguments.callee, false);
                }, false);
            }, false);
        }
    }

    //-- drag&drop, area and context menu --//

    function updateAreas() {
        try {
            if (defined()) {
                var titlebar = document.getElementById('titlebar');
                var content = document.getElementById('content') || {};
                var fullscreen = document.getElementById('fullscreen') || {};
                var minimize = document.getElementById('minimize') || {};
                var maximize = document.getElementById('maximize') || {};
                var close = document.getElementById('close') || {};

                var height = !!titlebar ? titlebar.offsetHeight || 27 : 27;
                var buttonPadding = 12;
                var margin = content.offsetLeft || 0;
                var leftOffset = 0;
                var closeOffset = close.offsetWidth || 0;
                var miniOffset = minimize.offsetWidth || 0;
                var rightOffset = closeOffset + miniOffset + buttonPadding; // 12 pixels of padding

                if (!!fullscreen.style && fullscreen.style.visibility == 'visible') {
                    leftOffset = fullscreen.offsetWidth + buttonPadding;
                }

                if (!!maximize.style && maximize.style.visibility == 'visible') {
                    rightOffset += maximize.offsetWidth;
                }

                // Setup title bar drag region
                // left, top, right, height
                desktopgap.frame.setArea('dragbar',
                    margin + leftOffset,
                    margin,
                    margin + rightOffset,
                    height);

            }
        } catch (err) {
            console.error('(desktopgap.js) Error on "updateAreas()": ' + err);
        }
    }

    function disableContextMenu() {
        window.addEventListener('contextmenu', function (event) {
            event.preventDefault();
            event.stopPropagation();
            return false;
        }, false);
    }

    function disableDragAndDrop() {
        window.addEventListener("dragover", function (e) {
            e = e || event;
            e.preventDefault();
        }, false);

        window.addEventListener('drop', function (e) {
            e = e || event;
            e.preventDefault();
        }, false);
    }

    function init() {
        //disableContextMenu();
        disableDragAndDrop();
        initButtons();
        //updateAreas();
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    var exports = window.desktopgap = window.dg = {};

    //-- RUNTIME (protected) --//
    exports.runtime = require('desktopgap_runtime.js');

    //-- CONSOLE --//
    exports.console = require('desktopgap_console.js');

    //-- I18N --//
    exports.i18n = require('desktopgap_i18n.js');

    //-- FRAME --//
    exports.frame = require('desktopgap_frame.js');

    //-- REGISTRY --//
    exports.registry = require('desktopgap_registry.js');

    //-- CONNECTION --//
    exports.connection = require('desktopgap_connection.js');
    exports.Connections = exports.connection.Connections;

    //-- DEVICE --//
    exports.device = require('desktopgap_device.js');

    //-- FILE --//
    exports.fileSystem = require('desktopgap_file.js');

    //-- LOCALSTORE --//
    exports.LocalStore = require('desktopgap_localstore.js');

    // ------------------------------------------------------------------------
    //                      -> n a v i g a t o r
    // ------------------------------------------------------------------------

    window.navigator = window.navigator || {};

    for (var key in exports) {
        window.navigator[key] = exports[key];
    }

    // ------------------------------------------------------------------------
    //                      -> window
    // ------------------------------------------------------------------------

    window.deviceready = false;

    window.console = exports.console;

    window.i18n = exports.i18n;

    window.device = exports.device;

    window.Connections = exports.Connections;

    window.LocalStore = exports.LocalStore;

    // ------------------------------------------------------------------------
    //                  i n i t i a l i z a t i o n
    // ------------------------------------------------------------------------

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(window.location.search);
        if (results == null)
            return "";
        else
            return decodeURIComponent(results[1].replace(/\+/g, " "));
    }

    function start() {
        init();

        var bridge = defined('bridge');

        //-- set version --//
        window.version = !!bridge ? bridge.version() : '0.1 (debug)';

        //-- set ready state --//
        window.deviceready = true;
    }

    document.addEventListener(EVENT_DEVICEREADY, function () {
        start();
    }, false);

    /**
     * check if function is called out of desktopgap runtime
     * (for debug into external browser)
     */
    if (getParameterByName('desktopgap')==='false') {
        setTimeout(function(){
            // create the event
            _trigger(document, EVENT_DEVICEREADY);
        }, 100);
    }


})(this);
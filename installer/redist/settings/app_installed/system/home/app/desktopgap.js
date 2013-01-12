!(function (window) {

    var document = window.document;

    // ------------------------------------------------------------------------
    //                      utility
    // ------------------------------------------------------------------------

    window.has = function has(obj, key) {
        return Object.prototype.hasOwnProperty.call(obj, key);
    };

    window.values = function values(obj) {
        var values = [];
        for (var key in obj) if (has(obj, key)) values.push(obj[key]);
        return values;
    };

    window.toArray = function toArray(obj) {
        if (!obj) return [];
        if (obj.length === +obj.length) return Array.prototype.slice.call(obj);
        return values(obj);
    };

    window.isFunction = function isFunction(obj) {
        return Object.prototype.toString.call(obj) == '[object Function]';
    };

    window.isString = function isString(obj) {
        return Object.prototype.toString.call(obj) == '[object String]';
    };

    window.isElement = function isElement(obj) {
        return !!(obj && obj.nodeType == 1);
    };

    function extend(target, source) {
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

    function evalScript(text) {
        if (!!text) {
            return window[ "eval" ].call(window, text);
        }
        return {};
    }

    function getUrl(url, callback) {
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

    window.getAllElementsWithAttribute = function getAllElementsWithAttribute(attribute, startElem) {
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
    };

    /**
     * Load script and evaluate returning the result.
     * @param url Script to load.
     * @return {*} Result of script evaluation
     */
    window.require = function require(url) {
        try {
            var script = getUrl(url);
            if (!!script) {
                return evalScript(script);
            }
        } catch (err) {
            return {
                error: err
            };
        }
        return {};
    };

    /**
     * Load text file (html, css, ...) and returns the content.
     * @param url
     * @return {string} Content of text file, or empty string.
     */
    window.load = function load(url) {
        // console.log('load: ' + url);
        try {
            var text = getUrl(url);
            if (!!text) {
                return text;
            }
        } catch (err) {
            return err.toString();
        }
        return '';
    };

    // ------------------------------------------------------------------------
    //                      initialization
    // ------------------------------------------------------------------------

    window.defined = function defined(property) {
        if (typeof desktopgap != 'undefined') {
            return !!property ? desktopgap[property] : desktopgap;
        }
        return false;
    };

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
        disableContextMenu();
        disableDragAndDrop();
        initButtons();
        updateAreas();
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

    document.addEventListener('deviceready', function () {
        init();

        var bridge = defined('bridge');

        //-- set version --//
        window.version = bridge.version();

        //-- set ready state --//
        window.deviceready = true;

    }, false);

})(this);
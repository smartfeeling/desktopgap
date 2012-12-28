!(function (window) {

    var document = window.document;

    // ------------------------------------------------------------------------
    //                      utility
    // ------------------------------------------------------------------------

    function isFunction(func) {
        return Object.prototype.toString.call(func) == '[object Function]';
    }

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
        return undefined;
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
        var buttons = desktopgap.frame.buttons
            , close = buttons.close = document.getElementById('close')
            , fullscreen = buttons.fullscreen = document.getElementById('fullscreen')
            , minimize = buttons.minimize = document.getElementById('minimize')
            , maximize = buttons.maximize = document.getElementById('maximize')
            , maximize_glyph = document.getElementById('maximize-glyph');

        //-- fix focus bug --//
        fixButtonFocus(fullscreen, minimize, maximize);

        //-- buttons style --//
        fullscreen['data-state'] = normal;
        maximize['data-state'] = normal;

        //-- handle buttons click --//
        close.onclick = makeClickHandler('close');
        minimize.onclick = makeClickHandler('minimize');
        maximize.onclick = makeClickHandler('maximize', function () {
            console.log(maximize.src);
            if (maximize['data-state'] == normal) {
                maximize['data-state'] = clicked;
                maximize_glyph.src = './frame/glyph-exit-maximize.png';
            } else {
                maximize['data-state'] = normal;
                maximize_glyph.src = 'frame/glyph-maximize.png';
            }
        });
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
        fullscreen.addEventListener('click', function () {
            fullscreen.classList.remove('fullscreen');
            document.body.addEventListener('mouseover', function () {
                fullscreen.classList.add('fullscreen');
                document.body.removeEventListener('mouseover', arguments.callee, false);
            }, false);
        }, false);

        minimize.addEventListener('click', function () {
            minimize.classList.remove('minimize');
            document.body.addEventListener('mouseover', function () {
                minimize.classList.add('minimize');
                document.body.removeEventListener('mouseover', arguments.callee, false);
            }, false);
        }, false);

        maximize.addEventListener('click', function () {
            maximize.classList.remove('maximize');
            document.body.addEventListener('mouseover', function () {
                maximize.classList.add('maximize');
                document.body.removeEventListener('mouseover', arguments.callee, false);
            }, false);
        }, false);
    }

    //-- drag&drop, area and context menu --//

    function updateAreas() {

        if (defined()) {
            var content = document.getElementById('content') || {};
            var fullscreen = document.getElementById('fullscreen') || {};
            var minimize = document.getElementById('minimize') || {};
            var maximize = document.getElementById('maximize') || {};
            var close = document.getElementById('close') || {};
            var height = 27;
            var buttonPadding = 6;
            var margin = content.offsetLeft || 0;
            var leftOffset = 0;
            var closeOffset = close.offsetWidth || 0;
            var miniOffset = minimize.offsetWidth || 0;
            var rightOffset = closeOffset + miniOffset + buttonPadding; // 6 pixels of padding

            if (fullscreen.style.visibility == 'visible') {
                leftOffset = fullscreen.offsetWidth + buttonPadding;
            }

            if (maximize.style.visibility == 'visible') {
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

    //-- CONSOLE --//
    exports.console = require('desktopgap_console.js');

    //-- FRAME --//
    exports.frame = require('desktopgap_frame.js');

    //-- CONNECTION --//
    exports.connection = require('desktopgap_connection.js');
    exports.Connections = exports.connection.Connections;

    //-- DEVICE --//
    exports.device = require('desktopgap_device.js');

    //-- FILE --//
    exports.fileSystem = require('desktopgap_file.js');

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

    window.device = exports.device;

    window.Connections = exports.Connections;

    // ------------------------------------------------------------------------
    //                  i n i t i a l i z a t i o n
    // ------------------------------------------------------------------------

    document.addEventListener('deviceready', function () {
        init();

        //-- set ready state --//
        window.deviceready = true;

    }, false);

})(this);
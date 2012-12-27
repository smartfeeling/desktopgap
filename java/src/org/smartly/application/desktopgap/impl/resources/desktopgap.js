!(function (window) {

    var document = window.document;

    // ------------------------------------------------------------------------
    //                      initialization
    // ------------------------------------------------------------------------

    function defined(property) {
        if (typeof desktopgap != 'undefined') {
            return !!property ? desktopgap[property] : desktopgap;
        }
        return false;
    }

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
    //                      C O N S O L E
    // ------------------------------------------------------------------------

    var console = {

        init: function () {

            return this;
        },

        open: function () {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().open();
            }
        },

        log: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().log(message);
            }
        },

        error: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().error(message);
            }
        },

        warn: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().warn(message);
            }
        },

        info: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().info(message);
            }
        },

        debug: function (message) {
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().debug(message);
            }
        }
    };

    // ------------------------------------------------------------------------
    //                      F R A M E
    // ------------------------------------------------------------------------

    var frame = {

        init: function () {

            return this;
        },

        buttons: {},

        buttonClicked: function (name) {
            if (defined('bridge')) {
                desktopgap['bridge'].buttonClicked(name);
            }
        },

        setArea: function (name, left, top, right, height) {
            if (defined('bridge')) {
                name = name || 'undefined';
                left = parseFloat(left);
                top = parseFloat(top);
                right = parseFloat(right);
                height = parseFloat(height);
                desktopgap['bridge'].setArea(name, left, top, right, height);
            }
        }

    };

    // ------------------------------------------------------------------------
    //                      D E V I C E
    // ------------------------------------------------------------------------

    /**
     * The device object describes the device's hardware and software
     * <p/>
     * Properties
     * <p/>
     * <ul>
     *     <li>device.name</li>
     *     <li>device.platform</li>
     *     <li>device.uuid</li>
     *     <li>device.version</li>
     * </ul>
     * Variable Scope
     * <p/>
     * Since device is assigned to the window object, it is implicitly in the global scope.
     * <p/>
     * // These reference the same `device`
     * var phoneName = window.device.name;
     * var phoneName = device.name;
     */
    var device = {

        init: function () {
            var self = this;
            document.addEventListener('deviceready', function () {
                self['name'] = getDevice('name');
                self['platform'] = getDevice('platform');
                self['uuid'] = getDevice('uuid');
                self['version'] = getDevice('version');
            }, false);
            return self;
        },

        name: 'undefined',

        platform: 'undefined',

        uuid: 'undefined',

        version: 'undefined'

    };

    function getDevice(property) {
        var bridge = defined('bridge');
        if (!!bridge) {
            return bridge.device(property);
        }
        return 'undefined';
    }

    // ------------------------------------------------------------------------
    //                      C O N N E C T I O N
    // ------------------------------------------------------------------------

    var Connections = {
        UNKNOWN: 'UNKNOWN',
        ETHERNET: 'ETHERNET',
        NONE: 'NONE'
    };

    var connection = {

        init: function () {
            var self = this;
            document.addEventListener('deviceready', function () {
                self.refresh();
            }, false);
            return self;
        },

        type: Connections.UNKNOWN,

        refresh: function () {
            var bridge = defined('bridge');
            if (!!bridge) {
                this['type'] = bridge.connection().getType();
            }
        }

    };

    function getConnection() {
        var bridge = defined('bridge');
        if (!!bridge) {
            return bridge.connection().getType();
        }
        return Connections.UNKNOWN;
    }

    // ------------------------------------------------------------------------
    //                      F I L E
    // ------------------------------------------------------------------------


    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    var exports = window.desktopgap = window.dg = {};

    exports.console = console.init();

    exports.frame = frame.init();

    exports.connection = connection.init();
    exports.Connections = Connections;

    exports.device = device.init();

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
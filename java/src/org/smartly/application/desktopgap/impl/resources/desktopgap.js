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

    function makeClickHandler(buttonName) {
        return function (event, element) {
            event.cancelBubble = true;
            if (event.stopPropagation)
                event.stopPropagation();
            desktopgap.frame.buttonClicked(buttonName);
        };
    }

    function setupButtonHandlers() {
        document.getElementById('close').onclick = makeClickHandler('close');
        document.getElementById('minimize').onclick = makeClickHandler('minimize');
        document.getElementById('maximize').onclick = makeClickHandler('maximize');
        document.getElementById('fullscreen').onclick = makeClickHandler('fullscreen');
    }

    function updateAreas() {

        if (defined()) {
            var content = document.getElementById('content');
            var fullscreen = document.getElementById('fullscreen');
            var minimize = document.getElementById('minimize');
            var maximize = document.getElementById('maximize');
            var close = document.getElementById('close');
            var height = 27;
            var buttonPadding = 6;
            var margin = content.offsetLeft;
            var leftOffset = 0;
            var rightOffset = close.offsetWidth + minimize.offsetWidth + buttonPadding; // 6 pixels of padding

            if (fullscreen.style.visibility == 'visible') {
                leftOffset = fullscreen.offsetWidth + buttonPadding;
            }

            if (maximize.style.display == 'block') {
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

    function fixButtonFocus() {
        var fullscreen = document.getElementById('fullscreen');
        var minimize = document.getElementById('minimize');
        var maximize = document.getElementById('maximize');

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

    function init() {
        var imgs = [
            'frame/bg-reg.png', 'frame/bg-hov.png', 'frame/bg-down.png',
            'frame/bg-max-hov.png', 'frame/bg-max-reg.png', 'frame/bg-max-down.png',
            'frame/control-fullscreen-hover.png', 'frame/control-fullscreen-down.png'
        ];
        for (var i = 0, l = imgs.length; i < l; i++) {
            var img = new Image();
            img.src = imgs[i];
        }

        disableContextMenu();
        disableDragAndDrop();
        fixButtonFocus();
        setupButtonHandlers();
        updateAreas();
    }

    // ------------------------------------------------------------------------
    //                      C O N S O L E
    // ------------------------------------------------------------------------

    var console = {

        init: function () {

            return this;
        },

        open: function (){
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().open();
            }
        },

        log: function (message){
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().log(message);
            }
        },

        error: function (message){
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().error(message);
            }
        },

        warn: function (message){
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().warn(message);
            }
        },

        info: function (message){
            var bridge = defined('bridge');
            if (!!bridge) {
                bridge.console().info(message);
            }
        },

        debug: function (message){
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
        ETHERNET:'ETHERNET',
        NONE:'NONE'
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

        refresh: function(){
            var bridge = defined('bridge');
            if (!!bridge) {
                this['type'] = bridge.connection().getType();
            }
        }

    };

    function getConnection(){
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

    window.console = exports.console;

    window.device = exports.device;

    window.Connections = exports.Connections;

    // ------------------------------------------------------------------------
    //                  i n i t i a l i z a t i o n
    // ------------------------------------------------------------------------

    document.addEventListener('deviceready', function () {
        init();
    }, false);

})(this);
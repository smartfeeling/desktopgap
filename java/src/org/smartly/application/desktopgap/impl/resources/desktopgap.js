!(function (window) {

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

            return this;
        },

        name: getDevice('name'),

        platform: getDevice('platform'),

        uuid: getDevice('uuid'),

        version: getDevice('version')

    };

    function getDevice(property){
        var bridge = defined('bridge');
        if (!!bridge) {
           bridge.device().get(property);
        }
        return 'undefined';
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    var exports = window.desktopgap = window.dg = {};

    exports.frame = frame.init();

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

    window.device = exports.device;

    // ------------------------------------------------------------------------
    //                  i n i t i a l i z a t i o n
    // ------------------------------------------------------------------------

    document.addEventListener('deviceready', function () {
        init();
    }, false);

})(this);
!(function (window) {

    // ------------------------------------------------------------------------
    //                      initialization
    // ------------------------------------------------------------------------

    function defined(property){
        if (typeof desktopgap != 'undefined') {
            return !!property ? null!=desktopgap[property]:true;
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
            desktopgap.setFrameArea('dragbar',
                margin + leftOffset,
                margin,
                margin + rightOffset,
                height);
            // left, top, right, height

            // Setup app container region
            desktopgap.setFrameArea('app', margin + 1, height, margin + 1, margin + 1); // left, top, right, bottom

            // Setup app container region when app is maximized
            desktopgap.setFrameArea('appmaximized', margin, height - 1, margin, margin); // left, top, right, bottom

            desktopgap.setFrameArea('sizemargin', margin, margin, margin, margin);
            desktopgap.setFrameArea('sizetl', margin, 10, margin, 10);
            desktopgap.setFrameArea('sizetr', margin, 10, margin, 10);
            desktopgap.setFrameArea('sizebr', margin, 14, margin, 14);
            desktopgap.setFrameArea('sizebl', margin, 14, margin, 14);
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
    //                      E V E N T S
    // ------------------------------------------------------------------------

    var events = {

        init: function () {
            this._listeners = {};

            //-- declare main events --//
            this._listeners['ready'] = [];
            this._listeners['exit'] = [];

            return this;
        },

        on: function (event, callback) {
            // ensure event repo exists
            this._listeners[event] = this._listeners[event] || [];
            // add listener
            this._listeners[event].push(callback);
        },

        trigger: function (event, opt_args) {
            var self = this
                , listeners = this._listeners[event] || [];
            try {
                for (var i = 0; i < listeners.length; i++) {
                    try {
                        listeners[i].apply(self)
                    } catch (err) {
                    }
                }
            } catch (ignored) {
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
        }

    };

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    var exports = window.desktopgap = window.dg = {};

    exports.events = events.init();

    exports.frame = frame.init();

    // ------------------------------------------------------------------------
    //                  i n i t i a l i z a t i o n
    // ------------------------------------------------------------------------

    exports.events.on('ready', function () {
        init();
    });

})(this);
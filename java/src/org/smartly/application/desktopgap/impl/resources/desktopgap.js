!(function (window) {

    // ------------------------------------------------------------------------
    //                      initialization
    // ------------------------------------------------------------------------

    function makeClickHandler(buttonName) {
        return function (event, element) {
            event.cancelBubble = true;
            if (event.stopPropagation)
                event.stopPropagation();
            desktopgap.frameButtonClicked(buttonName);
        };
    }

    function setupButtonHandlers() {
        document.getElementById('close').onclick = makeClickHandler('close');
        document.getElementById('minimize').onclick = makeClickHandler('minimize');
        document.getElementById('maximize').onclick = makeClickHandler('maximize');
        document.getElementById('fullscreen').onclick = makeClickHandler('fullscreen');
    }

    function updateAreas() {

        if (typeof desktopgap != 'undefined') {
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
        var  imgs = [
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

    window.initdg = function(){
        alert('app: '  + foo.toString());
    };

    // ------------------------------------------------------------------------
    //                      listeners
    // ------------------------------------------------------------------------

    var listeners = {

        init: function () {
            this._listeners = [];

            return this;
        },

        add: function (callback) {
            this._listeners.push(callback);
        },

        notify: function () {
            var self = this;
            try {
                for (var i = 0; i < this._listeners.length; i++) {
                    try{ this._listeners[i].apply(self) }catch (err){}
                }
            } catch (ignored) {
            }
        }

    };

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    var exports = window.desktopgap = window.dg = {};

    exports.callbacks = listeners.init();


})(this);
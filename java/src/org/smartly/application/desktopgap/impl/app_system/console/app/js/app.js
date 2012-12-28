!(function (window) {

    var document = window.document
        , require = window['require']
        , imported = false
        ;


    // --------------------------------------------------------------------
    //               deviceready
    // --------------------------------------------------------------------

    document.addEventListener('data', function (evt) {
        var data = evt.data[0];
        if (!!window['deviceready']) {
            importComponents();
            onData(data);
        } else {
            document.addEventListener('deviceready', function () {
                importComponents();
                onData(data);
            }, false);
        }
    }, false);

    // --------------------------------------------------------------------
    //               console initialization
    // --------------------------------------------------------------------

    var console_comp;

    function importComponents(){
        if(!imported){

            require('./js/components/console/console.js');
            require('./js/components/level/level.js');
            require('./js/components/message/message.js');

            imported = true;
        }
    }

    /**
     * Data arrived to console.
     * @param data
     */
    function onData(data) {


        try {
            if (!console_comp) {
                console_comp = new desktopgap.gui.console.Console({
                    items: data
                });
                console_comp.appendTo('#app_content');
            } else {
                appendDataToConsole(data);
            }
        } catch (err) {
            console.error(err);
        }
    }

    function appendDataToConsole(data) {
        console.log('appendDataToConsole: ' + data);
    }

})(this);
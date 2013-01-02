!(function (window) {

    var document = window.document
        , desktopgap = window.desktopgap
        , require = window['require']
        , imported = false
        ;


    // --------------------------------------------------------------------
    //               deviceready
    // --------------------------------------------------------------------

    document.addEventListener('deviceready', function () {
        //-- import required scripts --//
        importComponents();

        initApp();

    }, false);

    function testLogger() {
        console.log('STARTED DESKTOP GAP!! ' + desktopgap['bridge'].toString());

        console.log('device.uuid: ' + device.uuid);

        console.log('navigator.connection.type: ' + navigator.connection.type);

        console.warn('test warning');

        console.error('test error');

        console.open();
    }

    // --------------------------------------------------------------------
    //               app initialization
    // --------------------------------------------------------------------

    function importComponents() {
        if (!imported) {

            // require('./js/components/console/console.js');


            imported = true;
        }
    }

    // --------------------------------------------------------------------
    //               app handlers
    // --------------------------------------------------------------------

    function initHandlers() {

        //-- close --//
        ly.el.click($('#btn_close'), function () {
            desktopgap.frame.minimize();
        });

    }

    // --------------------------------------------------------------------
    //               app
    // --------------------------------------------------------------------

    var app_version ='.app-version-id'
    ;

    function initApp(){
        //--  handlers --//
        initHandlers();

        // set version
        $(app_version).html(window.version);

        // localize
        i18n.translate();
    }

})(this);
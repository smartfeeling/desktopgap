!(function (window) {

    var document = window.document
        , desktopgap = window.desktopgap
        , registry = desktopgap['registry']
        , i18n = window['i18n']
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

    // --------------------------------------------------------------------
    //               app initialization
    // --------------------------------------------------------------------

    function importComponents() {
        if (!imported) {


            require('./js/app/Application.js');

            imported = true;
        }
    }


    // --------------------------------------------------------------------
    //               app
    // --------------------------------------------------------------------

    var application = null // main controller
        ;

    function initApp() {
        // localize
        i18n.translate();

        //-- components --//
        initComponents();


    }

    // --------------------------------------------------------------------
    //               app handlers
    // --------------------------------------------------------------------

    function initComponents() {
        try {

            if (!application) {
                application = new desktopgap.gui.Application({});
                application.appendTo('#app_content');
                application.show();
            }

        } catch (err) {
            console.error('(app.js) initComponents(): ' + err);
        }
    }

})(this);
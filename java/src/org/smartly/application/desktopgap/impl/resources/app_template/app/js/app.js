!(function(window){

    /**
     * Declare closure variables.
     */
    var document = window.document

        //-- desktopgap objects --//
        , desktopgap = window.desktopgap
        , registry = desktopgap['registry']
        , i18n = window['i18n']
        , require = window['require']

        //-- internal flag --//
        , imported = false


        ;

    // --------------------------------------------------------------------
    //               deviceready
    // --------------------------------------------------------------------

    /**
     * Handle device ready event emitted when desktopgap
     * runtime engine is ready.
     */
    document.addEventListener('deviceready', function () {
        //-- import required scripts --//
        importLibs();

        //-- bootstrap application --//
        initApp();
    }, false);

    // --------------------------------------------------------------------
    //               libs initialization
    // --------------------------------------------------------------------

    /**
     * Import script at runtime.
     * Here you can use desktopgap importer to load scripts
     * i.e.: require('./js/favorites.js');
     */
    function importLibs() {
        if (!imported) {


            //require('./js/favorites.js');

            imported = true;
        }
    }



    // --------------------------------------------------------------------
    //               app initialization
    // --------------------------------------------------------------------

    /**
     * Initialize localization and application.
     */
    function initApp() {
        // localize
        i18n.translate();

        //-- components --//
        initComponents();
    }

    // --------------------------------------------------------------------
    //              create components for application
    // --------------------------------------------------------------------

    function initComponents() {
        try {

        } catch (err) {
            console.error('(app.js) initComponents(): ' + err);
        }
    }


})(this);
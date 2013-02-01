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


            //require('./js/favorites.js');

            imported = true;
        }
    }



    // --------------------------------------------------------------------
    //               app
    // --------------------------------------------------------------------

    var app_version = '.app-version-id'
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
            Ext.require('Ext.container.Viewport');

            Ext.application({

                name: 'Dev',
                appFolder:'js/app',

                autoCreateViewport:true,

                controllers: [
                    'Pages'
                ],

                launch: function() {

                }
            });
        } catch (err) {
            console.error('(app.js) initComponents(): ' + err);
        }
    }

})(this);
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

            require('./js/components/pages/controller.js');
            require('./js/components/pages/apps/apps.js');
            require('./js/components/pages/help/help.js');
            require('./js/components/pages/task/task.js');
            require('./js/components/pages/tool/tool.js');
            require('./js/components/pages/tool/dev/tool_dev.js');

            require('./js/components/list/list.js');
            require('./js/components/tile/tile.js');
            require('./js/components/actionbutton/actionbutton.js');

            require('./js/favorites.js');

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

        } catch (err) {
            console.error('(app.js) initComponents(): ' + err);
        }
    }

})(this);
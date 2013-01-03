!(function (window) {

    var document = window.document
        , desktopgap = window.desktopgap
        , i18n = window['i18n']
        , require = window['require']

        , imported = false
        , pages_controller

        , mnu_apps = 'mnu_apps'
        , mnu_help = 'mnu_help'
        , mnu_task = 'mnu_task'
        , mnu_tool = 'mnu_tool'

        , sel_btn_close = '#btn_close'
        , sel_menu = '.app-menu li'
        , sel_mnu_apps = '#' + mnu_apps
        , sel_mnu_help = '#' + mnu_help
        , sel_mnu_task = '#' + mnu_task
        , sel_mnu_tool = '#' + mnu_tool
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

            require('./js/components/pages/controller.js');
            require('./js/components/pages/apps/apps.js');
            require('./js/components/pages/help/help.js');
            require('./js/components/pages/task/task.js');
            require('./js/components/pages/tool/tool.js');


            imported = true;
        }
    }

    // --------------------------------------------------------------------
    //               app handlers
    // --------------------------------------------------------------------

    function initComponents() {
        try {
            //-- creates pages controller --//
            if (!pages_controller) {
                var $panel = $('.page-content');
                $panel.html('');
                // create pages object
                var pages = {};
                pages[mnu_apps] = desktopgap.gui.pages.PageApps;
                pages[mnu_help] = desktopgap.gui.pages.PageHelp;
                pages[mnu_task] = desktopgap.gui.pages.PageTask;
                pages[mnu_tool] = desktopgap.gui.pages.PageTool;

                pages_controller = new desktopgap.gui.pages.PagesController({
                    pages: pages
                });
                pages_controller.appendTo($panel);
            }
        } catch (err) {
            console.error('(app.js) initComponents(): ' + err);
        }
    }

    function initHandlers() {

        //-- close --//
        ly.el.click($(sel_btn_close), function () {
            desktopgap.frame.minimize();
        });

        //-- menu --//
        ly.el.click($(sel_mnu_apps), function () {
            mnuClick(this);
        });
        ly.el.click($(sel_mnu_help), function () {
            mnuClick(this);
        });
        ly.el.click($(sel_mnu_task), function () {
            mnuClick(this);
        });
        ly.el.click($(sel_mnu_tool), function () {
            mnuClick(this);
        });
    }

    function mnuClick($elem) {
        //-- remove selection --//
        $(sel_menu).removeClass('selected');

        //-- add selection to current item--//
        $elem.addClass('selected');

        var id = $elem.attr('id')
            ;
        pages_controller.open(id);
    }

    // --------------------------------------------------------------------
    //               app
    // --------------------------------------------------------------------

    var app_version = '.app-version-id'
        ;

    function initApp() {
        // set version
        $(app_version).html(window.version);

        // localize
        i18n.translate();

        //-- components --//
        initComponents();

        //--  handlers --//
        initHandlers();

        //-- show firs window--//
        if (desktopgap.runtime.hasApps()) {
            // some applications installed
            pages_controller.open(mnu_apps);
            $(sel_mnu_apps).addClass('selected');
        } else {
            // no applications installed
            pages_controller.open(mnu_help);
            $(sel_mnu_help).addClass('selected');
        }

    }

})(this);
!(function (window) {

    var document = window.document
        , desktopgap = window.desktopgap
        , i18n = window['i18n']
        , require = window['require']

        , EVENT_CLICK = 'click'
        , EVENT_FAV = 'favorite'
        , EVENT_ACTION = 'action'

        , imported = false
        , pages_controller

        , mnu_apps = 'mnu_apps'
        , mnu_help = 'mnu_help'
        , mnu_task = 'mnu_task'
        , mnu_tool = 'mnu_tool'
        , mnu_tool_dev = 'mnu_tool_dev'

        , sel_btn_close = '#btn_close'
        , sel_menu = '.app-menu li'
        , sel_pagetitle = '.page-title'
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
            require('./js/components/pages/tool/dev/tool_dev.js');

            require('./js/components/list/list.js');
            require('./js/components/tile/tile.js');

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

                // root pages
                pages[mnu_apps] = desktopgap.gui.pages.PageApps;
                pages[mnu_help] = desktopgap.gui.pages.PageHelp;
                pages[mnu_task] = desktopgap.gui.pages.PageTask;
                pages[mnu_tool] = desktopgap.gui.pages.PageTool;

                // sub pages
                pages[mnu_tool_dev] = desktopgap.gui.pages.PageToolDev;

                //-- creates controller component --//
                pages_controller = new desktopgap.gui.pages.PagesController({
                    pages: pages
                });
                pages_controller.appendTo($panel);

                //-- handle controller events --//
                pages_controller.on(EVENT_CLICK, function(item){
                     var mnu_id = item['_id'];
                    if(!!mnu_id){
                       openPage(mnu_id);
                    }
                });
                pages_controller.on(EVENT_ACTION, function(action, item){
                    // clicked action
                    var actionId = action['_id']; // details (App details)
                });
                pages_controller.on(EVENT_FAV, function(item){
                    // add item to favorites
                    // TODO: send item to favorite Page
                });
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
        openPage(id);
    }

    function openPage(pageId, argsArray){
        var title = pages_controller.title(pageId)
            ;
        pages_controller.open(pageId, argsArray);
        $(sel_pagetitle).html(title);
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
            $(sel_mnu_apps).addClass('selected');
            openPage(mnu_apps);
        } else {
            // no applications installed
            $(sel_mnu_help).addClass('selected');
            openPage(mnu_help);
        }
    }

})(this);
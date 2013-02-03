!(function (window) {

    var require = window['require']

        , sel_page = '#page-<%= cid %>'
        , sel_toolbar = '#toolbar-<%= cid %>'

        , toolbar
        , page_system       // memory, cpu usage, etc..
        , page_application  // create, compile, install
        , page_util         // open folders (store, settings, )
        , page_settings     // enable/disable debug mode on applications, kiosk-mode
        ;

    function Application(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/app/Application.html'),
            model: false,
            view: false
        });

        this['_pages'] = [];

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Application, ly.Gui);

    Application.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
        require('./js/app/ApplicationToolbar.js');

        require('./js/app/pages/system/PageSystem.js');
        require('./js/app/pages/util/PageUtil.js');

        console.debug('Application.js: Loaded all required libraries.');
    }

    function _initComponents(callback) {
        var self = this
            ;
        try {
            //-- init pages --//
            self.bindTo(_initPages)();

            //-- init toolbar --//
            self.bindTo(_initToolbar)();

            //-- show first page --//
            toolbar.doClick(page_system);
        } catch (err) {
            console.error('(Application.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    function _initPages() {
        var self = this
            , $parent = $(self.template(sel_page))
            ;
        try {

            //-- page_system --//
            if (!page_system) {
                page_system = new desktopgap.gui.pages.PageSystem({});
                page_system.appendTo($parent);
                page_system.hide();
                self['_pages'].push(page_system);
            }

            //-- page_util --//
            if (!page_util) {
                page_util = new desktopgap.gui.pages.PageUtil({});
                page_util.appendTo($parent);
                page_util.hide();
                self['_pages'].push(page_util);
            }

        } catch (err) {
            console.error('(Application.js) _initPages(): ' + err);
        }
    }

    function _initToolbar() {
        var self = this
            , $parent = $(self.template(sel_toolbar))
            ;
        try {
            if (!toolbar) {
                toolbar = new desktopgap.gui.ApplicationToolbar({items:self['_pages']});
                toolbar.appendTo($parent);
                toolbar.on('click', function(item){
                    self.bindTo(_show)(item);
                });
            }
        } catch (err) {
            console.error('(Application.js) _initToolbar( ): ' + err);
        }
    }

    function _show(page) {
        var self = this;

        // console.debug('Showing: ' + page);

        //-- hide pages --//
        _.forEach(self['_pages'], function (item) {
            item.hide();
        });

        page.fadeIn();
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.Application');
    desktopgap.gui.Application = Application;

    return desktopgap.gui.Application;

})(this);
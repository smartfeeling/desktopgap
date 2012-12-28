(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- tabs --//
        , sel_tabs = '#tab-<%= cid %> a'
        , sel_tab_all = '#ALL-<%= cid %>'
        , sel_tab_info = '#INFO-<%= cid %>'
        , sel_tab_warn = '#WARNING-<%= cid %>'
        , sel_tab_err = '#ERROR-<%= cid %>'
        ;

    function Console(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/console/console.html'),
            model: false,
            view: false
        });

        this['_items'] = options['items'];
        this['_panels'] = {};

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Console, ly.Gui);

    Console.prototype.items = function (items) {
        if (!!items) {
            this['_items'] = items;
            this.bindTo(_refresh)();
        }
        return this['_items'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , $tab = $(self.template(sel_tabs))
            ;

        ly.el.click($tab, function () {
            $(this).tab('show');
        });

        this.bindTo(_refresh)();
    }

    function _refresh() {
        var self = this

        // items
            , items = self['_items']
            , ALL = items['ALL'] || []
            , INFO = items['INFO'] || []
            , WARN = items['WARNING'] || []
            , ERR = items['ERROR'] || []

        // selectors
            , tab_all = self.template(sel_tab_all)
            , tab_info = self.template(sel_tab_info)
            , tab_warn = self.template(sel_tab_warn)
            , tab_err = self.template(sel_tab_err)
            ;

        try{
            //-- ALL --//
            self.bindTo(getPanel)(tab_all).items(ALL);

        } catch(err){
            console.error('(console.js) Error loading items: ' + err);
        }


        $(self.template(sel_self)).fadeIn();
    }

    function getPanel(tab_selector) {
        var self = this
            , panels = self['_panels']
            ;

        console.info('(console.js) Getting panel: ' + tab_selector);

        if (!panels[tab_selector]) {
            console.info('(console.js) Creating panel: ' + tab_selector);
            $(tab_selector).html('');
            panels[tab_selector] = new desktopgap.gui.level.Level({});
            panels[tab_selector].appendTo(tab_selector);
        }
        return panels[tab_selector];
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.console.Console');
    desktopgap.gui.console.Console = Console;

    return desktopgap.gui.console.Console;

})(this);
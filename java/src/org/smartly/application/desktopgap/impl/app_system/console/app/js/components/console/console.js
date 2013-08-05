(function (window) {

    'use strict';

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'
        , sel_footer = '#footer-<%= cid %>'

    //-- tabs --//
        , sel_tabs = '#tab-<%= cid %> a'
        , sel_tab_all = '#ALL-<%= cid %>'
        , sel_tab_fine = '#FINE-<%= cid %>'
        , sel_tab_info = '#INFO-<%= cid %>'
        , sel_tab_warn = '#WARNING-<%= cid %>'
        , sel_tab_err = '#ERRORS-<%= cid %>'
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

    Console.prototype.appendData = function (items) {
        if (!!items) {
            ly.deepExtend(this['_items'], items);
            this.bindTo(_load)(items);
            this.bindTo(_scrollBottom)();
        }
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

        this.bindTo(_load)(self['_items']);
    }

    function _load(items) {
        var self = this

        // items
            , ALL = items['ALL'] || []
            , INFO = items['INFO'] || []
            , WARN = items['WARNING'] || []
            , ERR = items['SEVERE'] || []
            , FINE = items['FINE'] || []

        // selectors
            , tab_all = self.template(sel_tab_all)
            , tab_info = self.template(sel_tab_info)
            , tab_warn = self.template(sel_tab_warn)
            , tab_err = self.template(sel_tab_err)
            , tab_fine = self.template(sel_tab_fine)
            ;

        try {
            //-- ALL --//
            self.bindTo(getPanel)(tab_all).items(ALL);
            //-- INFO --//
            self.bindTo(getPanel)(tab_info).items(INFO);
            //-- WARN --//
            self.bindTo(getPanel)(tab_warn).items(WARN);
            //-- ERROR --//
            self.bindTo(getPanel)(tab_err).items(ERR);
            //-- FINE --//
            self.bindTo(getPanel)(tab_fine).items(FINE);
        } catch (err) {
            console.error('(console.js) Error loading items: ' + err);
        }


        $(self.template(sel_self)).fadeIn();
    }

    function getPanel(tab_selector) {
        var self = this
            , panels = self['_panels']
            ;

        // console.info('(console.js) Getting panel: ' + tab_selector);

        if (!panels[tab_selector]) {
            // console.info('(console.js) Creating panel: ' + tab_selector);
            $(tab_selector).html('');
            panels[tab_selector] = new desktopgap.gui.level.Level({});
            panels[tab_selector].appendTo(tab_selector);
        }
        return panels[tab_selector];
    }

    function _scrollBottom(){
        var self = this;
        _.debounce(function(){
            try{
                var $bottom = $(self.template(sel_footer));
                ly.el.scrollTo($bottom);
            }catch(err){
                console.error('(console.js) _scrollBottom(): ' + err);
            }
        }, 500, false)();
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.console.Console');
    desktopgap.gui.console.Console = Console;

    return desktopgap.gui.console.Console;

})(this);
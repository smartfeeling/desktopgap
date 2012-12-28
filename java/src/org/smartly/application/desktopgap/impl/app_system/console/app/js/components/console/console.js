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
        var self = this;

        ly.base(this, {
            template: load('./js/components/console/console.html'),
            model:false,
            view:false
        });

        this['_items'] = options['items'];

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

        ly.el.click($tab, function(){
            $(this).tab('show');
        });

        this.bindTo(_refresh)();
    }

    function _refresh() {
        var self = this
            , $self = $(self.template(sel_self))
            , items = self['_items']
            ;


        $self.fadeIn();
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.console.Console');
    desktopgap.gui.console.Console = Console;

    return desktopgap.gui.console.Console;

})(this);
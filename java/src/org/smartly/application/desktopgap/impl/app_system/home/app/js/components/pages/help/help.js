(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- tabs --//
        , sel_tabs = '#tab-<%= cid %> a'
        ;

    function PageHelp(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/help/help.html'),
            model: false,
            view: false
        });


        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageHelp, ly.Gui);

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this

            ;


    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageHelp');
    desktopgap.gui.pages.PageHelp = PageHelp;

    return desktopgap.gui.pages.PageHelp;

})(this);
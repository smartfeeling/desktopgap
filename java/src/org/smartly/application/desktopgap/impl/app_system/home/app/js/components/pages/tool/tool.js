(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- tabs --//
        , sel_tabs = '#tab-<%= cid %> a'
        ;

    function PageTool(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/tool/tool.html'),
            model: false,
            view: false
        });


        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageTool, ly.Gui);

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

    ly.provide('desktopgap.gui.pages.PageTool');
    desktopgap.gui.pages.PageTool = PageTool;

    return desktopgap.gui.pages.PageTool;

})(this);
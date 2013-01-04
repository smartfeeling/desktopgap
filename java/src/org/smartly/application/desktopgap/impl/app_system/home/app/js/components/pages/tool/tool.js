(function (window) {

    var desktopgap = window.desktopgap
        , i18n = desktopgap['i18n']
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

        i18n.translate(self['parent'][0]);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageTool');
    desktopgap.gui.pages.PageTool = PageTool;

    return desktopgap.gui.pages.PageTool;

})(this);
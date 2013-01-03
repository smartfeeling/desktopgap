(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , sel_self = '#<%= cid %>'

    //-- tabs --//
        , sel_tabs = '#tab-<%= cid %> a'
        ;

    function PageApps(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/apps/apps.html'),
            model: false,
            view: false
        });


        // add listeners
        this.on('init', function(){
            _.delay(function(){
               self.bindTo(_init)();
            }, 100);
        });
    }

    ly.inherits(PageApps, ly.Gui);

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , names = runtime.appNames()
            ;


    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageApps');
    desktopgap.gui.pages.PageApps = PageApps;

    return desktopgap.gui.pages.PageApps;

})(this);
(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']
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

    PageApps.prototype.title = function () {
        return i18n.get('home.applications');
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , names = runtime.appNames()
            ;

        i18n.translate(self['parent'][0]);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageApps');
    desktopgap.gui.pages.PageApps = PageApps;

    return desktopgap.gui.pages.PageApps;

})(this);
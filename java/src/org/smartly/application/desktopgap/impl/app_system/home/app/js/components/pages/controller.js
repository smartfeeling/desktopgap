(function (window) {

    function PagesController() {
        var self = this
            ;

        ly.base(self, {
            template: '',
            model: false,
            view: false
        });


        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PagesController, ly.Gui);

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

    ly.provide('desktopgap.gui.pages.PagesController');
    desktopgap.gui.pages.PagesController = PagesController;

    return desktopgap.gui.pages.PagesController;

})(this);
(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- tabs --//
        , sel_tabs = '#tab-<%= cid %> a'
        ;

    function PageTask(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/task/task.html'),
            model: false,
            view: false
        });


        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageTask, ly.Gui);

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

    ly.provide('desktopgap.gui.pages.PageTask');
    desktopgap.gui.pages.PageTask = PageTask;

    return desktopgap.gui.pages.PageTask;

})(this);
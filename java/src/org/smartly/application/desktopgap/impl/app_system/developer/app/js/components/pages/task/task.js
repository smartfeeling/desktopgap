(function (window) {

    var desktopgap = window.desktopgap
        , i18n = desktopgap['i18n']
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

    PageTask.prototype.title = function () {
        return i18n.get('home.task_manager');
    };

    PageTask.prototype.setData = function (argsArray) {

    };

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

    ly.provide('desktopgap.gui.pages.PageTask');
    desktopgap.gui.pages.PageTask = PageTask;

    return desktopgap.gui.pages.PageTask;

})(this);
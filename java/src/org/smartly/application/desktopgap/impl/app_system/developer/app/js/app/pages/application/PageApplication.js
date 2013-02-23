(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']

        , sel_self = '#<%= cid %>'
        ;

    function PageApplication(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/app/pages/application/PageApplication.html'),
            model: false,
            view: false
        });

        this['_title'] = i18n.get('dictionary.page_application');

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageApplication, ly.Gui);

    PageApplication.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageApplication.prototype.title = function () {
        // console.log('TITLE: ' + this['_title']);
        return this['_title'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;


    }

    function _initComponents(callback) {
        var self = this
            , $self = $(self.template(sel_self))
            ;
        try {

        } catch (err) {
            console.error('(PageApplication.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageApplication');
    desktopgap.gui.pages.PageApplication = PageApplication;

    return desktopgap.gui.pages.PageApplication;

})(this);
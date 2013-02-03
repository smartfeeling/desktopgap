(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']

        , sel_self = '#<%= cid %>'
        ;

    function PageSystem(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/app/pages/system/PageSystem.html'),
            model: false,
            view: false
        });

        this['_title'] = i18n.get('dictionary.page_system');

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageSystem, ly.Gui);

    PageSystem.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageSystem.prototype.title = function () {
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
            console.error('(PageSystem.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageSystem');
    desktopgap.gui.pages.PageSystem = PageSystem;

    return desktopgap.gui.pages.PageSystem;

})(this);
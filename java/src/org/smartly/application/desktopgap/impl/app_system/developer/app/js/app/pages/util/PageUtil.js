(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']

        , sel_self = '#<%= cid %>'
        ;

    function PageUtil(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/app/pages/util/PageUtil.html'),
            model: false,
            view: false
        });

        this['_title'] = i18n.get('dictionary.page_util');

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageUtil, ly.Gui);

    PageUtil.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageUtil.prototype.title = function () {
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
            console.error('(PageUtil.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageUtil');
    desktopgap.gui.pages.PageUtil = PageUtil;

    return desktopgap.gui.pages.PageUtil;

})(this);
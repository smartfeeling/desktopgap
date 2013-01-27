(function (window) {

    var desktopgap = window.desktopgap
        , i18n = desktopgap['i18n']

        , EVENT_CLICK = 'click'
        , EVENT_FAV = 'favorite'

        , sel_list = '#list-<%= cid %>'

    // sub-pages
        , mnu_tool_dev = 'mnu_tool_dev'

        ;


    function PageToolDev(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/tool/dev/tool_dev.html'),
            model: false,
            view: false
        });


        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageToolDev, ly.Gui);

    PageToolDev.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageToolDev.prototype.title = function () {
        return i18n.get('home.tools') + ' - ' + i18n.get('home.tools_dev');
    };

    PageToolDev.prototype.setData = function (argsArray) {

    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
        //i18n.translate(self['parent'][0]);
        try {
            self['items'] = [];
            // developers
            self['items'].push({
                _id: 'mnu_tool_dev',
                description: i18n.get('home.tools_dev'),
                image: ''
            });
        } catch (err) {
            console.error('(tool_dev.js) _init(): ' + err);
        }
    }

    function _initComponents() {
        try {
            var self = this
                , $list_parent = $(self.template(sel_list))
                ;

            var list = new desktopgap.gui.list.List({items: self['items'], fav: true});
            list.appendTo($list_parent, function () {
                list.on(EVENT_CLICK, function (item) {
                   self.trigger(EVENT_CLICK, item);
                });
                list.on(EVENT_FAV, function (item) {
                    self.trigger(EVENT_FAV, item);
                });
            });
        } catch (err) {
            console.error('(tool_dev.js) _initComponents(): ' + err);
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageToolDev');
    desktopgap.gui.pages.PageToolDev = PageToolDev;

    return desktopgap.gui.pages.PageToolDev;

})(this);
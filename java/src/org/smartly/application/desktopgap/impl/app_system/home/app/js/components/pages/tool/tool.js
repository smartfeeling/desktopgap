(function (window) {

    var desktopgap = window.desktopgap
        , i18n = desktopgap['i18n']

        , EVENT_CLICK = 'click'
        , EVENT_FAV = 'favorite'
        , EVENT_ACTION = 'action'

        , sel_list = '#list-<%= cid %>'

    // sub-pages
        , mnu_tool_dev = 'mnu_tool_dev'

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

    PageTool.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageTool.prototype.title = function () {
        return i18n.get('home.tools');
    };

    PageTool.prototype.setData = function (argsArray) {

    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
        //i18n.translate(self['parent'][0]);
        try {
            //-- add items to list --//
            self['items'] = [];

            // developers
            self['items'].push({
                _id: 'act_tool_dev',
                description: i18n.get('home.tools_dev'),
                image: '',
                action: true,
                command: 'desktopgap.runtime.runApp("system_developer")'
            });

        } catch (err) {
            console.error('(tool.js) _init(): ' + err);
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
                list.on(EVENT_ACTION, function (item) {
                    self.trigger(EVENT_ACTION, item);
                });
                list.on(EVENT_FAV, function (item) {
                    self.trigger(EVENT_FAV, item);
                });
            });
        } catch (err) {
            console.error('(tool.js) _initComponents(): ' + err);
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageTool');
    desktopgap.gui.pages.PageTool = PageTool;

    return desktopgap.gui.pages.PageTool;

})(this);
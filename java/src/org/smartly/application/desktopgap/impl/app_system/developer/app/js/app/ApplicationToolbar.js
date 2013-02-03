!(function (window) {

    var EVENT_CLICK = 'click'

        , sel_title = '#title-<%= cid %>'
        , sel_menu = '#menu-<%= cid %>'
        , sel_tpl = '#tpl-<%= cid %>'
        ;

    function ApplicationToolbar(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/app/ApplicationToolbar.html'),
            model: false,
            view: false
        });

        this['_items'] = options['items'];
        this['_items_map'] = {};

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(ApplicationToolbar, ly.Gui);

    ApplicationToolbar.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    ApplicationToolbar.prototype.doClick = function (page) {
        var self = this;
        if (!!page) {
            // remove selection
            $('li', self.template(sel_menu)).removeClass('active');
            // select current
            var title = !!page.title ? page.title() : ''
                , id = page['cid']
                , elem = self['_items_map']['elem']
                ;
            $(elem).addClass('active');
            $(self.template(sel_title)).html(title);
            this.trigger(EVENT_CLICK, page);
        }
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
            , items = self['_items']
            , $menu = $(self.template(sel_menu))
            , tpl =  $(self.template(sel_tpl)).text()
            ;
        try {
            //console.debug(items);
            _.forEach(items, function (item) {
                var title = !!item.title?item.title():'title-not-defined'
                    , id = item['cid']
                    , $item_tpl = $(ly.template(tpl, {id:id, name:title}))
                    ;
                //console.debug(encodeURI(tpl));
                self['_items_map'][id] = {item:item, elem:$item_tpl};
                $menu.append($item_tpl);
                $item_tpl.find('a').on('click', function(){
                    console.debug('clicked: ' + id + ' ' + title);
                });
            });
        } catch (err) {
            console.error('(ApplicationToolbar.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }


    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.ApplicationToolbar');
    desktopgap.gui.ApplicationToolbar = ApplicationToolbar;

    return desktopgap.gui.ApplicationToolbar;

})(this);
(function () {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- items --//
        , tpl_link = '<li><a href="#" data="{id}">{value}</a> <span class="divider">/</span></li>'
        , tpl_active = '<li class="active">{value}</li>'
        ;

    function Breadcrumbs(options) {
        var self = this;

        ly.base(this, {
            template:'/assets/lib/bootstrap/widgets/breadcrumbs/breadcrumbs.vhtml',
            model:false,
            view:false
        });

        this['_items'] = _parseItems(options['items']);
        this['_active'] = null != options['active'] ? parseInt(options['active']) : 0;

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Breadcrumbs, ly.Gui);

    Breadcrumbs.prototype.items = function (items) {
        if (!!items) {
            this['_items'] = _parseItems(items);
            this.bindTo(_refresh)();
        }
        return this['_items'];
    };

    Breadcrumbs.prototype.active = function (active) {
        if (null != active) {
            this['_active'] = parseInt(active);
            this.bindTo(_refresh)();
        }
        return this['_active'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
        this.bindTo(_refresh)();
    }

    function _refresh() {
        var self = this
            , $self = $(self.template(sel_self))
            , items = self['_items']
            , active = _.isNaN(self['_active']) ? 0 : self['_active']
            , len = active+1
            ;
        $self.html('');
        //-- creates components --//
        for (var i = 0; i < len; i++) {
            var tpl = ly.template(i === len - 1 ? tpl_active : tpl_link, items[i]);
            $self.append(tpl);
        }
        //-- listener --//
        $self.find('a').on('click', function () {
            var id = parseInt($(this).attr('data'));
            self.trigger(EVENT_CLICK, items[id]);
            return false;
        });
    }

    function _parseItems(items) {
        var result = [];
        if (_.isString(items)) {
            items = items.split(',');
        }
        if (_.isArray(items)) {
            for (var i = 0; i < items.length; i++) {
                result.push({id:i, value:items[i]});
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.Breadcrumbs');
    ly.gui.widgets.Breadcrumbs = Breadcrumbs;

    return ly.gui.widgets.Breadcrumbs;
})();

(function () {

    lyb.require('/assets/lib/bootstrap/widgets/imagelist/imagelist.mini.css');

    var EVENT_CLICK = 'click'
        , TPL_ITEM =
            '<li data_id="{_id}" class="span4">' +
                '<div id="{_id}" class="thumbnail clickable">' +
                    '<img src="{image}"  alt=""/>' +
                    '<div class="imagelist-textbox back-gradient">' +
                        '<div class="imagelist-name">{name}</div>' +
                        '<div class="imagelist-description">{description}</div>' +
                    '</div>' +
                '</div>' +
            '</li>'
        , sel_self = '#<%= cid %>'
        , sel_thumbbuttons = '#<%= cid %> .clickable'
        , sel_template = '#template-<%= cid %>'

        ;

    function Imagelist(options) {
        var self = this
            ;

        ly.base(self, {
            template: '/assets/lib/bootstrap/widgets/imagelist/imagelist.vhtml',
            model: false,
            view: true
        });

        options = options || {};

        self['_items'] = options['items'];
        self['_selected'] = "";

        // add listeners
        self.on('init', _init);
    }

    ly.inherits(Imagelist, ly.Gui);

    Imagelist.prototype.select = function (item, emitEvent) {
        this.bindTo(_select)(item, emitEvent);
    };

    Imagelist.prototype.selected = function () {
        return this['_selected'];
    };

    Imagelist.prototype.items = function (items) {
        if (!!items) {
            this['_items'] = items;
            this.bindTo(_init)();
        }
        return this['_items'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , $self = $(self.template(sel_self))
            , template = TPL_ITEM // $(self.template(sel_template)).text()
            ;

        $self.html('');

        // creates items
        _.forEach(self['_items'], function (item) {
            var $item = $(ly.template(template, item));
            $item.appendTo($self);
        });

        // handler
        $(self.template(sel_thumbbuttons)).on('click', function (e) {
            e.stopImmediatePropagation();
            return self.bindTo(_clickItem)(this);
        });
    }

    function _clickItem(component) {

        try {
            var id = $(component).attr('id')
                ;
            // selected
            this.bindTo(_select)(id, true);
        } catch (err) {
            ly.console.error(err);
        }
        return false;
    }

    function _itemById(id) {
        var result;
        _.forEach(this['_items'], function (item) {
            if (item['_id'] === id) {
                result = item;
                return false;
            }
        });
        return result;
    }

    function _select(item_or_string, emitevent) {
        var self = this;
        _.delay(function () {
            var item = _.isString(item_or_string) ? self.bindTo(_itemById)(item_or_string) : item_or_string
                , $item = $('#' + item['_id'])
                ;
            var old_selection = self['_selected'];
            self['_selected'] = item;
            // selected
            $(self.template(sel_thumbbuttons)).parent().removeClass('active');
            $item.parent().addClass('active');

            emitevent = !!emitevent ? emitevent : !!old_selection ? old_selection['_id'] !== item['_id'] : true;

            if (!!emitevent) {
                self.trigger(EVENT_CLICK, item);
            }
        }, 100);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.Imagelist');
    ly.gui.widgets.Imagelist = Imagelist;

})();

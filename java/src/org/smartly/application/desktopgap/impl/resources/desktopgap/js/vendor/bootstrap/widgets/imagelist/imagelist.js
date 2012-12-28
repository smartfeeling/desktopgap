(function () {

    lyb.require('/assets/lib/bootstrap/widgets/imagelist/imagelist.mini.css');

    var EVENT_CLICK = 'click'

        , sel_self = '#<%= cid %>'
        , sel_thumbbuttons = '#<%= cid %> .clickable'
        , sel_template = '#template-<%= cid %>'

        ;

    function Imagelist(options) {
        var self = this
            ;

        ly.base(this, {
            template:'/assets/lib/bootstrap/widgets/imagelist/imagelist.vhtml',
            model:false,
            view:true
        });

        this['_items'] = options['items'];
        this['_selected'] = "";

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Imagelist, ly.Gui);

    Imagelist.prototype.select = function (item, emitEvent) {
        this.bindTo(_select)(item, emitEvent);
    };

    Imagelist.prototype.selected = function () {
        return this['_selected'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , $self = $(self.template(sel_self))
            , template = $(self.template(sel_template)).text()
            ;

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
        _.delay(function(){
            var item = _.isString(item_or_string) ? self.bindTo(_itemById)(item_or_string) : item_or_string
                , $item = $('#' + item['_id'])
                ;
            var old_selection =  self['_selected'];
            self['_selected'] = item;
            // selected
            $(self.template(sel_thumbbuttons)).parent().removeClass('active');
            $item.parent().addClass('active');

            emitevent = !!emitevent ? emitevent : !!old_selection? old_selection['_id']!==item['_id'] :true;

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

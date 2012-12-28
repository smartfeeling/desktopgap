(function () {

    lyb.require('/assets/lib/bootstrap/widgets/imagecombo/imagecombo.mini.css');

    var EVENT_CHANGE = 'change'

        , sel_self = '#<%= cid %>'
        , sel_icon = '#icon-<%= cid %>'
        , sel_thumb = '#thumb-<%= cid %>'
        , sel_thumb_image = '#thumb_image-<%= cid %>'
        , sel_thumb_description = '#thumb_description-<%= cid %>'
        , sel_template = '#template-<%= cid %>'

        ;

    function Imagecombo(options) {
        var self = this
            ;

        ly.base(this, {
            template:'/assets/lib/bootstrap/widgets/imagecombo/imagecombo.vhtml',
            model:false,
            view:true
        });

        this['_items'] = options['items'];
        this['_field_id'] = options['field_id'] || '_id';
        this['_field_label'] = options['field_label'] || 'name';
        this['_field_description'] = options['field_description'] || 'description';
        this['_field_image'] = options['field_image'] || 'image';
        this['_icon'] = options['icon'] || 'icon-asterisk';
        this['_size'] = options['size'] || 'input-medium';
        this['_selected'] = "";

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Imagecombo, ly.Gui);

    Imagecombo.prototype.select = function (item, emitEvent) {
        this.bindTo(_select)(item, emitEvent);
    };

    Imagecombo.prototype.selected = function () {
        return this['_selected'];
    };

    Imagecombo.prototype.items = function (items) {
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
            , $icon = $(self.template(sel_icon))
            , template = $(self.template(sel_template)).text()
            , field_id = self['_field_id']
            , field_label = self['_field_label']
            ;

        $self.html('');
        // creates items
        _.forEach(self['_items'], function (item) {
            var data = {_id:item[field_id], name:item[field_label]};
            var $item = $(ly.template(template, data));
            $item.appendTo($self);
        });

        // handler
        $self.unbind();
        $self.on('change', function (e) {
            e.stopImmediatePropagation();
            return self.bindTo(_changeItem)(this);
        });

        // icon
        $icon.addClass(self['_icon']);

        // size
        var size = self['_size'];
        if(size.indexOf('px')>-1 || size.indexOf('%')>-1){
            $self.attr('style', 'width:'+size+';')
        }else{
            $self.addClass(size);
        }
    }

    function _changeItem(component) {
        try {
            var id = ly.el.value($(component));
            // selected
            this.bindTo(_select)(id, true);
        } catch (err) {
            ly.console.error(err);
        }
        return false;
    }

    function _itemById(id) {
        var result
            , field_id = this['_field_id']
            ;
        _.forEach(this['_items'], function (item) {
            if (item[field_id] === id) {
                result = item;
                return false;
            }
        });
        return result||this['_items'][0];
    }

    function _select(item_or_string, emitevent) {
        var self = this;
        _.delay(function () {
            var item = _.isString(item_or_string) ? self.bindTo(_itemById)(item_or_string) : item_or_string
                ;
            self['_selected'] = item;

            // selected: show selection thumb
            self.bindTo(_showThumb)();

            if (!!emitevent) {
                self.trigger(EVENT_CHANGE, item);
            }
        }, 100);
    }

    function _showThumb() {
        var self = this
            , item = self['_selected'];
        if(!item){
            return;
        }
        var field_id = this['_field_id']
            , field_image = this['_field_image']
            , field_description = this['_field_description']
            , image = item[field_image]
            , description = item[field_description]
            , $thumb = $(self.template(sel_thumb))
            , $thumb_img = $(self.template(sel_thumb_image))
            , $thumb_des = $(self.template(sel_thumb_description))
            ;

        ly.el.value(self.template(sel_self), item[field_id]);

        if(!!image){
            // change image, name and description
            $thumb_img.attr('src', image);
            $thumb_des.html(description);
             // show thumb
            $thumb.fadeIn();
        } else {
            // hide thumb
            $thumb.hide();
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.Imagecombo');
    ly.gui.widgets.Imagecombo = Imagecombo;

})();

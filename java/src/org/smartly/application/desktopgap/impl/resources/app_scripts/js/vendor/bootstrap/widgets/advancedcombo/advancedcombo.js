(function () {

    lyb.require('/assets/lib/bootstrap/widgets/advancedcombo/advancedcombo.mini.css');

    var EVENT_SELECT = 'select'
        , EVENT_EDIT = 'edit'
        , EVENT_REMOVE = 'remove'
        , EVENT_ADD = 'add'
        , EVENT_SEARCH = 'search'

        , TEMPLATE = '<option value="{_id}">{name}</option>'

        , sel_sef = '#<%= cid %>'

        , sel_title = '#title-<%= cid %>'
        , sel_found = '#found-<%= cid %>'
        , sel_total = '#total-<%= cid %>'
        , sel_add = '#add-<%= cid %>'
        , sel_edit = '#edit-<%= cid %>'
        , sel_remove = '#remove-<%= cid %>'
        , sel_search_box = '#search-box-<%= cid %>'
        , sel_search = '#search-<%= cid %>'
        , sel_search_text = '#search-text-<%= cid %>'
        , sel_confirm_remove = '#confirm-remove-<%= cid %>'
        , sel_confirm_undo = '#confirm-undo-<%= cid %>'

        , sel_buttons = '#buttons-<%= cid %>'
        , sel_confirm = '#confirm-<%= cid %>'

        , sel_combo = '#combo-<%= cid %>'
        , sel_icon = '#icon-<%= cid %>'
        , sel_thumb = '#thumb-<%= cid %>'
        , sel_thumb_image = '#thumb_image-<%= cid %>'
        , sel_thumb_description = '#thumb_description-<%= cid %>'

        ;

    function AdvancedCombo(attributes) {
        var self = this
            ;

        ly.base(this, {
            template: '/assets/lib/bootstrap/widgets/advancedcombo/advancedcombo.vhtml',
            model: false,
            view: true
        });

        attributes = attributes || {}; // avoid error on null attributes

        self['_selected'] = "";

        // add listeners
        self.on('init', function () {
            self.attributes().on('change', self.bindTo(_changedAttributes));
            self.bindTo(_initAttributesModel)(attributes);
        });
    }

    ly.inherits(AdvancedCombo, ly.Gui);

    AdvancedCombo.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    AdvancedCombo.prototype.select = function (item, emitEvent) {
        this.bindTo(_select)(item, emitEvent);
    };

    AdvancedCombo.prototype.selected = function () {
        return this['_selected'];
    };

    AdvancedCombo.prototype.items = function (items) {
        var self = this;
        if (!!items) {
            self.attributes().set({items: items});
        }
        return self.attributes().get('items');
    };

    AdvancedCombo.prototype.search = function () {
        return '';
    };


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _initAttributesModel(options) {
        var self = this;
        var attributes = self.attrs();

        attributes['title'] = options['title'] || attributes['title'] || '';
        attributes['add'] = null != options['add'] ? !!options['add'] : ly.toBoolean(attributes['add']);
        attributes['add-tooltip'] = options['add-tooltip'] || attributes['add-tooltip'] || '';
        attributes['remove'] = null != options['remove'] ? !!options['remove'] : ly.toBoolean(attributes['remove']);
        attributes['remove-tooltip'] = options['remove-tooltip'] || attributes['remove-tooltip'];
        attributes['edit'] = null != options['edit'] ? !!options['edit'] : ly.toBoolean(attributes['edit']);
        attributes['edit-tooltip'] = options['edit-tooltip'] || attributes['edit-tooltip'];
        attributes['search'] = null != options['search'] ? !!options['search'] : ly.toBoolean(attributes['search']);
        attributes['search-tooltip'] = options['search-tooltip'] || attributes['search-tooltip'] || '';
        attributes['search-placeholder'] = options['search-placeholder'] || attributes['search-placeholder'] || '';

        attributes['icon'] = options['icon'] || attributes['icon'] || 'icon-asterisk';
        attributes['size'] = options['size'] || attributes['size'] || 'input-medium';

        attributes['skip'] = 0;
        attributes['limit'] = 0;
        attributes['page_nr'] = 1;
        attributes['page_count'] = 1;
        attributes['total'] = null != options['total'] ? options['total'] : null != options['total'] ? options['total'] : 0;

        attributes['items'] = null != options['items'] ? options['items'] : options['items'] || [];

        //-- item attributes --//
        attributes['item-id'] = options['item-id'] || attributes['item-id'] || '_id';
        attributes['item-name'] = options['item-name'] || attributes['item-name'] || 'name';
        attributes['item-description'] = options['item-description'] || attributes['item-description'] || 'description';
        attributes['item-image'] = options['item-image'] || attributes['item-image'] || 'image';

        self.attributes().set(attributes);

    }

    function _initComponents(callback) {
        var self = this
            ;

        self.bindTo(_initParentAttributes)();

        self.bindTo(_initHandlers)();

        self.bindTo(_toggleConfirm)(false);

        ly.call(callback);
    }

    function _initParentAttributes() {
        var self = this;

        var attributes = {};

        //-- title --//
        var title = self.parent.attr('data-title');
        if (!!title) {
            attributes['title'] = title;
        }

        //-- add --//
        var add = self.parent.attr('data-add');
        attributes['add'] = null == add || ly.toBoolean(add);
        if (!!attributes['add']) {
            var add_tooltip = self.parent.attr('data-add-tooltip');
            if (!!add_tooltip) {
                attributes['add-tooltip'] = add_tooltip;
            }
        }

        //-- remove --//
        var remove = self.parent.attr('data-remove');
        attributes['remove'] = null == remove || ly.toBoolean(remove);
        if (attributes['remove']) {
            var remove_tooltip = self.parent.attr('data-remove-tooltip');
            if (!!remove_tooltip) {
                attributes['remove-tooltip'] = remove_tooltip;
            }
        }

        //-- edit --//
        var edit = self.parent.attr('data-edit');
        attributes['edit'] = null == edit || ly.toBoolean(edit);
        if (attributes['edit']) {
            var edit_tooltip = self.parent.attr('data-edit-tooltip');
            if (!!edit_tooltip) {
                attributes['edit-tooltip'] = edit_tooltip;
            }
        }

        //-- search --//
        var search = self.parent.attr('data-search');
        attributes['search'] = null == search || ly.toBoolean(search);
        if (attributes['search']) {
            var search_tooltip = self.parent.attr('data-search-tooltip');
            if (!!search_tooltip) {
                attributes['search-tooltip'] = search_tooltip;
            }
            var search_placeholder = self.parent.attr('data-search-placeholder');
            if (!!search_placeholder) {
                attributes['search-placeholder'] = search_placeholder;
            }
        }

        self.attributes(attributes);
    }

    function _changedAttributes(model) {
        var self = this;
        var changed = model['changed'];

        //-- icon --//
        if (_.has(changed, 'icon')) {
            var $icon = $(self.template(sel_icon));
            $icon.addClass(changed['icon']);
        }

        //-- size --//
        if (_.has(changed, 'size')) {
            var $combo = $(self.template(sel_combo));
            var size = changed['size'];
            if (size.indexOf('px') > -1 || size.indexOf('%') > -1) {
                $combo.attr('style', 'width:' + size + ';')
            } else {
                $combo.addClass(size);
            }
        }

        //-- title --//
        if (_.has(changed, 'title')) {
            var $title = $(self.template(sel_title));
            $title.html(changed['title']);
        }

        //-- add --//
        if (_.has(changed, 'add')) {
            var $add = $(self.template(sel_add));
            if (!!changed['add']) {
                $add.show();
                $add.attr('title', self.attributes().get('add-tooltip'));
            } else {
                $add.hide();
            }
        }

        //-- remove --//
        if (_.has(changed, 'remove')) {
            var $remove = $(self.template(sel_remove));
            if (!!changed['remove']) {
                $remove.show();
                $remove.attr('title', self.attributes().get('remove-tooltip'));
            } else {
                $remove.hide();
            }
        }


        //-- edit --//
        if (_.has(changed, 'edit')) {
            var $edit = $(self.template(sel_edit));
            if (!!changed['edit']) {
                $edit.show();
                $edit.attr('title', self.attributes().get('edit-tooltip'));
            } else {
                $edit.hide();
            }
        }


        //-- search --//
        if (_.has(changed, 'search')) {
            var $search_box = $(self.template(sel_search_box));
            if (!!changed['search']) {
                $search_box.show();
                var tooltip = self.attributes().get('search-tooltip');
                if (!!tooltip) {
                    $(self.template(sel_search)).attr('title', tooltip);
                }
                var placeholder = self.attributes().get('search-placeholder');
                if (!!placeholder) {
                    $(self.template(sel_search_text)).attr('placeholder', placeholder);
                }
            } else {
                $search_box.hide();
            }
        }

        //-- items --//
        if (_.has(changed, 'items')) {
            if (_.isArray(changed['items'])) {
                self.bindTo(_loadItems)(changed['items']);
            }
        }
    }

    function _loadItems(items) {
        var self = this;
        var $combo = $(self.template(sel_combo))

        $combo.html('');
        if (_.isArray(items) && items.length > 0) {
            var field_id = self.attributes().get('item-id');
            var field_label = self.attributes().get('item-name');
            // creates items
            var count = 0;
            _.forEach(items, function (item) {
                if (!ly.isNull(item)) {
                    count++;
                    var data = {_id: item[field_id], name: item[field_label]};
                    var $item = $(ly.template(TEMPLATE, data));
                    $item.appendTo($combo);
                }
            });

            // update total
            if (count < 2) {
                // hide Found:
                $(self.template(sel_found)).hide();
            } else {
                $(self.template(sel_total)).html(count);
            }

            // select first
            self['_selected'] = items[0];
            // async call for gui
            self.select(self['_selected']);
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
        var self = this;
        var result;
        var field_id = self.attributes().get('item-id');
        var items = self.attributes().get('items');
        _.forEach(items, function (item) {
            if (item[field_id] === id) {
                result = item;
                return false;
            }
            return true;
        });
        return result || items[0];
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
                self.trigger(EVENT_SELECT, item);
            }
        }, 100);
    }

    function _showThumb() {
        var self = this
            , item = self['_selected'];
        if (!item) {
            return;
        }
        var field_id = self.attributes().get('item-id')
            , field_image = self.attributes().get('item-image')
            , field_description = self.attributes().get('item-description')
            , image = item[field_image] || ''
            , description = item[field_description]
            , $thumb = $(self.template(sel_thumb))
            , $thumb_img = $(self.template(sel_thumb_image))
            , $thumb_des = $(self.template(sel_thumb_description))
            ;

        ly.el.value(self.template(sel_combo), item[field_id]);

        if (!!image) {
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

    function _initHandlers() {
        var self = this;

        //-- search --//
        var $search = $(self.template(sel_search));
        var $search_text = $(self.template(sel_search_text));
        $search.tooltip();
        ly.el.click($search, function () {
            self.bindTo(_clickSearch)($search_text);
        });

        //-- add --//
        var $add = $(self.template(sel_add));
        $add.tooltip();
        ly.el.click($add, function () {
            self.bindTo(_clickAdd)();
        });
        //-- remove --//
        var $remove = $(self.template(sel_remove));
        $remove.tooltip();
        ly.el.click($remove, function () {
            self.bindTo(_clickRemove)();
        });
        //-- edit --//
        var $edit = $(self.template(sel_edit));
        $edit.tooltip();
        ly.el.click($edit, function () {
            self.bindTo(_clickEdit)();
        });


        // combo
        var $combo = $(self.template(sel_combo));
        $combo.unbind();
        $combo.on('change', function (e) {
            e.stopImmediatePropagation();
            return self.bindTo(_changeItem)(this);
        });

        //-- confirm remove --//
        var $confirm_remove = $(self.template(sel_confirm_remove));
        ly.el.click($confirm_remove, function () {
            self.bindTo(_confirmRemove);
        });
        //-- confirm undo --//
        var $confirm_undo = $(self.template(sel_confirm_undo));
        ly.el.click($confirm_undo, function () {
            self.bindTo(_toggleConfirm)(false);
        });
    }

    function _toggleConfirm(show) {
        var self = this;
        var $confirm = $(self.template(sel_confirm));
        var $buttons = $(self.template(sel_buttons));

        if (show) {
            $confirm.show();
            $buttons.hide();
        } else {
            $confirm.hide();
            $buttons.show();
        }
    }

    function _clickSearch($search_text) {
        var self = this;
        var txt = ly.el.value($search_text);

        //-- reset paging--//
        self.attributes().set({
            skip: 0,
            page_nr: 1,
            page_count: 1
        });

        //-- trigger event --//
        self.trigger(EVENT_SEARCH, txt);
    }

    function _clickEdit() {
        var self = this;
        var item = self.selected();
        if (!!item) {
            self.trigger(EVENT_EDIT, item);
        }
    }

    function _clickAdd() {
        var self = this;
        self.trigger(EVENT_ADD, '');
    }

    function _clickRemove() {
        var self = this;
        var item = self.selected();
        if (!!item) {
            // show remove question
            self.bindTo(_toggleConfirm)(true);
        }
    }

    function _confirmRemove() {
        var self = this;
        var item = self.selected();
        if (!!item) {
            // show remove question
            self.trigger(EVENT_REMOVE, item);
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.AdvancedCombo');
    ly.gui.widgets.AdvancedCombo = AdvancedCombo;

})();

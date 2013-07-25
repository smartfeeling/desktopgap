(function () {

    lyb.require('/assets/lib/bootstrap/widgets/pagedlist/pagedlist.mini.css');
    lyb.require('/assets/lib/bootstrap/widgets/pagedlist/pagedlistitem.js');

    var EVENT_SELECT = 'select'
        , EVENT_EDIT = 'edit'
        , EVENT_REMOVE = 'remove'
        , EVENT_NEXT = 'next'
        , EVENT_PREV = 'prev'
        , EVENT_ADD = 'add'
        , EVENT_SEARCH = 'search'
        , IMG_SRC = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADAAAAAwCAYAAABXAvmHAAAC00lEQVR4Xu1aW2riUBjOGQbBok0E79fggy19qbqBZgnuoHEHzgpqVzB2B3YF4+wgXcHYh2LpgxjFS2lBoz7rfJYDDXLiJXGqZ/CDn1P633P+SyCS+Xwu8IxvAucg+Xz+D86swCfqJJfLcV1D3x32wDGBYwKz2ez/vAFCiCIcEBCntlUCT09PD8IB4eLigu8eQJzOEzg/PxdxqJTkRZk1Go1HLhI4Ozu7hGwNDJkylOfn50cubiCTyYi0iSTK0F5eXh64KSHIlE3BL1DZQw/Y3wOQUZcYI+GLgRgc3YDE7RRKp9MphkwWxEcPNJtNXZblZUYJdMdTE9dwFEwMOZVK3ei6fsvTFCosMcrJZFJqt9s/9pkASSQSTE6n0yFmQchd46gyRFug2oKg88/6gsZpb4zShO7j8XiL3oZiLidQlhBS28cYJdFolJlZr9cjVsZisVjK9ErR6na7urAh4O+aLkKJnmX4MtbrseMkkUiEyej3+0TYMeDrCoe2xKiDFPgz1ujO9/o6HQ6HRTrNWDtFA18ZDAaGnSn0VWOQlg1gkUQoFFJeX1+Ng0sgGAxewY+6RixLe6J4YAnQPbIZ1EAgUH97e7vbeA/4/X4m5/39fSdNDPu0cbdCFv4fl+zY3APO53fFhloVlNtoD/h8PmZmw+HQ8Q3ANn36tlBGDLcmW87GqCRJP3GUTLO7MhqN7h3XvjVK8FmFD31lD4iiyOQYhkEECsjc4GAFo4FUyOrLDOjQ2neEKmwXqT32Jj49PWUyxuMxEQDwL+kTt8IIpEL+t5kBvV/0DdYpZNjWYc9OCdEFtBoSqOb1esuTyeSjZvF3CnoFYTcog4qWJeTxeJic6XS64G1bBlXoFaFH+2VnkEGtrccoeNs2oXpycrLQKwi7haU94na75ytWe104DGggZdseKAmHA8XOF5qCwAGIy+U6fqU8JnD8SskxCIjv30rwXkJ/AXUFovgG6GR5AAAAAElFTkSuQmCC'
        , sel_title = '#title-<%= cid %>'
        , sel_total = '#total-<%= cid %>'
        , sel_items = '#items-<%= cid %>'
        , sel_loading = '#loading-<%= cid %>'
        , sel_empty = '#empty-<%= cid %>'
        , sel_add = '#add-<%= cid %>'
        , sel_search_box = '#search-box-<%= cid %>'
        , sel_search = '#search-<%= cid %>'
        , sel_search_text = '#search-text-<%= cid %>'
        , sel_next = '#next-<%= cid %>'
        , sel_prev = '#prev-<%= cid %>'
        , sel_count = '#count-<%= cid %>'
        , sel_pager = '#pager-<%= cid %>'
        , sel_modal = '#modal-<%= cid %>'
        , sel_modal_edit = '#modal-edit-<%= cid %>'
        , sel_modal_remove = '#modal-remove-<%= cid %>'
        , sel_modal_undo = '#modal-undo-<%= cid %>'
        , sel_modal_confirm_remove = '#modal-confirm-remove-<%= cid %>'
        , sel_item_name = '#item-name-<%= cid %>'
        , sel_item_description = '#item-description-<%= cid %>'
        , sel_item_image = '#item-image-<%= cid %>'
        , sel_actions = '#modal-actions-<%= cid %>'
        , sel_confirm = '#modal-confirm-<%= cid %>'
        ;

    function PagedList(options) {
        var self = this;
        ly.base(this, {
            template: '/assets/lib/bootstrap/widgets/pagedlist/pagedlist.vhtml',
            model: false,
            view: false
        });

        self.set(options);

        self.bindTo(_refreshOptions)();

        // add Events
        self.on('init', _init);
    }

    ly.inherits(PagedList, ly.Gui);

    PagedList.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PagedList.prototype.set = function (options) {
        var self = this;
        if (!!options) {
            self['_title'] = options['title'] || '';
            self['_add'] = null != options['add'] ? !!options['add'] : true;
            self['_add_tooltip'] = options['_add_tooltip'] || '';
            self['_search'] = null != options['search'] ? !!options['search'] : true;
            self['_search_tooltip'] = options['_search_tooltip'] || '';
            self['_search_placeholder'] = options['_search_placeholder'] || '';

            self['_skip'] = null != options['skip'] ? options['skip'] : null != self['_skip'] ? self['_skip'] : 0;
            self['_limit'] = null != options['limit'] ? options['limit'] : null != self['_limit'] ? self['_limit'] : 5;
            self['_page_nr'] = null != options['page_nr'] ? options['page_nr'] : null != self['_page_nr'] ? self['_page_nr'] : 1;
            self['_page_count'] = null != options['page_count'] ? options['page_count'] : null != self['_page_count'] ? self['_page_count'] : 1;
            self['_total'] = null != options['total'] ? options['total'] : null != self['_total'] ? self['_total'] : 0;

            self['_items'] = null != options['items'] ? options['items'] : self['_items'] || [];

            self['_modal'] = null != options['modal'] ? !!options['modal'] : true;
            self['_modal_edit'] = null != options['modal_edit'] ? !!options['modal_edit'] : true;
            self['_modal_remove'] = null != options['modal_remove'] ? !!options['modal_remove'] : true;

            //-- item attributes --//
            if (null == self['_item_name']) {
                self['_item_name'] = null != options['item_name'] ? options['item_name'] : 'name';
            }
            if (null == self['_item_description']) {
                self['_item_description'] = null != options['item_description'] ? options['item_description'] : 'description';
            }
            if (null == self['_item_image']) {
                self['_item_image'] = null != options['item_image'] ? options['item_image'] : 'image';
            }

            if (_.isArray(options['items'])) {
                self.bindTo(_loadItems)(options['items']);
            }

            self.bindTo(_enablePrevNext)();
        }
    };

    PagedList.prototype.skip = function () {
        var self = this;
        return self['_skip'];
    };

    PagedList.prototype.limit = function () {
        var self = this;
        return self['_limit'];
    };

    PagedList.prototype.search = function () {
        var self = this
            , $search_text = $(self.template(sel_search_text))
            ;
        return ly.el.value($search_text) || '';
    };

    /**
     * Reset skip and page_nr parameters.
     * No effect on query
     */
    PagedList.prototype.resetPosition = function (reload) {
        var self = this;

        //-- reset paging--//
        self['_skip'] = 0;
        self['_page_nr'] = 1;
        self['_page_count'] = 1;
        if(!!reload){
           // new search event
            self.bindTo(_clickSearch)($(self.template(sel_search_text)));
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
            ;

        self.bindTo(_initParentOptions)();

        self.bindTo(_refreshOptions)();

        self.bindTo(_initHandlers)();

        self.bindTo(_enablePrevNext)();

        ly.call(callback);
    }

    function _initParentOptions() {
        var self = this;

        //-- title --//
        var title = self.parent.attr('data-title');
        if (!!title && !self['_title']) {
            self['_title'] = title;
        }

        //-- add --//
        var add = self.parent.attr('data-add');
        if (null == add || ly.toBoolean(add)) {
            self['_add'] = true;
            var add_tooltip = self.parent.attr('data-add-tooltip');
            if (!!add_tooltip && !self['_add_tooltip']) {
                self['_add_tooltip'] = add_tooltip;
            }
        }

        //-- search --//
        var search = self.parent.attr('data-search');
        if (null == search || ly.toBoolean(search)) {
            self['_search'] = true;
            var search_tooltip = self.parent.attr('data-search-tooltip');
            if (!!search_tooltip && !self['_search_tooltip']) {
                self['_search_tooltip'] = search_tooltip;
            }
            var search_placeholder = self.parent.attr('data-search-placeholder');
            if (!!search_placeholder && !self['_search_placeholder']) {
                self['_search_placeholder'] = search_placeholder;
            }
        }
    }

    function _refreshOptions() {
        var self = this;

        //-- title --//
        var $title = $(self.template(sel_title));
        $title.html(self['_title']);

        //-- add --//
        var $add = $(self.template(sel_add));
        if (!!self['_add']) {
            $add.show();
            $add.attr('title', self['_add_tooltip']);
        } else {
            $add.hide();
        }

        //-- add --//
        var $search_box = $(self.template(sel_search_box));
        if (!!self['_search']) {
            $search_box.show();
            if (!!self['_search_tooltip']) {
                $(self.template(sel_search)).attr('title', self['_search_tooltip']);
            }
            if (!!self['_search_placeholder']) {
                $(self.template(sel_search_text)).attr('placeholder', self['_search_placeholder']);
            }
        } else {
            $search_box.hide();
        }
    }

    function _loadItems(items) {
        var self = this;

        // wait PageListItem is loaded
        if (!_.isFunction(ly.gui.widgets.PagedListItem)) {
            _.delay(function () {
                self.bindTo(_loadItems)(items);
            }, 200);
            return;
        }

        // loading....
        self.bindTo(_toggleLoading)(true);

        // load with little delay
        _.delay(function () {

            var $items = $(self.template(sel_items))
                ;

            // clear body
            $items.html('');

            var count = 0;
            _.forEach(items, function (item) {
                if (!ly.isNull(item)) {
                    count++;
                    var comp = new ly.gui.widgets.PagedListItem({
                        name: self['_item_name'],
                        description: self['_item_description'],
                        image: self['_item_image'],
                        data: item
                    });
                    comp.appendTo($items);
                    comp.on('click', function () {
                        self.bindTo(_clickItem)(item);
                    });
                }
            });

            self.bindTo(_toggleLoading)(false, count === 0);

        }, 200);
    }

    function _clickItem(item) {
        var self = this;
        if (!!item) {
            self.trigger(EVENT_SELECT, item);
            self.bindTo(_showModal)(item);
        }
    }

    function _initHandlers() {
        var self = this
            , $search = $(self.template(sel_search))
            , $search_text = $(self.template(sel_search_text))
            , $add = $(self.template(sel_add))
            , $prev = $(self.template(sel_prev))
            , $next = $(self.template(sel_next))

            ;

        //-- search --//
        $search.tooltip();
        ly.el.click($search, function () {
            self.bindTo(_clickSearch)($search_text);
        });

        //-- add --//
        $add.tooltip();
        ly.el.click($add, function () {
            self.trigger(EVENT_ADD, '');
        });

        //-- prev --//
        $prev.tooltip();
        ly.el.click($prev, function () {
            if (self['_page_nr'] > 1) {
                self['_page_nr']--;
                self['_skip'] -= self['_limit'];
                self.bindTo(_enablePrevNext)();
                self.bindTo(_toggleLoading)(false);
                self.trigger(EVENT_PREV, {skip: self['_skip'], limit: self['_limit']});
            }
            $search_text.focus();
        });
        //-- next --//
        $next.tooltip();
        ly.el.click($next, function () {
            if (self['_page_nr'] < self['_page_count']) {
                self['_page_nr']++;
                self['_skip'] += self['_limit'];
                self.bindTo(_enablePrevNext)();
                self.bindTo(_toggleLoading)(false);
                self.trigger(EVENT_NEXT, {skip: self['_skip'], limit: self['_limit']});
            }
            $search_text.focus();
        });


    }

    function _clickSearch($search_text) {
        var self = this;
        var txt = ly.el.value($search_text);

        //-- reset paging--//
        self['_skip'] = 0;
        self['_page_nr'] = 1;
        self['_page_count'] = 1;

        //-- trigger event --//
        self.trigger(EVENT_SEARCH, txt);
    }

    function _enablePrevNext() {
        var self = this
            , page_number = self['_page_nr']
            , page_count = self['_page_count']
            , $prev = $(self.template(sel_prev))
            , $next = $(self.template(sel_next))
            , $label = $(self.template(sel_count))
            , $pager = $(self.template(sel_pager))
            , $total = $(self.template(sel_total))
            ;

        $total.html(self['_total']);

        if (page_count < 2) {
            $pager.hide();
            $prev.hide();
            $next.hide();
            $label.hide();
        } else {
            $pager.show();
            $prev.show();
            $next.show();
            $label.show();

            // label
            $label.html(page_number + '/' + page_count);
        }
    }

    function _toggleLoading(loading, empty) {
        var self = this;
        var $items = $(self.template(sel_items))
            , $loading = $(self.template(sel_loading))
            , $empty = $(self.template(sel_empty))
            ;
        if (loading) {
            $items.hide();
            $empty.hide();
            $loading.show();
        } else {
            $loading.hide();
            if (empty) {
                $items.hide();
                $empty.fadeIn();
            } else {
                $items.fadeIn();
                $empty.hide();
            }
        }
    }

    function _showModal(item) {
        var self = this;
        var $modal = $(self.template(sel_modal))
            , $actions = $(self.template(sel_actions))
            , $confirm = $(self.template(sel_confirm))
            , $name = $(self.template(sel_item_name))
            , $description = $(self.template(sel_item_description))
            , $image = $(self.template(sel_item_image))

            , $edit = $(self.template(sel_modal_edit))
            , $remove = $(self.template(sel_modal_remove))
            ;

        //-- show buttons --//
        $actions.show();
        $confirm.hide();

        //-- MODAL --//
        $edit.unbind();
        $remove.unbind();
        if (!!self['_modal_edit']) {
            $edit.show();
            ly.el.click($edit, function () {
                $modal.modal('hide');
                self.trigger(EVENT_EDIT, item);
            });
        } else {
            $edit.hide();
        }

        if (!!self['_modal_remove']) {
            $remove.show();
            ly.el.click($remove, function () {
                self.bindTo(_confirmRemove)(item);
            });
        } else {
            $remove.hide();
        }

        $name.html(ly.value(item, self['_item_name']));
        $description.html(ly.value(item, self['_item_description']));
        $image.attr('src', ly.value(item, self['_item_image'] || IMG_SRC));
        //$name.html(item[self['_item_name']]);
        //$description.html(item[self['_item_description']]);
        //$image.attr('src', item[self['_item_image']] || IMG_SRC);

        //-- show modal --//
        $modal.modal({
            backdrop: true,
            keyboard: true,
            show: true
        });
    }

    function _confirmRemove(item) {
        var self = this;
        var $actions = $(self.template(sel_actions))
            , $confirm = $(self.template(sel_confirm))
            , $undo = $(self.template(sel_modal_undo))
            , $remove = $(self.template(sel_modal_confirm_remove))
            ;

        $actions.hide();
        $confirm.fadeIn();

        ly.el.click($undo, function () {
            $confirm.hide();
            $actions.fadeIn();
        });

        ly.el.click($remove, function () {
            self.trigger(EVENT_REMOVE, item);
            $confirm.fadeOut();
            // close modal with little delay
            _.delay(function () {
                $(self.template(sel_modal)).modal('hide');
            }, 500);
        });
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.PagedList');
    ly.gui.widgets.PagedList = PagedList;

})();

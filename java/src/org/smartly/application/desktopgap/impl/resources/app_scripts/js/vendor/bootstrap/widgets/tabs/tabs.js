(function () {

    var TPL_NAV = '<li><a href="#{id}" data-toggle="pill">{name} &nbsp;<button id="btn-remove-{id}" class="close"><span class="icon-remove"></span></button></a></li>';
    var TPL_NAV_ADD_BUTTON = '<li><div style="margin-top: 5px;width: 100%;height: 100%"><button id="add-language-<%= cid %>" title="" class="btn btn-mini btn-primary"><span class="icon-plus icon-white"></span></button></div></li>';
    var TPL_TAB = '<div class="tab-pane" id="{id}" style="position: relative;">' +
        '<div id="{id}-confirm-remove" class="hide alert alert-block" style="position:fixed;">{removeconfirm} &nbsp;<div class="pull-right"><button id="{id}-remove-ok" class="btn btn-mini btn-danger">{removeok}</button>&nbsp;<button id="{id}-remove-cancel" class="btn btn-mini btn-primary">{removecancel}</button></div></div>' +
        '<div id="{id}-header"></div>' +
        '<div id="{id}-body" style="margin-top: 3px;"></div>' +
        '</div>';


    var EVENT_ADD = 'add'
        , EVENT_REMOVE = 'remove'
        , EVENT_MODEL_PREFIX = 'model:'

        , sel_self = '#<%= cid %>'

        , sel_nav = '#nav-<%= cid %>'
        , sel_tab = '#tab-<%= cid %>'

        ;

    function Tabs(options) {
        var self = this;
        ly.base(this, {
            template: '/assets/lib/bootstrap/widgets/tabs/tabs.vhtml',

            view: true
        });

        options = options || {};

        // fields
        self['_components'] = [];

        // add Events
        self.on('init', function () {
            self.attributes().on('change', self.bindTo(_changedAttributes));
            self.bindTo(_initOptions)(options);
        });
    }

    ly.inherits(Tabs, ly.Gui);

    Tabs.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    /**
     * Returns or set Tabs
     * @param tabs
     * @returns {Array} i.e. [{"_id":"it", "name":"Italian", "gui-body": GUI_OBJECT}]
     */
    Tabs.prototype.tabs = function (tabs) {
        var self = this;
        if (_.isArray(tabs)) {
            self.attributes().set({'tabs': tabs});
        }
        return self.attributes().get('tabs');
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _initOptions(options) {
        var self = this;
        var attributes = self.attrs();

        attributes['add'] = null != options['add'] ? !!options['add'] : ly.toBoolean(attributes['add']);
        attributes['add-tooltip'] = options['add-tooltip'] || attributes['add-tooltip'] || '';

        attributes['remove'] = null != options['remove'] ? !!options['remove'] : ly.toBoolean(attributes['remove']);
        attributes['remove-confirm'] = options['remove-confirm'] || attributes['remove-confirm'] || 'Confirm remove "{name}"?';
        attributes['remove-ok'] = options['remove-ok'] || attributes['remove-ok'] || 'OK';
        attributes['remove-cancel'] = options['remove-cancel'] || attributes['remove-cancel'] || 'CANCEL';

        attributes['tabs'] = !!options['tabs'] ? options['tabs'] : [];

        self.attributes(attributes);
    }

    /**
     * GUI/View initialization
     * @private
     */
    function _initComponents(callback) {
        var self = this;

        ly.call(callback, self, []);
    }

    function _changedAttributes(model) {
        var self = this;
        var changed = model['changed'];

        if (_.has(changed, 'tabs')) {
            self.bindTo(_initTabs)();
        }
    }

    function _initTabs() {
        var self = this;
        var tabs = self.attributes().get('tabs');

        if (!_.isArray(tabs) || tabs.length == 0) {
            return;
        }

        var $nav = $(self.template(sel_nav));
        var $tab = $(self.template(sel_tab));


        // remove existing
        _.forEach(self['_components'], function (comp) {
            comp.detach(true);
        });
        $nav.html('');
        $tab.html('');

        // loop on tabs
        _.forEach(tabs, function (tab, index) {
            var id = tab['_id'];
            var name = tab['name'];

            var $html_nav = $(ly.template(TPL_NAV, {id: id, name: name}));
            var $html_tab = $(ly.template(TPL_TAB, {
                id: id, name: name,
                removeconfirm: ly.template(self.attributes().get('remove-confirm'), {name: name}),
                removeok: self.attributes().get('remove-ok'),
                removecancel: self.attributes().get('remove-cancel')
            }));

            //-- NAV --//
            var nav_elements = self.bindTo(_initTabNav)(tab, index, $html_nav, $nav);

            //-- CONTENT --//
            var tab_elements = self.bindTo(_initTabContent)(tab, index, $html_tab, $tab);

            //-- listeners --//
            ly.el.click(nav_elements.btn_remove, function () {
                //tab_elements.body.hide();
                //tab_elements.confirm.show();
                tab_elements.confirm.slideDown();
            });

            ly.el.click(tab_elements.btn_cancel, function () {
                //tab_elements.body.show();
                tab_elements.confirm.slideUp();
            });
            ly.el.click(tab_elements.btn_ok, function () {
                //tab_elements.body.show();
                tab_elements.confirm.slideUp();
                self.bindTo(_clickRemove)(tab);
            });
        });

        //-- add button --//
        var allow_add = ly.toBoolean(self.attributes().get('add'));
        if (allow_add) {
            var $html_add = $(self.template(TPL_NAV_ADD_BUTTON));
            $nav.append($html_add);
            var $btn_add = $html_add.find('button');
            ly.el.click($btn_add, self.bindTo(_clickAdd));
        }
    }

    function _initTabNav(tab, index, $html_nav, $box) {
        var self = this;
        var allow_remove = null == tab['remove'] ? self.attributes().get('remove') : ly.toBoolean(tab['remove']);

        var $nav_link = $html_nav.find('a');
        $nav_link.on('shown', function (e) {
            self.bindTo(_tabShown)(tab, e.target, e.relatedTarget);
        });

        // remove button
        var $btn_remove = $html_nav.find('#btn-remove-' + tab['_id']);
        if (index === 0) {
            $html_nav.addClass('active');
            if (!allow_remove) {
                $btn_remove.hide();
            }
        } else {
            $btn_remove.hide();
        }

        $box.append($html_nav);

        return {btn_remove: $btn_remove};
    }

    function _initTabContent(tab, index, $html_tab, $box) {
        var self = this;
        var header = tab['gui-header'];
        var body = tab['gui-body'];

        //-- remove confirm --//
        var $box_confirm = $html_tab.find('#' + tab['_id'] + '-confirm-remove');
        var $btn_ok = $html_tab.find('#' + tab['_id'] + '-remove-ok');
        var $btn_cancel = $html_tab.find('#' + tab['_id'] + '-remove-cancel');

        //-- body --//
        var $box_body = $html_tab.find('#' + tab['_id'] + '-body');
        if (!!body && !!body.appendTo) {
            body.appendTo($box_body);
            body.on('all', function (eventName) {
                // dispatch all body events
                self.trigger(EVENT_MODEL_PREFIX + eventName, body);
            });
        }
        var $box_header = $html_tab.find('#' + tab['_id'] + '-header');

        if (index === 0) {
            $html_tab.addClass('active');
        }


        $box.append($html_tab);

        return {
            confirm: $box_confirm,
            btn_ok: $btn_ok,
            btn_cancel: $btn_cancel,
            body: $box_body
        };
    }

    // ------------------------------------------------------------------------
    //                      e v e n t s
    // ------------------------------------------------------------------------
    function _tabShown(tab, target, relatedTarget) {
        var self = this;
        var $btn_target = $(target).find('button');
        var $btn_related = $(relatedTarget).find('button');
        var allow_remove = null == tab['remove'] ? self.attributes().get('remove') : ly.toBoolean(tab['remove']);

        $btn_related.hide();
        // show current close button
        if (allow_remove) {
            $btn_target.show();
        } else {
            $btn_target.hide();
        }
    }

    function _clickRemove(tab) {
        var self = this;
        self.trigger(EVENT_REMOVE, tab);
        //console.log('remove:' + JSON.stringify(tab));
    }

    function _clickAdd() {
        var self = this;
        self.trigger(EVENT_ADD);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.Tabs');
    ly.gui.widgets.Tabs = Tabs;

})();

/**
 * List component.
 *
 * options:
 *      - items: Object {_id:'', description:'', image:''}
 */
(function (window) {

    var desktopgap = window.desktopgap
        , i18n = window['i18n']
        , favorites = window['favorites']

        , EVENT_CLICK = 'click'
        , EVENT_FAV = 'favorite'
        , EVENT_ACTION = 'action'

        , sel_title = '#title-<%= cid %>'
        , sel_list = '#list-<%= cid %>'
        , sel_tpl = '#tpl-<%= cid %>'

        , sel_ico = '#ico-<%= cid %>-'
        , sel_txt = '#txt-<%= cid %>-'
        , sel_fav = '#fav-<%= cid %>-'
        ;


    function List(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/list/list.html'),
            model: false,
            view: false
        });

        this['_fav'] = !!options ? !!options['fav'] : false;
        this['_items'] = !!options ? options['items'] || [] : [];
        this['_title'] = !!options ? options['title'] || '' : '';
        this['_actions'] = !!options ? options['actions'] || [] : []; // array of action objects: {_id:'act1', icon:'details.png'}

        this['_initialized'] = false;

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(List, ly.Gui);

    List.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    List.prototype.addItem = function (item) {
        if (!!item) {
            var self = this;
            self['_items'].push(item);
            if (self['_initialized']) {
                var $ul = $(self.template(sel_list))
                    , tpl = $(self.template(sel_tpl)).html()
                    ;
                self.bindTo(_loadItem)($ul, tpl, item);
            }
        }
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
        // selectors
        self['txt'] = self.template(sel_txt);
        self['ico'] = self.template(sel_ico);
        self['fav'] = self.template(sel_fav);
    }

    function _initComponents(callback) {
        try {
            this.bindTo(_initTitle)();
            this.bindTo(_initItems)();
        } catch (err) {
            console.error('(list.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    function _initTitle() {
        try {
            var self = this
                , title = self['_title']
                , $title = $(self.template(sel_title))
                ;
            $title.html(title);
            // console.log('TITLE: ' + title);
        } catch (err) {
            console.error('(list.js) _initTitle(): ' + err);
        }
    }

    function _initItems() {
        try {
            var self = this
                , items = self['_items']
                , tpl = $(self.template(sel_tpl)).html()
                , $ul = $(self.template(sel_list))
                ;

            self['_initialized'] = true;

            //-- loop on items --//
            _.forEach(items, function (item) {
                if (!!item) {
                    self.bindTo(_loadItem)($ul, tpl, item);
                }
            });
        } catch (err) {
            console.error('(list.js) _initItems(): ' + err);
        }
    }

    function _loadItem($ul, tpl, item) {
        try {
            var self = this;

            // check if item has a localizable description
            if (!!item['description']) {
                if (_.isObject(item['description'])) {
                    item['description'] = i18n.get(i18n.lang(), item, 'description');
                }
            } else {
                item['description'] = '';
            }

            var $item = $(ly.template(tpl, item));

            // append to list
            $ul.append($item);

            var $txt = $(self['txt'] + item['_id']);
            var $fav = $(self['fav'] + item['_id']);

            if (!self['_fav']) {
                $fav.hide();
            } else {
                self.bindTo(_setFav)($fav, item['is_favorite']);
            }

            // handle txt
            ly.el.click($txt, function () {
                self.trigger(EVENT_CLICK, item);
                //console.log(EVENT_CLICK);
            });
            // handle fav
            ly.el.click($fav, function () {
                self.trigger(EVENT_FAV, item);
                self.bindTo(_toggleFav)($fav);
            });

        } catch (err) {
            console.error('(list.js) _loadItem(): ' + err);
        }
    }

    function _toggleFav($fav){
        if ($fav.hasClass('fav-on')) {
            $fav.removeClass('fav-on');
            $fav.addClass('fav-off');
        } else {
            $fav.removeClass('fav-off');
            $fav.addClass('fav-on');
        }
    }

    function _setFav($fav, is_favorite){
        if (!!is_favorite) {
            $fav.removeClass('fav-on');
            $fav.addClass('fav-off');
        } else {
            $fav.removeClass('fav-off');
            $fav.addClass('fav-on');
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.list.List');
    desktopgap.gui.list.List = List;

    return desktopgap.gui.list.List;

})(this);
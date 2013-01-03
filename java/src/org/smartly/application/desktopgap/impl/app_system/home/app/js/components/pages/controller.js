(function (window) {

    var tpl_page_id = '<%= mnu_id+"_"+cid %>'
        ;

    function PagesController(options) {
        var self = this
            ;

        ly.base(self, {
            template: '',
            model: false,
            view: false
        });

        this['_classes'] = !!options ? options['pages'] || {} : {};
        this['_instances'] = {};
        this['_current_id'] = null;

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PagesController, ly.Gui);

    PagesController.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };


    PagesController.prototype.open = function (pageId) {
        var self = this
            , instances = self['_instances']
            , div_id = !!instances[pageId] ? instances[pageId]['div_id'] : null
            , old_div_id = self['_current_id']
            ;
        if (div_id === old_div_id) {
            return;
        }
        self['_current_id'] = div_id;
        // hide current page
        if (!!old_div_id) {
            self.bindTo(hidePage)(old_div_id, function () {
                // show new page
                if (!!div_id) {
                    self.bindTo(showPage)(div_id);
                }
            });
        } else {
            // show new page
            if (!!div_id) {
                self.bindTo(showPage)(div_id);
            }
        }
    };

    PagesController.prototype.close = function (pageId) {
        var self = this
            , instances = self['_instances']
            ;
        if (!!instances[pageId]) {
            self.bindTo(hidePage)(instances[pageId]['div_id']);
        }
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
    }

    function _initComponents() {
        var self = this
            ;
        // create page instances
        _.forEach(self['_classes'], function (func, id) {
            if (!self['_instances'][id]) {
                // create instance
                self['_instances'][id] = new func();
                // assign id
                self['_instances'][id]['mnu_id'] = id;
                self['_instances'][id]['div_id'] = self['_instances'][id].template(tpl_page_id);
                var $container = $('<div id="' + self['_instances'][id]['div_id'] + '"></div>');
                $container.hide();
                self['parent'].append($container);
                self['_instances'][id].appendTo($container);

            }
        });
    }

    function showPage(div_id, callback) {
        var self = this;

        $('#' + div_id).fadeIn(function () {
            ly.call(callback)
        });
    }

    function hidePage(div_id, callback) {
        var self = this;

        $('#' + div_id).fadeOut(function () {
            ly.call(callback)
        });
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PagesController');
    desktopgap.gui.pages.PagesController = PagesController;

    return desktopgap.gui.pages.PagesController;

})(this);
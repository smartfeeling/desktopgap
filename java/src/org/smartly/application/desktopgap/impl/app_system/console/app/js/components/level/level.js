(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- items --//
        ;

    function Level(options) {
        var self = this;

        ly.base(this, {
            template: load('./js/components/level/level.html'),
            model:false,
            view:false
        });

        this['_items'] =  [];

            // add listeners
        this.on('init', _init);
    }

    ly.inherits(Level, ly.Gui);

    Level.prototype.items = function (items) {
        if (!!items) {
            this['_items'] = items;
            this.bindTo(_refresh)();
        }
        return this['_items'];
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
            ;

    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.level.Level');
    desktopgap.gui.level.Level = Level;

    return desktopgap.gui.level.Level;

})(this);
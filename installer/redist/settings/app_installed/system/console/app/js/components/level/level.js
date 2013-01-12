(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- items --//
        , sel_count = '#count-<%= cid %>'
        , sel_messages = '#messages-<%= cid %>'
        ;

    function Level(options) {
        var self = this;

        ly.base(this, {
            template: load('./js/components/level/level.html'),
            model: false,
            view: false
        });

        this['_items'] = [];

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Level, ly.Gui);

    Level.prototype.items = function (items) {

        if (!!items) {
            this['_items'].push(items);
            this['_items'] = _.flatten(this['_items']);
            // console.log(JSON.stringify(this['_items']));
            this.bindTo(_load)(items);
        }
        return this['_items'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;
        self.bindTo(_load)(self['_items']);
    }

    function _load(items) {
        var self = this
            , $count = $(self.template(sel_count))
            ;

        //-- update count --//
        $count.html(self['_items'].length);

        //-- add messages --//
        _.forEach(items, function (item) {
            self.bindTo(_addMessage)(item);
        });
    }

    function _addMessage(message) {
        var self = this
            , $messages = $(self.template(sel_messages))
            ;

        var comp = new desktopgap.gui.message.Message({item:message});
        comp.appendTo($messages);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.level.Level');
    desktopgap.gui.level.Level = Level;

    return desktopgap.gui.level.Level;

})(this);
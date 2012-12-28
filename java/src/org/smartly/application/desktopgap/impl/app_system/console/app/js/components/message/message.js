(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- items --//
        ;

    function Message(options) {
        var self = this;

        ly.base(this, {
            template: load('./js/components/message/message.html'),
            model:false,
            view:false
        });

        this['_items'] = options['items'];

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Message, ly.Gui);

    Message.prototype.items = function (items) {
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

    ly.provide('desktopgap.gui.message.Message');
    desktopgap.gui.message.Message = Message;

    return desktopgap.gui.message.Message;

})(this);
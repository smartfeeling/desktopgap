(function (window) {

    var EVENT_CLICK = 'click'
        , sel_self = '#<%= cid %>'

    //-- items --//
        , sel_img = '#img-<%= cid %>'
        , sel_date = '#date-<%= cid %>'
        , sel_time = '#time-<%= cid %>'
        , sel_message = '#message-<%= cid %>'
        ;

    function Message(options) {
        var self = this;

        ly.base(this, {
            template: load('./js/components/message/message.html'),
            model: false,
            view: false
        });

        this['_item'] = options['item'];

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Message, ly.Gui);

    Message.prototype.item = function (item) {
        if (!!item) {
            this['_item'] = item;
            this.bindTo(_refresh)();
        }
        return this['_item'];
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
            , item = self['_item']
            ;
        if (!!item && !!item['level']) {
            var level = item['level']
                , message = item['message']
                , date = item['date']
                , time = item['time']
                , $img = $(self.template(sel_img))
                , $date = $(self.template(sel_date))
                , $time = $(self.template(sel_time))
                , $message = $(self.template(sel_message))
                ;
            //-- img --//
            $img.attr('src', './images/'+level+'.png');
            //-- date --//
            $date.html(date);
            //-- time --//
            $time.html(time);
            //-- message --//
            $message.html(message);
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.message.Message');
    desktopgap.gui.message.Message = Message;

    return desktopgap.gui.message.Message;

})(this);
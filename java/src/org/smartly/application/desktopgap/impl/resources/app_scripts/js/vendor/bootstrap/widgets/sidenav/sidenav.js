(function () {

    lyb.require('/assets/lib/bootstrap/widgets/sidenav/sidenav.mini.css');

    var EVENT_CLICK = 'click'

        , sel_self = '#<%= cid %>'
        , sel_navbuttons = '#<%= cid %> a'
        , sel_template = '#li-<%= cid %>'

        ;

    function Sidenav(options) {
        var self = this
            ;

        ly.base(this, {
            template:'/assets/lib/bootstrap/widgets/sidenav/sidenav.vhtml',
            model:false,
            view:true
        });

        this['_navs'] = options['navs'];
        this['_selected_idx'] = 0;

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(Sidenav, ly.Gui);

    Sidenav.prototype.selected = function(idx){
        if(null!=idx){
           var $el = this.bindTo(_lookupEl)(idx);
           if(!!$el && !!$el[0]){
               // event for first item selection
               this.bindTo(_clickNav)($el);
           }
        }
        return this['_selected_idx'];
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , $self = $(self.template(sel_self))
            , template = $(self.template(sel_template)).text()
            ;


        // creates navigation items
        var count = 0;
        _.forEach(self['_navs'], function (nav) {
            nav['idx']=count;
            var $item = $(ly.template(template, nav));
            $item.appendTo($self);
            if(count===0){
                $item.addClass('active'); // activate first
            }
            count++;
        });

        // event for first item selection
        this.trigger(EVENT_CLICK, self['_navs'][0]);

        // handler
        $(self.template(sel_navbuttons)).on('click', function (e) {
            e.stopImmediatePropagation();
            return self.bindTo(_clickNav)(this);
        });
    }

    function _clickNav(component) {

        try {
            var id = $(component).attr('data-set')
                , idx = parseInt($(component).attr('data-idx'))
                , item = this.bindTo(_itemById)(id, '_id')
                ;

            // selected
            $(this.template(sel_navbuttons)).parent().removeClass('active');
            $(component).parent().addClass('active');
            this['_selected_idx'] = idx;

            this.trigger(EVENT_CLICK, item);
        } catch (err) {
            ly.console.error(err);
        }
        return false;
    }

    function _lookupEl(idx){
        var self = this
            , $self = $(self.template(sel_self))
        ;
        return $self.find('li a[data-idx="'+idx+'"]');
    }

    function _itemById(id, field){
        var result;
        field = field||'_id';
        _.forEach(this['_navs'], function(item){
            if(item[field]===id){
                result = item;
                return false;
            }
        });
        return result;
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.Sidenav');
    ly.gui.widgets.Sidenav = Sidenav;

})();

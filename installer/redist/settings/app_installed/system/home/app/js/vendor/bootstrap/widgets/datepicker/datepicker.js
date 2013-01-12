(function () {

    var sel_button = '#button-<%= cid %>'
        , sel_reset = '#reset-<%= cid %>'
        ;

    function Datepicker() {
        var self = this;
        ly.base(this, {
            template:'/assets/lib/bootstrap/widgets/datepicker/datepicker.vhtml',
            model:false,
            view:false
        });

        self['_value'] = 0;

        // add Events
        this.on('init', _init);
    }

    ly.inherits(Datepicker, ly.Gui);

    Datepicker.prototype.value = function (date) {
        if (null != date) {
            if (date instanceof Date) {
                date = date.getTime(); // number
            }
            this['_value'] = date;
            this.bindTo(_refresh)();
        }
        return this['_value'];
    };

    Datepicker.prototype.startDate = function (date) {
        if(null!=date){
            var  $button = $(_.template(sel_button, this));
            if (!(date instanceof Date)) {
                date = new Date(date);
            }
            $button.datepicker('setStartDate', date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate());
            $('#datepicker').datepicker('update');
        }
        return this;
    };

    Datepicker.prototype.endDate = function (date) {
        if(null!=date){
            var  $button = $(_.template(sel_button, this));
            if (!(date instanceof Date)) {
                date = new Date(date);
            }
            $button.datepicker('setEndDate', date.getFullYear() + '-' + (date.getMonth()+1) + '-' + date.getDate());
            $('#datepicker').datepicker('update');
        }
        return this;
    };
    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , $button = $(_.template(sel_button, self))
            , now = new Date()
            ;

        self['_label'] = $button.html();

        //-- init start point --//
        $button.attr('data-date', now.getFullYear() + '-' + (now.getMonth()+1) + '-' + now.getDate());

        //-- datepicker--//
        $button.datepicker()
            .on('changeDate', function (e) {
                self.value(e['date']);
                $button.datepicker('hide');
                self.trigger('change', e);
            });

        //-- reset handlers --//
        ly.el.click(_.template(sel_reset, self), function () {
            self.value(0);
        }, self);

    }

    function _refresh() {
        var $button = $(_.template(sel_button, this))
            , formatted = this.bindTo(_formatDate)(this['_value']);
        ;
        $button.html(formatted);
    }

    function _formatDate(milliseconds) {
        if (milliseconds === 0) {
            return this['_label'];
        }
        var lang = app['lang']
            , $button = $(_.template(sel_button, this))
            , d = new Date(milliseconds)
            , curr_date = d.getDate()
            , curr_month = d.getMonth()
            , curr_year = d.getFullYear()
            ;
        if ($.fn.datepicker.dates[lang]) {
            return curr_date + ' ' + $.fn.datepicker.dates[lang]['monthsShort'][curr_month] + ' ' + curr_year;
        }
        return curr_date + '-' + curr_month + '-' + curr_year;
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('ly.gui.widgets.Datepicker');
    ly.gui.widgets.Datepicker = Datepicker;

})();

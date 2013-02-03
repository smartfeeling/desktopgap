(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']
        , device = desktopgap['device']

        , sel_self = '#<%= cid %>'
        , sel_max = '#max_memory-<%= cid %>'
        , sel_total = '#total_memory-<%= cid %>'
        , sel_free = '#free_memory-<%= cid %>'
        , sel_used = '#used_memory-<%= cid %>'

        , id_pie = 'pie-<%= cid %>'
        ;

    function PageSystem(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/app/pages/system/PageSystem.html'),
            model: false,
            view: false
        });

        this['_title'] = i18n.get('dictionary.page_system');

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageSystem, ly.Gui);

    PageSystem.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageSystem.prototype.title = function () {
        // console.log('TITLE: ' + this['_title']);
        return this['_title'];
    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;

        self['freeMemory'] = device.freeMemory();
        self['totalMemory'] = device.totalMemory();
        self['maxMemory'] = device.maxMemory();
        self['usedMemory'] = device.usedMemory();

        document.addEventListener('resize', function(data){
            self.bindTo(_initCharts)();
        });
    }

    function _initComponents(callback) {
        var self = this
            , $self = $(self.template(sel_self))
            ;

        i18n.translate($self[0]);

        try {
            //-- labels --//
            self.bindTo(_initLabels)();

            //-- charts --//
            self.bindTo(_initCharts)();
        } catch (err) {
            console.error('(PageSystem.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    function _initLabels(){
        var self = this
            , $max = $(self.template(sel_max))
            , $total = $(self.template(sel_total))
            , $free = $(self.template(sel_free))
            , $used = $(self.template(sel_used))
            ;
        $max.html(self['maxMemory'] + ' Mb');
        $total.html(self['totalMemory'] + ' Mb');
        $free.html(self['freeMemory'] + ' Mb');
        $used.html(self['usedMemory'] + ' Mb');
    }

    function _initCharts(){
        var self = this;

        self.bindTo(_initChartPie)();
    }

    function _initChartPie(){
        var self = this
            , pie = self.template(id_pie)

            , lbl_max = i18n.get('dictionary.max_memory')
            , lbl_total = i18n.get('dictionary.total_memory')
            , lbl_free = i18n.get('dictionary.free_memory')
            , lbl_used = i18n.get('dictionary.used_memory')
            , per_total = (self['totalMemory']/self['maxMemory'])*100
            , per_free = (self['freeMemory']/self['maxMemory'])*100
            , per_used = (self['usedMemory']/self['maxMemory'])*100
            ;

        $('#'+pie).html('');

        var data = [
            [lbl_max, self['maxMemory']],
            [lbl_total, self['totalMemory']],
            [lbl_free, self['freeMemory']],
            [lbl_used, self['usedMemory']]
        ];
        var plot_pie = jQuery.jqplot (pie, [data],
            {
                seriesDefaults: {
                    // Make this a pie chart.
                    renderer: jQuery.jqplot.PieRenderer,
                    rendererOptions: {
                        // Put data labels on the pie slices.
                        // By default, labels show the percentage of the slice.
                        showDataLabels: true
                    }
                },
                legend: { show:true, location: 'e' }
            }
        );
    }
    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageSystem');
    desktopgap.gui.pages.PageSystem = PageSystem;

    return desktopgap.gui.pages.PageSystem;

})(this);
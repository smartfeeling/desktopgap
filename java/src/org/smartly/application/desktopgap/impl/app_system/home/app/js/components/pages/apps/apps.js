(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']

        , EVENT_CLICK = 'click'
        , EVENT_FAV = 'favorite'
        , EVENT_ACTION = 'action' // 'run', 'view', 'uninstall'

        , sel_self = '#<%= cid %>'
        ;

    function PageApps(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/apps/apps.html'),
            model: false,
            view: false
        });

        this['_groups'] = {}; // applications grouped

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(PageApps, ly.Gui);

    PageApps.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };

    PageApps.prototype.title = function () {
        return i18n.get('home.applications');
    };

    PageApps.prototype.setData = function (argsArray) {

    };

    PageApps.prototype.addApp = function (app) {

    };

    PageApps.prototype.removeApp = function (app) {

    };

    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            , groups = runtime.appGroups()
            ;
        //-- creates apps structure --//
        /*
         {
         group: {
         app_id_1: {app manifest ...}
         }
         }
         */
        if (_.size(groups) > 0) {
            _.forEach(groups, function (appsArray, groupId) {
                self['_groups'][groupId] = self['_groups'][groupId] || {};
                _.forEach(appsArray, function(app){
                    var app_uid = app['_id'];
                    // add app to group map
                    self['_groups'][groupId][app_uid] = app;
                    // console.log(JSON.stringify(self['_groups'][groupId][uid]));
                });
            });
        }
    }

    function _initComponents(callback) {
        var self = this
            , groups = self['_groups']
            , $self = $(self.template(sel_self))
            ;
        try {
            // loop on groups
            _.forEach(groups, function(group, groupId){
                var group_title = i18n.get('home.' + groupId);
                // console.log(JSON.stringify(group));
                // loop on app in group
                _.forEach(group, function(app, appid){
                    // console.log(group_title + ': ' + JSON.stringify(app));
                    //-- creates group component (a List of App) --//
                    if(!group['instance']){
                       group['instance'] = new desktopgap.gui.list.List({
                           title: group_title,
                           fav:true,
                           items: [],
                           actions: [{_id:'run', icon:'', name:i18n.get('home.act_run')}]  // array of action objects: {_id:'act1', icon:'details.png'}
                       });
                        group['instance'].appendTo($self);
                        group['instance'].on(EVENT_CLICK, function(item){
                            // run application
                            self.bindTo(_runApp)(item);
                        });
                        group['instance'].on(EVENT_FAV, function(item){
                            // add application  to favorites
                            self.trigger(EVENT_FAV, item);
                        });
                        group['instance'].on(EVENT_ACTION, function(action, item){
                            // eval action

                        });
                    }
                    //-- add application to list --//
                    app['is_favorite'] = favorites.has(app);
                    group['instance'].addItem(app);
                });
            });
        } catch (err) {
            console.error('(apps.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    function _openAppDetails(app){
        var self = this
            ;
    }

    function _runApp(app){
        var self = this
            , id = !!app?app['_id']:null
            ;
        if(!!id){
            // console.log('RUN APPLICATION: ' + id);
            runtime.runApp(id);
        } else {
            console.error('(apps.js) _runApp(): Application _id is null. -> ' + JSON.stringify(app));
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageApps');
    desktopgap.gui.pages.PageApps = PageApps;

    return desktopgap.gui.pages.PageApps;

})(this);
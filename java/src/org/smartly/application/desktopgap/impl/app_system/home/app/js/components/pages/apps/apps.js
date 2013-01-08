(function (window) {

    var desktopgap = window.desktopgap
        , runtime = desktopgap.runtime
        , i18n = desktopgap['i18n']
        ;

    function PageApps(options) {
        var self = this
            ;

        ly.base(self, {
            template: load('./js/components/pages/apps/apps.html'),
            model: false,
            view: false
        });

        this['_apps'] = {};

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
                self['_apps'][groupId] = self['_apps'][groupId] || {};
                _.forEach(appsArray, function(app){
                    var uid = app['uid'];
                    // add app to group map
                    self['_apps'][groupId][uid] = app;
                    // console.log(JSON.stringify(self['_apps'][groupId][uid]));
                });
            });
        }
    }

    function _initComponents(callback) {
        var self = this
            , groups = self['_apps']
            ;
        try {
            // loop on groups
            _.forEach(groups, function(group, groupId){
                var group_title = i18n.get('home.' + groupId);
                // console.log(JSON.stringify(group));
                // loop on app in group
                _.forEach(group, function(app, appid){
                    console.log(group_title + ': ' + JSON.stringify(app));
                });
            });
        } catch (err) {
            console.error('(apps.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.pages.PageApps');
    desktopgap.gui.pages.PageApps = PageApps;

    return desktopgap.gui.pages.PageApps;

})(this);
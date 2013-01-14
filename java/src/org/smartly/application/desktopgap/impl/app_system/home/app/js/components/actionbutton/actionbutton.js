/**
 * Action button controller for Control Panel.
 *
 * This is an action delegate controller for main Control Panel actions (exit, restart OS, close OS, ...)
 */
(function (window) {

    var desktopgap = window.desktopgap
        , i18n = window['i18n']

        , EVENT_CLICK = 'click'
        , EVENT_FAV = 'favorite'
        , EVENT_ACTION = 'action'

        , sel_act_main = '#act_main-<%= cid %>'
        , sel_act_shutdown = '#act_shutdown-<%= cid %>'
        , sel_act_hide = '#act_hide-<%= cid %>'
        , sel_act_exit = '#act_exit-<%= cid %>'

        ;


    function ActionButton(options) {
        var self = this
            , template = load('./js/components/actionbutton/actionbutton.html')
            ;

        // console.log(template);

        ly.base(self, {
            template: template,
            model: false,
            view: false
        });

        this['_initialized'] = false;

        // add listeners
        this.on('init', _init);
    }

    ly.inherits(ActionButton, ly.Gui);

    ActionButton.prototype.appendTo = function (parent, callback) {
        var self = this;
        ly.base(self, 'appendTo', parent, function () {
            self.bindTo(_initComponents)(callback);
        });
    };


    // ------------------------------------------------------------------------
    //                      p r i v a t e
    // ------------------------------------------------------------------------

    function _init() {
        var self = this
            ;

    }

    function _initComponents(callback) {
        try {
            //-- translate --//
            i18n.translate();

            this.bindTo(_initHandlers)();

        } catch (err) {
            console.error('(actionbutton.js) _initComponents(): ' + err);
        }
        ly.call(callback);
    }

    function _initHandlers(){
        var self = this;
        try {
            //-- Action Main--//
            var $act_main = self.template(sel_act_main);
            ly.el.click($act_main, function(){
                desktopgap.runtime.exit();
            });

            //-- Action Hide--//
            var $act_hide = self.template(sel_act_hide);
            ly.el.click($act_hide, function(){
                desktopgap.frame.minimize();
            });

            //-- Action Exit--//
            var $act_exit = self.template(sel_act_exit);
            ly.el.click($act_exit, function(){
                desktopgap.runtime.exit();
            });

            //-- Action ShutDown--//
            var $act_shutdown = self.template(sel_act_shutdown);
            ly.el.click($act_shutdown, function(){
                desktopgap.runtime.shutdown();
            });
        } catch(err){
            console.error('(actionbutton.js) _initHandlers(): ' + err);
        }
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    ly.provide('desktopgap.gui.actionbutton.ActionButton');
    desktopgap.gui.actionbutton.ActionButton = ActionButton;

    return desktopgap.gui.actionbutton.ActionButton;

})(this);
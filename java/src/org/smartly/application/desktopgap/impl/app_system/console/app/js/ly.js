(function (window, $, _, Backbone) {

    // ------------------------------------------------------------------------
    //                      initialization
    // ------------------------------------------------------------------------

    // Avoid `console` errors in browsers that lack a console.
    if (!(window.console && window.console.log)) {
        (function () {
            var noop = function () {
            };
            var methods = ['assert', 'clear', 'count', 'debug', 'dir', 'dirxml', 'error', 'exception', 'group', 'groupCollapsed', 'groupEnd', 'info', 'log', 'markTimeline', 'profile', 'profileEnd', 'markTimeline', 'table', 'time', 'timeEnd', 'timeStamp', 'trace', 'warn'];
            var length = methods.length;
            var console = window.console = {};
            while (length--) {
                console[methods[length]] = noop;
            }
        }());
    }

    // ------------------------------------------------------------------------
    //                      Logging
    // ------------------------------------------------------------------------

    var console = {
        log: function () {
            try {
                if (window['console'] && window['console'].log) {
                    if (typeof window['console'].log === "function") {
                        window['console'].log.apply(window['console'], arguments);
                    } else {
                        window['console'].log(arguments[0]);
                    }
                }
            } catch (ignored) {
            }
        },

        info: function () {
            try {
                if (window['console'] && window['console'].info) {
                    if (typeof window['console'].info === "function") {
                        window['console'].info.apply(window['console'], arguments);
                    } else {
                        window['console'].info(arguments[0]);
                    }
                }
            } catch (ignored) {
            }
        },

        warn: function () {
            try {
                if (window['console'] && (typeof window['console'].warn) != "undefined") {
                    if (typeof window['console'].warn === "function") {
                        window['console'].warn.apply(window['console'], arguments);
                    } else {
                        window['console'].warn(arguments[0]);
                    }
                }
            } catch (ignored) {
            }
        },

        error: function () {
            try {
                if (window['console'] && window['console'].error) {
                    if (typeof window['console'].error === "function") {
                        window['console'].error.apply(window['console'], arguments);
                    } else {
                        window['console'].error(arguments[0]);
                    }
                }
            } catch (ignored) {
            }
        }
    };

    // ------------------------------------------------------------------------
    //                      AJAX
    // ------------------------------------------------------------------------


    // This function is called from the user's script.
    // Arguments -
    //	url	- The url of the serverside script that is to be called. Append all the arguments to
    //			this url - eg. 'get_data.php?id=5&car=benz'
    //	callback - Function that must be called once the data is ready.
    //	method - GET or POST
    //  params -
    //  error - error function
    function ajax(url, callback, method, params, error) {
        var http = _getHTTPObject() //The XMLHttpRequest object is recreated at every call - to defeat Cache problem in IE
            , async = !!callback
            ;
        if (!http || !url) return;
        if (http.overrideMimeType) http.overrideMimeType('text/xml');

        if (!method) var method = 'GET';//Default GET

        if (params instanceof String) {
            params = params;
        } else if (typeof params == 'object') {
            var result = '';
            for (var m in params) {
                if (!!m) {
                    if (result.length > 0) {
                        result += '&';
                    }
                    result += m.concat("=").concat(encodeIf(params[m]));
                }
            }
            params = result;
        } else {
            params = null;
        }

        if (method === 'POST') {
            try {
                http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
                http.setRequestHeader('Content-length', !!params ? params.length : 0);
                http.setRequestHeader('Connection', 'close');
            } catch (err) {
            }
        }

        //Kill the Cache problem in IE.
        /*var now = "uid=" + new Date().getTime();
         url += (url.indexOf("?") + 1) ? "&" : "?";
         url += now; */

        http.open(method, url, true);
        if (async) {
            http.onreadystatechange = function () {//Call a function when the state changes.
                if (http.readyState == 4) {//Ready State will be 4 when the document is loaded.
                    if (http.status == 200) {
                        var result = '';
                        if (http.responseText) result = http.responseText;
                        //Give the data to the callback function.
                        if (callback) callback(result);
                    } else { //An error occured
                        if (error) error(http.status);
                    }
                }
            }
            http.send(params);
        } else {
            http.send(params);
            return  http.responseText;
        }
    }

    // ------------------------------------------------------------------------
    //                      Utils
    // ------------------------------------------------------------------------

    function validateEmail(email) {
        var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return re.test(email);
    }

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
        var regexS = "[\\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(window.location.search);
        if (results == null)
            return "";
        else
            return decodeURIComponent(results[1].replace(/\+/g, " "));
    }


    /**
     * Format string replacing place holders.
     * ex. format("Hello {0}", "world") give output "Hello world".
     * ex. format("Hello {name}", {name:'jack'}) give output "Hello jack".
     * ex. format("Hello {SF.encode:name}", {name:'http://'}) give output "Hello http%3A%2F%2F".
     * @params: var_args
     *      - {string} text to format. ex. "Hello {0}"
     *      - {string} variable number of arguments. ex. "world". Optionally
     *        first parameter after text can be an object,
     *        ex. format("hello Mr.'{name}', you are a '{1}' '{adj}'", {name:"potato", adj:"vegetable}, "nice")
     *        returns "hello Mr. 'potato', you are a 'nice' 'vegetable'".
     **/
    /**
     function format() {
     var args = arguments,
     text;
     if (args && args.length > 0) {
     text = args[0];
     if (null != text) {
     // replace numbers {0}, {1}
     text = text.replace(/{(\d+)}/g, function (match, number) {
     var i = parseInt(number) + 1,
     val = args[i], result;
     result = (typeof val != 'undefined' && null != val && typeof val != 'object')
     ? val
     : match;
     return result;
     });
     // replace strings {name1}, {name2}
     if (typeof(args[1]) === "object") {
     // get every . : or character [\.:\w]
     text = text.replace(/{([\.:\w]+)}/g, function (match, number) {
     var n = match.substring(1, match.length - 1),
     tokens = n.split(":"),
     func = tokens.length > 1 ? eval(tokens[0]) : null,
     name = tokens.length > 1 ? tokens[1] : n,
     value = typeof args[1][name] != 'undefined'
     ? args[1][name]
     : null,
     result;
     if (func) {
     value = func(value);
     }
     result = (typeof value != 'undefined' && null != value)
     ? value
     : match;
     return result;
     });
     }
     } else {
     text = "";
     }
     } else {
     text = "";
     }
     return text;
     }
     **/
    /**
     * shortcut to _.template(text, data, {interpolate:/\{(.+?)\}/g});
     * @param text template text
     * @param data object
     * @return {string}
     */
    function template(text, data) {
        return _.template(text, data, {interpolate: /\{(.+?)\}/g});
    }

    function isNull(arg) {
        try {
            if (null == arg || 'NULL' == arg) return true;
            return _.isArray(arg)
                ? (arg.length > 0 ? arg.length === 1 && isNull(arg[0]) : true)
                : (_.isObject(arg) ? _.size(arg) === 0 : (arg === 'NULL' || arg == '' || arg['response'] === 'NULL'));
        } catch (err) {
            ly.console.error(err);
        }
    }

    function hasText(text) {
        return null != text ? (text.toString().trim().length > 0) : false;
    }

    function replaceAll(text, searchfor, replacetext) {
        if (_.isString(text)) {
            return text.replace(new RegExp(searchfor, 'g'), replacetext);
        }
        return '';
    }

    // ------------------------------------------------------------------------
    //                      Utils Objects
    // ------------------------------------------------------------------------

    /**
     * Get or set a value into object using a path ('prop1.prop2.prop3').
     * Usage:
     *  var object = {};
     *  SF.value(object, 'prop1.prop2', 'hello'); // assign value
     *  var val = SF.value(object, 'prop1.prop2'); // return value
     **/
    function value(root, path, value) {
        var segments = path.split('.'),
            cursor = root || window,
            segment,
            i;

        for (i = 0; i < segments.length - 1; ++i) {
            segment = segments[i];
            cursor = cursor[segment] = cursor[segment] || {};
        }
        if (null != value) {
            cursor[segments[i]] = value;
        }
        return cursor[segments[i]];
    }

    function provide(path) {
        value(window, path);
        return {
            exports: function () {
                if (arguments.length === 1) {
                    value(window, path, arguments[0]);
                } else if (arguments.length === 2) {
                    window[path][arguments[0]] = arguments[1];
                }
            }
        };
    }

    /**
     * Deep extend objects
     * @param obj
     * @return {*}
     */
    function deepExtend(obj) {
        var parentRE = /#{\s*?_\s*?}/,
            slice = Array.prototype.slice,
            hasOwnProperty = Object.prototype.hasOwnProperty;

        _.each(slice.call(arguments, 1), function (source) {
            for (var prop in source) {
                if (hasOwnProperty.call(source, prop)) {
                    if (_.isUndefined(obj[prop])) {
                        obj[prop] = source[prop];
                    } else if (_.isString(source[prop]) && parentRE.test(source[prop])) {
                        if (_.isString(obj[prop])) {
                            obj[prop] = source[prop].replace(parentRE, obj[prop]);
                        }
                    } else if (_.isArray(obj[prop]) || _.isArray(source[prop])) {
                        if (!_.isArray(obj[prop]) || !_.isArray(source[prop])) {
                            throw 'Error: Trying to combine an array with a non-array (' + prop + ')';
                        } else {
                            obj[prop] = _.reject(deepExtend(obj[prop], source[prop]), function (item) {
                                return _.isNull(item);
                            });
                        }
                    } else if (_.isObject(obj[prop]) || _.isObject(source[prop])) {
                        if (!_.isObject(obj[prop]) || !_.isObject(source[prop])) {
                            throw 'Error: Trying to combine an object with a non-object (' + prop + ')';
                        } else {
                            obj[prop] = deepExtend(obj[prop], source[prop]);
                        }
                    } else {
                        if (null != prop) {
                            obj[prop] = source[prop];
                        }
                    }
                }
            }
        });
        return obj;
    }

    function createModel(object) {
        object = _.isObject(object) ? object : {};
        return new (Backbone.Model.extend(object))();
    }

    // ------------------------------------------------------------------------
    //                      Inheritance
    // ------------------------------------------------------------------------

    /**
     * Inherit the prototype methods from one constructor into another.
     *
     * Usage:
     * <pre>
     * function ParentClass(a, b) { }
     * ParentClass.prototype.foo = function(a) { }
     *
     * function ChildClass(a, b, c) {
     *   ParentClass.call(this, a, b);
     * }
     *
     * goog.inherits(ChildClass, ParentClass);
     *
     * var child = new ChildClass('a', 'b', 'see');
     * child.foo(); // works
     * </pre>
     *
     * In addition, a superclass' implementation of a method can be invoked
     * as follows:
     *
     * <pre>
     * ChildClass.prototype.foo = function(a) {
     *   ChildClass.superClass_.foo.call(this, a);
     *   // other code
     * };
     * </pre>
     *
     * @param {Function} constructor Child class.
     * @param {Function} superConstructor Parent class.
     */
    function inherits(constructor, superConstructor) {
        if (constructor && superConstructor) {
            var super_ = 'super_';

            /** @constructor */
            function tempCtor() {
            }

            tempCtor.prototype = superConstructor.prototype;
            constructor[super_] = superConstructor.prototype;
            constructor.prototype = new tempCtor();
            constructor.prototype.constructor = constructor;
        } else {
            console.error('Constructor and SuperConstructor must be valid. Constructor=' + constructor);
        }
    }

    /**
     * Call up to the superclass.
     *
     * If this is called from a constructor, then this calls the superclass
     * contructor with arguments 1-N.
     *
     * If this is called from a prototype method, then you must pass
     * the name of the method as the second argument to this function. If
     * you do not, you will get a runtime error. This calls the superclass'
     * method with arguments 2-N.
     *
     * This function only works if you use SF.inherits to express
     * inheritance relationships between your classes.
     *
     * This function is a compiler primitive. At compile-time, the
     * compiler will do macro expansion to remove a lot of
     * the extra overhead that this function introduces. The compiler
     * will also enforce a lot of the assumptions that this function
     * makes, and treat it as a compiler error if you break them.
     *
     * @param {!Object} me Should always be "this".
     * @param {*=} opt_methodName The method name if calling a super method.
     * @param {...*} var_args The rest of the arguments.
     * @return {*} The return value of the superclass method.
     */
    function base(me, opt_methodName, var_args) {
        var super_ = 'super_', // superClass_
            caller = arguments.callee.caller;

        if (caller[super_]) {
            // This is a constructor. Call the superclass constructor.
            return caller[super_].constructor.apply(me, Array.prototype.slice.call(arguments, 1));
        }

        var args = Array.prototype.slice.call(arguments, 2);
        var foundCaller = false;
        for (var ctor = me.constructor;
             ctor; ctor = ctor[super_] && ctor[super_].constructor) {
            if (ctor.prototype[opt_methodName] === caller) {
                foundCaller = true;
            } else if (foundCaller) {
                return ctor.prototype[opt_methodName].apply(me, args);
            }
        }

        // If we did not find the caller in the prototype chain,
        // then one of two things happened:
        // 1) The caller is an instance method.
        // 2) This method was not called by the right caller.
        if (me[opt_methodName] === caller) {
            return me.constructor.prototype[opt_methodName].apply(me, args);
        } else {
            if (me.constructor[super_] && me.constructor[super_][opt_methodName]) {
                return me.constructor[super_][opt_methodName].apply(me, args);
            }
            throw Error(
                'base("' + opt_methodName + '") called from a method of one name ' +
                    'to a method of a different name');
        }
    }

    // ------------------------------------------------------------------------
    //                      f u n c t i o n
    // ------------------------------------------------------------------------

    /**
     * call a function
     * Params: func, context, parameters[]
     * Params: func, parameters[]
     **/
    function call() {
        var args = _.toArray(arguments);
        if (_.isFunction(args[0])) {
            var func = args[0];
            if (args.length === 1) {
                func();
            } else if (args.length === 2) {
                if (_.isArray(args[1])) {
                    func.apply(this, args[1]);
                } else {
                    func.apply(args[1]);
                }
            } else {
                if (_.isArray(args[2])) {
                    func.apply(args[1], args[2]);
                } else if (_.isArray(args[1])) {
                    func.apply(args[2], args[1]);
                } else {
                    func();
                }
            }
        }
    }

    // ------------------------------------------------------------------------
    //                      c o o k i e
    // ------------------------------------------------------------------------

    var cookie = {

        get: function (name) {
            try {
                var result = "",
                    nameEQ = name + "=",
                    ca = document.cookie.split(';'),
                    c = null;
                for (var i = 0; i < ca.length; i++) {
                    c = ca[i];
                    while (c.charAt(0) == ' ') c = c.substring(1, c.length);
                    if (c.indexOf(nameEQ) == 0) {
                        result = c.substring(nameEQ.length, c.length);
                        return result.trim();
                    }
                }
                return result;
            } catch (err) {
                return "";
            }
        },

        set: function (name, value, days) {
            try {
                var expires = "", date = null;
                if (days) {
                    date = new Date();
                    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                    expires = "; expires=" + date.toGMTString();
                }
                document.cookie = (name + "=" + value + expires + "; path=/").trim();
            } catch (err) {
                // unsupported
            }
        }

    };

    // ------------------------------------------------------------------------
    //                      e l e m e n t s
    // ------------------------------------------------------------------------

    var el = {

        isInput: function (selector) {
            var $el = $(selector)
                , result = true;
            $el.each(function () {
                if (this.tagName.toLowerCase() !== 'input'
                    && this.tagName.toLowerCase() !== 'textarea'
                    && this.tagName.toLowerCase() !== 'select') {
                    result = false;
                    return false;
                }
            });
            return result;
        },

        value: function (selector, newvalue) {
            var $el = $(selector)
                , is_checkbox = $el.attr('type') === 'checkbox'
                ;
            if (null != newvalue) {
                if (is_checkbox) {
                    if (!!newvalue) {
                        $el.attr('checked', 'true')
                    } else {
                        $el.removeAttr('checked');
                    }
                } else {
                    if (_.isBoolean(newvalue)) {
                        $el.val(!!newvalue)
                    } else {
                        if (el.isInput($el)) {
                            $el.val(newvalue);
                        } else {
                            $el.html(newvalue);
                        }
                    }
                }
            }
            return is_checkbox ? $el.is(':checked') : $el.val();
        },

        attr: function (selector, attr, newvalue) {
            var $el = $(selector);
            if (null != newvalue) {
                $el.attr(attr, newvalue);
            }
            return $el.attr(attr);
        },

        scrollTo: function (selector, navheight) {
            _.debounce(function () {
                var offset = $(selector).offset();
                var offsetTop = offset.top;
                var totalScroll = offsetTop - (navheight || 0);
                $('body,html').animate({
                    scrollTop: totalScroll
                }, 500);
            }, true, 500)();
        },

        scrollTop: function () {
            this.scrollTo('html');
        },

        /**
         * Avoid double click
         * @param selector
         * @param callback
         * @param context
         * @param delay
         */
        click: function (selector, callback, context, delay) {
            var $el = $(selector);
            if (_.isFunction(callback)) {
                $el.unbind('click');
                $el.on('click', function () {
                    var $self = $(this);
                    _.debounce(_.bind(callback, context || $self, $self), delay || 1000, true)();
                    return false;
                });
            }
        },

        keypress: function (selector, key, callback, context, delay) {

            var $el = $(selector);
            if (_.isFunction(callback)) {
                $el.unbind('keypress');
                $el.on('keypress', function (evt) {
                    var $self = $(this);
                    var evt = (evt) ? evt : ((event) ? event : null);

                    if ((evt.keyCode == key)) {
                        return callback.apply(context || $self, [evt]);
                    }
                    return true;
                });
            }
        }
    };

    // ------------------------------------------------------------------------
    //                      remote
    // ------------------------------------------------------------------------

    var remote = {

        /**
         *
         * @param options  {type:"GET", url:"/rest..", "data":{}, "success": function, "error":function}
         * @private
         */
        ajax: function _ajax(options) {
            var type = options['type'] || 'GET'
                , url = options['url']
                , data = options['data']
                , ctx = options['context'] || this
                , fn_success = options['success']
                , fn_error = options['error']
                ;
            if (!!url) {
                $.ajax({
                    type: type,
                    url: url,
                    dataType: 'json',
                    data: data,
                    cache: false, // disabling cache avoid IE caches ajax responses

                    success: function (response) {
                        ly.call(fn_success, ctx, [response]);
                    },

                    error: function (jqXHR, textStatus, errorThrown) {
                        if (!!fn_error) {
                            ly.call(fn_error, ctx, [response]);
                        } else {
                            ly.console.error('"' + errorThrown['message'] + '" caused from:' + jqXHR['responseText']);
                        }
                    }
                });
            }
        }
    };


    // ------------------------------------------------------------------------
    //                      g u i
    // ------------------------------------------------------------------------

    function Gui(options) {
        this['cid'] = _.uniqueId('comp-');
        this['parent'] = null;
        this['options'] = options;
        this['_template'] = !!options ? options['template'] || '' : '';
        this['model'] = !!options ? options['model'] || null : null;
        this['view'] = !!options ? options['view'] || null : null;

        _.extend(this, Backbone.Events);
    }

    Gui.prototype.bindTo = function (func) {
        return _.bind(func, this);
    };

    Gui.prototype.template = function (text) {
        return _.template(text, this);
    };

    Gui.prototype.appendTo = function (selector, callback) {
        var self = this;
        self['parent'] = $(selector);
        if (self['_template'].indexOf('/') === 0) {
            ajax(self['_template'], function (markup) {
                _attach(self, markup, callback);
            });
        } else {
            _attach(self, self['_template'], callback);
        }
    };

    Gui.prototype.detach = function (remove) {
        if (!!remove) {
            _remove(this);
        } else {
            _detach(this);
        }
    };

    Gui.prototype.children = function (selector) {
        return !!selector ? $('#' + this['cid']).find(selector) : $('#' + this['cid']).find();
    };

    Gui.prototype.attributes = function (attributes) {
        if (!!attributes && null != this['model']) {
            this['model'].set(attributes, {silent: true});
        }
        return (null != this['model']) ? this['model'].attributes : null;
    };

    Gui.prototype.hasModel = function () {
        return null != this['model'];
    };

    Gui.prototype.hasView = function () {
        return null != this['view'];
    };

    Gui.prototype.bindModel = function (model) {
        var self = this;
        if (model instanceof Backbone.Model) {
            self['model'] = model;
        } else if (_.isObject(model)) {
            self['model'] = new (Backbone.Model.extend(model))();
        } else {
            // unsupported model
            return;
        }

        self['model'].on('change', function (model, changes) {
            _changeModel(self, model, changes);
        });

        _initView(self);
    };

    Gui.prototype.set = function (data) {
        var self = this;
        _.each(data, function (value, key) {
            try {
                _setValue(self, key, value);
            } catch (err) {
                console.error(err);
            }
        });
    };

    Gui.prototype.get = function (name) {
        if (this.hasModel() && this['model'].has(name)) {
            return this['model'].get(name);
        }
        return el.value('#' + name);
    };
    //-- private --//

    function _getElement(key) {
        var $el = $('#' + key);
        return !!$el[0] ? $el : $('[data-id="' + key + '"]');
    }

    function _setValue(self, key, value) {
        var item = {};
        item[key] = value;
        el.value(_getElement(key), value);
        self['model'].set(item, {silent: true});
        self['model'].change();
    }

    function _attach(self, markup, callback) {
        try {
            //-- init model --//
            _initModel(self);

            //-- load template --//
            var $markup = $(_.template(markup, {cid: self['cid'], model: self['model']}));
            self['parent'].append($markup);
            self['_component'] = $markup;

            //-- init view --//
            _initView(self);

            // trigger 'init'
            _trigger(self, 'init', self);
        } catch (err) {
            // probably markup if corrupted
            console.error(err);
        }

        // callback function if any
        if (_.isFunction(callback)) {
            callback.call(self);
        }
    }

    function _detach(self) {
        if (!!self['_component']) {
            self['_component'].detach();
        }
    }

    function _remove(self) {
        if (!!self['_component']) {
            self['_component'].remove();
        }
    }

    function _initModel(self) {
        // model
        if (null != self['model']) {
            self['model'] = _toModel(self, self['model']);
            self['model'].on('change', function (model, changes) {
                _changeModel(self, model, changes);
            });
        }
    }

    function _initView(self) {
        var cid = self['cid']
            , model = self['model']
            , view = self['view'];
        // view
        if (null != view) {
            self['view'] = _toView(self, view, model);
            // delegate events for components using cid as class
            var viewEvent = "change ." + cid
                , events = {};
            events[viewEvent] = function (e) {
                _changedViewField(self, self['view'], e);
            };
            self['view'].delegateEvents(events);

            //-- bind model --//
            _bindModelDefaults(self, model);
        }
    }

    function _bindModelDefaults(self, model) {
        if (!!model && !!model['defaults']) {
            _.each(model['defaults'], function (value, key) {
                try {
                    el.value(_getElement(key), model['defaults'][key]);
                } catch (err) {
                    console.error(err);
                }
            });
        }
    }

    function _toModel(self, value) {
        var model = value;
        if (_.isBoolean(value)) {
            model = new (Backbone.Model.extend({}))();
        } else if (value instanceof Backbone.Model) {
            // already assigned
        } else if (_.isObject(value)) {
            // DEFAULTS CAN BE A FUNCTION
            if (_.isFunction(value['defaults'])) {
                value['defaults'] = value['defaults'].apply(self, self);
            }
            model = new (Backbone.Model.extend(value))();
        }
        model['validate'] = model['validate'] || function () {
        };
        return model;
    }

    function _toView(self, value, model) {
        var view = value
            , cid = self['cid']
            , options = {el: '#' + cid}
            ;
        if (_.isBoolean(value)) {
            if (!!self['model']) {
                options = _.extend(options, {model: model});
            }
            view = new (Backbone.View.extend(options))();
        } else if (value instanceof Backbone.View) {
            // already assigned
            if (!_.isEmpty(value['options'])) {
                // check el
                if (!value['options']['el']) {
                    value['options']['el'] = '#' + cid
                }
                // check model
                if (!value['options']['model'] && !!model) {
                    value['options']['model'] = model;
                }
            } else {
                if (!!self['model']) {
                    options = _.extend(options, {model: model});
                }
                view = new (Backbone.View.extend(options))();
            }
        } else if (_.isObject(value)) {
            value = _.extend(value, options);
            view = new (Backbone.View.extend(value))();
        }
        return view;
    }

    function _validate(self, attributes) {
        return !!self['model'] && _.isFunction(self['model'].validate) ? !_.isString(self['model'].validate(attributes)) : true;
    }

    function _changedViewField(self, view, e) {
        if (!!view['model']) {
            var target = e.target
                , val = ly.el.value(target)
                , key = $(target).attr('id')
                , item = {}
                ;
            item[key] = val;
            // update model
            var valid = _validate(self, item);
            if (!valid) {
                el.value(target, view['model'].get(key));
            } else {
                view['model'].set(item, {silent: true});
                view['model'].change();
                //_trigger(self, 'change', self, view['model'], key, view['model'].get(key));
            }
        }
    }

    function _changeModel(self, model, changed) {
        var changes = changed['changes'] || model['changed'];
        _.each(changes, function (value, key, list) {
            try {
                if (null != value) {
                    // update view
                    el.value('#' + key, model.get(key));

                    // trigger change event
                    _trigger(self, 'change', model, key, model.get(key));
                }
            } catch (err) {
                console.error(err);
            }
        });
    }


    function _trigger(self, name) {
        var args = Array.prototype.slice.call(arguments, 1);
        self.trigger.apply(self, args);
    }

    // ------------------------------------------------------------------------
    //                      private - (not exported)
    // ------------------------------------------------------------------------

    function _getHTTPObject() {
        var http = false;
        //Use IE's ActiveX items to load the file.
        if (typeof ActiveXObject != 'undefined') {
            try {
                http = new ActiveXObject("Msxml2.XMLHTTP");
            } catch (e) {
                try {
                    http = new ActiveXObject("Microsoft.XMLHTTP");
                } catch (E) {
                    http = false;
                }
            }
            //If ActiveX is not available, use the XMLHttpRequest of Firefox/Mozilla etc. to load the document.
        } else if (window.XMLHttpRequest) {
            try {
                http = new XMLHttpRequest();
            } catch (e) {
                http = false;
            }
        }
        return http;
    }

    // ------------------------------------------------------------------------
    //                      e x p o r t s
    // ------------------------------------------------------------------------

    var exports = window.ly = window.smartly = window.smartly || {};

    //-- log --//
    exports.console = console;

    //-- ajax --//
    exports.ajax = ajax;

    //-- utils --//
    exports.validateEmail = validateEmail;
    exports.getParameterByName = getParameterByName;
    // exports.format = format;
    exports.template = template;
    exports.isNull = isNull;
    exports.hasText = hasText;
    exports.replaceAll = replaceAll;

    //-- object utils --//
    exports.provide = provide;
    exports.value = value;
    exports.deepExtend = deepExtend;

    //-- Backbone --//
    exports.createModel = createModel;

    //-- inheritance --//
    exports.inherits = inherits;
    exports.base = base;

    //-- functions --//
    exports.call = call;

    //-- cookie --//
    exports.cookie = cookie;

    //-- elements --//
    exports.el = el;

    //-- remote --//
    exports.remote = remote;

    //-- gui --//
    exports.Gui = Gui;

})(this, this['$'], this['_'], this['Backbone']);
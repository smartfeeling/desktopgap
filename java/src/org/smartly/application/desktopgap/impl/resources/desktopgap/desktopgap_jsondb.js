(function (window) {

    'use strict';

    var NAME = 'jsondb';

    var java = window.java; // function to get a java reference

    // ------------------------------------------------------------------------
    //                      DB Connection
    // ------------------------------------------------------------------------

    var module = {

        toString: function () {
            return !!this['_java'] ? this['_java'].toString() : 'Call "create(dbName)" to obtain DB reference.';
        },

        create: function (dbName) {
            this['_java'] = java(NAME);
            if (!!this['_java']) {
                return new DB(this['_java'].create(dbName));
            } else {
                console.error('Tool "' + NAME + '" not found.');
            }
            return null;
        }
    };

    // ------------------------------------------------------------------------
    //                      DB
    // ------------------------------------------------------------------------

    function DB(javaTool) {
        // assign java tool
        this['_java'] = javaTool;
    }

    DB.prototype.toString = function () {
        return !!this['_java'] ? this['_java'].toString() : 'DB not initialized!';
    };

    DB.prototype.collection = function (name) {
        return new Collection(this, name || '_anonymous');
    };

    // ------------------------------------------------------------------------
    //                      Collection
    // ------------------------------------------------------------------------

    function Collection(db, collName) {
        // assign java tool
        this['_java'] = db['_java'].collection(collName);
    }

    Collection.prototype.findOne = function () {
        try {
            var result = '';
            var args = toArray(arguments);
            if (args.length === 1) {
                result = this['_java'].findOneAsString('_id', args[0]);
            } else if (args.length === 2) {
                result = this['_java'].findOneAsString(args[0], args[1]);
            }
            return null != result ? JSON.parse(result.toString()) : null;
        } catch (err) {
            console.error('(desktopgap_jsondb.js) findOne(): ' + err);
            return null;
        }
    };

    Collection.prototype.upsert = function (item) {
        console.log('UPSERT: ' + item);
        item = isString(item) ? item : JSON.stringify(item);
        var result = this['_java'].upsert(item).toString();
        return JSON.parse(result);
    };

    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    var exports = module;

    return exports;

})(this);
;
(function (window) {

    'use strict';

    var NAME = 'fileUtils';

    var java = window.java; // function to get a java reference

    function _tool() {
        return java(NAME);
    }

    // ------------------------------------------------------------------------
    //                      FileUtils
    // ------------------------------------------------------------------------

    function FileUtils() {

    }

    /**
     * Open Native file selector
     * @param callback returns array of file names as Array of String
     */
    FileUtils.prototype.select = function (callback) {
        var self = this;
        var tool = _tool();
        if (!!tool) {
            delay(function () {
                var jsonArray = tool.select();
                if (!!jsonArray) {
                    try {
                        call(callback, self, [JSON.parse(jsonArray)]);
                    } catch (err) {
                        call(callback, self, [[]]);
                    }
                } else {
                    // console.log('NOTHING SELECTED');
                    call(callback, self, [[]]);
                }
            }, 10);
        } else {
            call(callback, self, [[]]);
        }
    };

    // --------------------------------------------------------------------
    //               exports
    // --------------------------------------------------------------------

    return new FileUtils();

})(this);
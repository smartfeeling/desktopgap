!(function () {

    var elemId = '[ELEMENT]'
        , value = '[VALUE]'
        ;

    if (!!window.deviceready) {
        setElemValue(elemId, value);
    } else {
        document.addEventListener('deviceready', function () {
            setElemValue(elemId, value);
        }, false);
    }

    function setElemValue(id, val) {
        var elem = document.getElementById(id);
        if (!!elem) {
            var tagName = elem.tagName.toLowerCase();
            if (tagName === 'input') {
                elem.value = val;
            } else {
                elem.innerHTML = val;
            }
        } else {
            // console.warn('(setElemValue.js) Element "' + id + '" not found.');
        }
    }

})();

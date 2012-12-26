!(function () {

    $('#app_content').html('APPLICATION LOADED!!');

    document.addEventListener('deviceready', function () {
        alert('STARTED DESKTOP GAP!! ' + desktopgap['bridge'].toString());

        alert(device.uuid);
    }, false);

})();
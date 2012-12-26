!(function () {

    $('#app_content').html('APPLICATION LOADED!!');

    document.addEventListener('deviceready', function () {
        console.log('STARTED DESKTOP GAP!! ' + desktopgap['bridge'].toString());

        console.log(device.uuid);

        console.log(navigator.connection.type);

        console.open();
    }, false);

})();
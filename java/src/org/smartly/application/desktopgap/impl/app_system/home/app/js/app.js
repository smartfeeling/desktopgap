!(function(){

    $('#app_content').html('APPLICATION LOADED!!');

    desktopgap.events.on('ready', function(){
        alert('STARTED DESKTOP GAP!! ' + desktopgap['bridge'].toString());
    });

})();
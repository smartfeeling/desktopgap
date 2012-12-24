!(function(){

    $('#app_content').html('APPLICATION LOADED!!');

    navigator.events.on('ready', function(){
        alert('STARTED DESKTOP GAP!! ' + desktopgap['bridge'].toString());
    });

})();
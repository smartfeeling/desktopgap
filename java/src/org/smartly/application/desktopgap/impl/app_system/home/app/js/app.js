!(function(){

    $('#app_content').html('APPLICATION LOADED!!');

    desktopgap.callbacks.add(function(){
        alert('STARTED DESKTOP GAP!! ' + desktopgap.foo.toString());
    });

})();
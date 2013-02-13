!(function (window, $) {

    //-- main desktopgap event --//
    document.addEventListener('deviceready', function () {

        //-- init application stage --//
        $(document).ready(function () {

            $("#minefield").minesweeper({
                skin: "default",
                size: [10, 10],
                mines: 10
            });
        });

    }, false);


})(this, jQuery);

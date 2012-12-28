!(function () {

    var eventName = '[EVENT_NAME]'
        , eventData = '[EVENT_DATA]'
    ;

    // create the event
    var evt = document.createEvent('Event');

    // define that the event name is '[EVENT_NAME]'
    evt.initEvent('[EVENT_NAME]', true, true);

    if(!!eventData){
        evt.data = evt.data||[];
        evt.data.push(JSON.parse(eventData));
    }

    // dispatch the Event
    document.dispatchEvent(evt);

})();

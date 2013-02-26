!(function () {

    var eventName = '[EVENT_NAME]';
    var eventData = '[EVENT_DATA]';

    // create the event
    var evt = document.createEvent('Event');

    // define that the event name is '[EVENT_NAME]'
    evt.initEvent(eventName, true, true);

    if (!!eventData) {
        evt.data = evt.data || [];
        evt.data.push(parse(eventData));
    }

    // dispatch the Event
    document.dispatchEvent(evt);

    function parse(data) {
        try {
            return JSON.parse(data)
        } catch (err) {
            console.error(err);
        }
        return data;
    }

})();

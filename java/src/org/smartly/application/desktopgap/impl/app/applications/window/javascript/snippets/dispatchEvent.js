// create the event
var evt = document.createEvent('Event');

// define that the event name is '[EVENT_NAME]'
evt.initEvent('[EVENT_NAME]', true, true);

// dispatch the Event
document.dispatchEvent(evt);
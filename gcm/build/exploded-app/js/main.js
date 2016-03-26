// 1) Acquire Database info
// 2) Listen to new goal reminders
// 3) Create Cron Job w/ Id
// 4) Cron function will send task to GCM

$("body").append("Server has initiated 1" + "<br/>");
var Firebase = require("firebase");
var CronJobManager = require('cron-job-manager');
var manager = new CronJobManager();

var database = new Firebase("https://flickering-inferno-500.firebaseio.com/");
database.child("accounts/-KBBCeO6ekFF8gs4Xsdf").on("value", function(snapshot) {
    var newPost = snapshot.val();
    $("body").append(newPost.email + "<br/>");
    $("body").append(newPost.firstname + "<br/>");

    var message = "My Job";
    manager.add('a_key_string_to_call_this_job', '*/10 * * * * *', function() { $("body").append(message + "<br/>");})
    manager.start('a_key_string_to_call_this_job');



});




/*
notificationobject = [Id, next dateTime, Type]
var apple = {
    type: "macintosh",
    color: "red",
    getInfo: function () {
    return this.color + ' ' + this.type + ' apple';
    }
}
*/
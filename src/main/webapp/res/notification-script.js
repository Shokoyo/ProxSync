/**
 * Created by Jeremias on 24.09.2017.
 */
function removeNotification(event, key) {
    console.log(key);
    var watchRef = firebase.database().ref("/users/" + uid + "/notifications/" + key.split(".").join("-") + "/hidden");
    watchRef.set(true);
    watchRef.once('value', function() {
        document.getElementById("notifications-" + key).style.display = 'none';
        var div = document.getElementById("dividers-" + key);
        if(div != null) {
            div.style.display='none';
        }
    });
    event.stopPropagation();
}

function watchNext(event, key) {
    event.stopPropagation();
}

$(document).on('click', function (e) {
    if ($(e.target).closest("#notification-menu").length === 0 && menuNotifications.open) {
        menuNotifications.open = true;
    } else {
        menuNotifications.open = false;
    }
});

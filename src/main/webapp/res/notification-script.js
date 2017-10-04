/**
 * Created by Jeremias on 24.09.2017.
 */
function removeNotification(key) {
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
}

function watchNext(event, key) {
    var nextEpisodeRef = firebase.database().ref("users/" + uid + "/watchlist/" + key.replace(".", "-") + "/episode");
    var newWindow = window.open('', '_blank');
    nextEpisodeRef.once('value', function(snapshot) {
        var nextEpisode = parseInt(snapshot.val());
        nextEpisode += 1;
        if(!(window.location.pathname.includes("ProxSync") || window.location.pathname.includes("AniSync") || window.location.pathname === "/")) {
            newWindow.location.href=window.location.href.substring(0, window.location.href.lastIndexOf("/")) + "/?id=" + key + "&episode=" + nextEpisode, "_blank";
        } else {
            loadVideoByEpisode("https://9anime.to/watch/" + key);
        }
    });
}

$(document).on('click', function (e) {
    if ($(e.target).closest("#notification-menu").length === 0 && !menuNotifications.open) {
        console.log("false");
        menuNotifications.open = false;
    } else {
        menuNotifications.open = true;
        console.log("true");
    }
});

$("#notification-menu").on('click', function(e) {
    e.stopPropagation();
});
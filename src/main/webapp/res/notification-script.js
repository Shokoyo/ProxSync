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
    if(!(window.location.pathname === "/ProxSync/" || window.location.pathname === "/AniSync/" || window.location.pathname === "/")) {
        var newWindow = window.open('', '_blank');
    }
    nextEpisodeRef.once('value', function(snapshot) {
        var nextEpisode = parseInt(snapshot.val());
        nextEpisode += 1;
        if(!(window.location.pathname === "/ProxSync/" || window.location.pathname === "/AniSync/" || window.location.pathname === "/")) {
            newWindow.location = "../?id=" + key + "&episode=" + nextEpisode;
        } else {
            loadVideoByEpisode("https://9anime.to/watch/" + key, nextEpisode);
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
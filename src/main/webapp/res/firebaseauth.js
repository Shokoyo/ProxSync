var roomCreated = false;
var currentUser;

firebase.auth().onAuthStateChanged(function (authData) {
    if (authData) {
        console.log("Logged in as:", authData.uid);
        if(!authData.isAnonymous && (getCookie("anonymous") === "true" || getCookie("loginData") !== authData.uid)) {
            console.log("non anonymous login");
            setCookie("loginData", authData.uid, 10000);
            setCookie("anonymous", "false", 10000);
            location.reload();
            return;
        }
        currentUser = authData.currentUser;
        uid = authData.uid;
        anonymous = authData.isAnonymous;
        if (!roomCreated) {
            roomCreated = true;
            var url = "" + window.location;
            if (getQueryVariable("r") === null) {
                if (socket.readyState === socket.OPEN) {
                    createRoom();
                } else {
                    socket.onopen = createRoom;
                }
                var id = getQueryVariable("id");
                var episode = getQueryVariable("episode");
                if (id !== null) {
                    loadVideoByEpisode("https://9anime.is/watch/" + id, episode === null ? 1 : parseInt(episode));
                }
            } else {
                if (socket.readyState === socket.OPEN) {
                    joinParamRoom();
                } else {
                    socket.onopen = joinParamRoom;
                }
            }
        } else {
            console.log(uid);
            var userAction = {
                action: "uid",
                value: uid
            };
            socket.send(JSON.stringify(userAction));
        }
        updateAuthButtons(authData);
    }
    else {
        console.log("Not logged in; going to log in as anonymous");
        setCookie("anonymous", "true", 10000);
        currentUser = null;
        firebase.auth().signInAnonymously().catch(function (error) {
            console.error("Anonymous authentication failed:", error);
        });
    }
});

function joinParamRoom() {
    var userAction = {
        action: "join",
        id: "" + getQueryVariable("r"),
        uid: uid,
        name: getCookie("username"),
        anonymous: anonymous
    };
    isOwner = false;
    socket.send(JSON.stringify(userAction));
}

function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return null;
}

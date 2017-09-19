var roomCreated = false;
var currentUser;

firebase.auth().onAuthStateChanged(function (authData) {
    if (authData) {
        console.log("Logged in as:", authData.uid);
        setCookie("loginData", authData.uid, 10000);
        currentUser = authData.currentUser;
        uid = authData.uid;
        anonymous = authData.isAnonymous;
        if (!roomCreated) {
            roomCreated = true;
            var url = "" + window.location;
            if (url.indexOf("?") === -1) {
                if (socket.readyState === socket.OPEN) {
                    createRoom();
                } else {
                    socket.onopen = createRoom;
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
    alert('Query Variable ' + variable + ' not found');
}

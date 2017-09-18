var uid;
var roomCreated = false;
var anonymous;

function register() {
    var provider = new firebase.auth.GoogleAuthProvider();
    firebase.auth().signInWithPopup(provider).then(function (result) {
        // This gives you a Google Access Token. You can use it to access the Google API.
        var token = result.credential.accessToken;
        // The signed-in user info.
        var user = result.user;
        console.log(user);
        // ...
    }).catch(function (error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // The email of the user's account used.
        var email = error.email;
        // The firebase.auth.AuthCredential type that was used.
        var credential = error.credential;
        console.log(errorCode);
        // ...
    });
}

function signout() {
    firebase.auth().signOut().then(function () {

    }).catch(function (error) {// Sign-out successful.
        // An error happened.
    });
}

var currentUser;
firebase.auth().onAuthStateChanged(function (authData) {
    if (authData) {
        console.log("Logged in as:", authData.uid);
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
        anonymous: anonymous,
        name: getCookie("username")
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

function updateAuthButtons(user) {
    console.log(user.isAnonymous);
    if (user && !user.isAnonymous) {
        document.getElementById("register-row").style.display = 'none';
        document.getElementById("signout-row").style.display = '';
        var name = user.displayName;
        console.log(name);
        document.getElementById("welcome-msg").textContent = "Welcome " + name + "!";
    } else {
        document.getElementById("register-row").style.display = '';
        document.getElementById("signout-row").style.display = 'none';
    }
}
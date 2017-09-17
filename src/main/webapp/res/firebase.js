var uid;
var roomCreated = false;

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

function loginAnonimously() {
    firebase.auth().signInAnonymously().catch(function (error) {
        // Handle Errors here.
        var errorCode = error.code;
        var errorMessage = error.message;
        // ...
    });
}

firebase.auth().onAuthStateChanged(function (user) {
    if (user) {
        // User is signed in.
        var isAnonymous = user.isAnonymous;
        uid = user.uid;
        console.log(isAnonymous);
        console.log(uid);
        if (!isAnonymous) {
            updateAuthButtons(user);
        }
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
            var userAction = {
                action: "uid",
                value: uid
            };
            socket.send(JSON.stringify(userAction));
        }
        // ...
    } else {
        updateAuthButtons(user)
    }
});

function joinParamRoom() {
    var userAction = {
        action: "join",
        id: "" + getQueryVariable("r"),
        uid: uid,
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
    if (user && !user.isAnonymous) {
        console.log("signed in");
        document.getElementById("register-row").style.display = 'none';
        document.getElementById("signout-row").style.display = '';
        var name = user.displayName;
        console.log(name);
        document.getElementById("welcome-msg").textContent = "Welcome " + name + "!";
    } else {
        console.log("not signed in");
        console.log("login in anonymously");
        if (!user) {
            loginAnonimously();
        }
        document.getElementById("register-row").style.display = '';
        document.getElementById("signout-row").style.display = 'none';
    }
}
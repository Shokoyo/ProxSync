var uid;
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
    console.log("signout");
    setCookie("anonymous", "true", 10000);
    setCookie("loginData", "", 10000);
    firebase.auth().signOut().then(function () {
    }).catch(function (error) {// Sign-out successful.
        // An error happened.
    });
}

function updateAuthButtons(user) {
    console.log(user.isAnonymous);
    if (user && !user.isAnonymous) {
        document.getElementById("register-row").style.display = 'none';
        document.getElementById("signout-row").style.display = '';
        var name = user.displayName;
        console.log(name);
        document.getElementById("welcome-msg").textContent = "Welcome " + name + "!";
        var nameField = document.getElementById("user-name");
        if(nameField !== null) {
            nameField.innerHTML = name;
        }
    } else {
        document.getElementById("register-row").style.display = '';
        document.getElementById("signout-row").style.display = 'none';
    }
}

function setCookie(cname, cvalue, exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
    var expires = "expires=" + d.toUTCString();
    document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
    var name = cname + "=";
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}
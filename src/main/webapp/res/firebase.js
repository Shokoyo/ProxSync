function register() {
    var provider = new firebase.auth.GoogleAuthProvider();
    firebase.auth().signInWithPopup(provider).then(function(result) {
        // This gives you a Google Access Token. You can use it to access the Google API.
        var token = result.credential.accessToken;
        // The signed-in user info.
        var user = result.user;
        console.log(user);
        // ...
    }).catch(function(error) {
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
    firebase.auth().signOut().then(function() {
        // Sign-out successful.
    }).catch(function(error) {
        // An error happened.
    });
}

function updateAuthButtons() {
    firebase.auth().onAuthStateChanged(function(user) {
        if (user) {
            console.log("signed in");
            document.getElementById("register-row").style.display='none';
            document.getElementById("signout-row").style.display='';
            name = user.displayName;
            console.log(name);
            document.getElementById("welcome-msg").textContent = "Welcome " + name + "!";
        } else {
            console.log("not signed in");
            document.getElementById("register-row").style.display='';
            document.getElementById("signout-row").style.display='none';
        }
    });
}
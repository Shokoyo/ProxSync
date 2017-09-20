firebase.auth().onAuthStateChanged(function (authData) {
    if (authData) {
        console.log("Logged in as:", authData.uid);
        setCookie("loginData", authData.uid, 10000);
        currentUser = authData.currentUser;
        document.getElementById("user-name").innerHTML = "" + authData.displayName;
        uid = authData.uid;
        anonymous = authData.isAnonymous;
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
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
        updateAuthButtons(authData);
    }
    else {
        console.log("Not logged in; going to log in as anonymous");
        currentUser = null;
        setCookie("anonymous", "true", 10000);
        firebase.auth().signInAnonymously().catch(function (error) {
            console.error("Anonymous authentication failed:", error);
        });
    }
});
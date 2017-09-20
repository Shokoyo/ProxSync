var name;

firebase.auth().onAuthStateChanged(function (authData) {
    if (authData) {
        console.log("Logged in as:", authData.uid);
        if(getCookie("loginData") !== authData.uid && !authData.isAnonymous) {
            setCookie("loginData", authData.uid, 10000);
            location.reload();
        }
        currentUser = authData.currentUser;
        uid = authData.uid;
        anonymous = authData.isAnonymous;
        updateAuthButtons(authData);
    }
    else {
        console.log("Not logged in; going to log in as anonymous");
        currentUser = null;
        setCookie("loginData", "", 10000);
        firebase.auth().signInAnonymously().catch(function (error) {
            console.error("Anonymous authentication failed:", error);
        });
    }
});
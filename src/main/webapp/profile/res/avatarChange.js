// Create a root reference
var ref = firebase.storage().ref();

function uploadAvatar(file) {
    console.log("upload avatar");
    ref.child('images/avatar-' + uid + '.jpg').put(file).then(function (snapshot) {
        var avatarRef = firebase.storage().refFromURL('gs://proxsync.appspot.com/images/avatar-' + uid + '.jpg');
        avatarRef.getDownloadURL().then(function (url) {
            db.collection("users").doc(uid).set({
                avatar: url
            }, { merge: true });
            console.log(url);
        }).catch(function (error) {
            switch (error.code) {
                case 'storage/object_not_found':
                    // File doesn't exist
                    break;

                case 'storage/unauthorized':
                    // User doesn't have permission to access the object
                    break;

                case 'storage/canceled':
                    // User canceled the upload
                    break;
                case 'storage/unknown':
                    // Unknown error occurred, inspect the server response
                    break;
            }
        });
    });
}

function uploadBanner(file) {
    console.log("upload banner");
    ref.child('images/banner-' + uid + '.jpg').put(file).then(function (snapshot) {
        var avatarRef = firebase.storage().refFromURL('gs://proxsync.appspot.com/images/banner-' + uid + '.jpg');
        avatarRef.getDownloadURL().then(function (url) {
            var userRef = db.collection("users").doc(uid);
            userRef.set({
                banner: url
            }, { merge: true });
            console.log(url);
        }).catch(function (error) {
            switch (error.code) {
                case 'storage/object_not_found':
                    // File doesn't exist
                    break;

                case 'storage/unauthorized':
                    // User doesn't have permission to access the object
                    break;

                case 'storage/canceled':
                    // User canceled the upload
                    break;
                case 'storage/unknown':
                    // Unknown error occurred, inspect the server response
                    break;
            }
        });
    });
}
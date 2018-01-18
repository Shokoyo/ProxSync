function removeFromWatchlist(key) {
    /*var oldKey = key;
    key = key.split(".").join("-");
    console.log(key);
    console.log("/users/" + uid + "/watchlist/" + key);
    if(key !== "") {
        firebase.database().ref("/users/" + uid + "/watchlist/" + key).remove();
        firebase.database().ref("/users/" + uid + "/notifications/" + key).remove();
        firebase.database().ref("/watching/" + key + "/" + uid).remove();
        document.getElementById("card-" + oldKey).style.display = 'none';
    } else {
        console.log("corrupted watchlist entry");
    }*/
}

function addToFavorites(key) {
    /*var favoritesRef = firebase.database().ref("/users/" + uid + "/favorites");
    var oldKey = key;
    key = key.split(".").join("-");
    favoritesRef.once('value', function(snapshot) {
        var favorites = snapshot.val();
        if(favorites === null) {
            favorites = {
            };
        }
        favorites[key] = "true";
        favoritesRef.set(favorites);
        favoritesRef.once('value', function(snapshot) {
            var favEl = document.getElementById("favorites-" + oldKey);
            favEl.onclick=function() {
                removeFromFavorites(oldKey);
                return false;
            };
            favEl.innerHTML = "favorite";
        });
    });*/
}

function removeFromFavorites(key) {
    /*var oldKey = key;
    key = key.split(".").join("-");
    if(key !== "") {
        var favRef = firebase.database().ref("/users/" + uid + "/favorites/" + key);
        favRef.remove();
        favRef.once('value', function(snapshot) {
            var favEl = document.getElementById("favorites-" + oldKey);
            favEl.onclick=function() {
                addToFavorites(oldKey);
                return false;
            };
            favEl.innerHTML = "favorite_border";
        });
    } else {
        console.log("corrupted favorite entry");
    }*/
}
function removeFromWatchlist(key) {
    var oldKey = key;
    key = key.split(".").join("-");
    console.log(key);
    console.log("/users/" + uid + "/watchlist/" + key);
    if(key !== "") {
        firebase.database().ref("/users/" + uid + "/watchlist/" + key).remove();
        firebase.database().ref("/notifications/" + uid + "/" + key).remove();
        document.getElementById("card-" + oldKey).style.display = 'none';
    } else {
        console.log("corrupted watchlist entry");
    }
}

function addToFavorites(key) {

}
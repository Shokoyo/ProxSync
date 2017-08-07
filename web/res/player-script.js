/**
 * Created by Jeremias on 06.08.2017.
 */

var socket = new WebSocket("ws://localhost:8080/ProxSync_war_exploded/actions");
socket.onmessage = onMessage;

//add onload function
if (window.attachEvent) {
    window.attachEvent('onload', hideVideoURL);
} else {
    if (window.onload) {
        var curronload = window.onload;
        var newonload = function (evt) {
            curronload(evt);
            hideVideoURL();
        };
        window.onload = newonload;
    } else {
        window.onload = hideVideoURL;
    }
}

//hide video url and text field (when not connected to a room)
function hideVideoURL() {
    document.getElementById("url").style.display = 'none';
    document.getElementById("url-button").style.display = 'none';
}

var myPlayer = videojs('my-player');
myPlayer.ready(function () {
    myPlayer.src("http://vjs.zencdn.net/v/oceans.mp4");
    myPlayer.volume(0.1);
    myPlayer.play();
});

function createRoom() {

    document.getElementById("url").style.display = '';
    document.getElementById("url-button").style.display = '';
}

function joinRoom() {
    var id = document.getElementById("room-id-in").value;
    var UserAction = {
        action: "join",
        id: id
    };
    socket.send(JSON.stringify(UserAction));
    document.getElementById("url").style.display = '';
    document.getElementById("url-button").style.display = '';
}

function loadVideo() {
    var url = document.getElementById("url").value;
    document.getElementById("room-id-out").innerHTML = url;
}

function onMessage(event) {

}
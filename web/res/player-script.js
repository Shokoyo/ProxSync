/**
 * Created by Jeremias on 06.08.2017.
 */

var loc = window.location, new_uri;
if (loc.protocol === "https:") {
    new_uri = "wss:";
} else {
    new_uri = "ws:";
}
new_uri += "//" + loc.host;
new_uri += loc.pathname + "actions";
var socket = new WebSocket(new_uri);
socket.onmessage = onMessage;
var isOwner = true;
var roomJoined = false;
var syncing = false;

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

var myPlayer = videojs('my-player');
myPlayer.on('stalled', handleStopEvent);
myPlayer.on('pause', handleStopEvent);
myPlayer.on('play', handlePlayEvent);
//hide video url and text field (when not connected to a room)
function hideVideoURL() {
    document.getElementById("url").style.display = 'none';
    document.getElementById("url-button").style.display = 'none';
}

function hidePlayButtons() {
    myPlayer.removeChild('BigPlayButton');
    var children = myPlayer.children();
    children[5].removeChild('PlayToggle');
}

myPlayer.ready(function () {
    myPlayer.src("http://vjs.zencdn.net/v/oceans.mp4");
    myPlayer.volume(0.1);
});

function createRoom() {
    if (!roomJoined) {
        isOwner = true;
        roomJoined = true;
        var userAction = {
            action: "create"
        };
        socket.send(JSON.stringify(userAction));
        document.getElementById("url").style.display = '';
        document.getElementById("url-button").style.display = '';
    }
}
function joinRoom() {
    if (!roomJoined) {
        isOwner = false;
        var id = document.getElementById("room-id-in").value;
        var userAction = {
            action: "join",
            id: id
        };
        socket.send(JSON.stringify(userAction));
        hidePlayButtons();
    }
}

function leaveRoom() {

}

function loadVideo() {
    var url = document.getElementById("url").value;
    var userAction = {
        action: "video",
        url: url
    };
    socket.send(JSON.stringify(userAction));
}

function onMessage(event) {
    var eventJSON = JSON.parse(event.data);
    if (eventJSON.action === "pause") {
        myPlayer.pause();
        myPlayer.currentTime(eventJSON.current);
        sendBufferedInd();
        syncing = false;
    }
    if (eventJSON.action === "play") {
        if (!syncing) {
            myPlayer.play();
            syncing = true;
        }
    }
    if (eventJSON.action === "jump") {
        myPlayer.currentTime(eventJSON.time);
    }
    if (eventJSON.action === "bufferedRequest") {
        sendBufferedInd();
    }
    if (eventJSON.action === "video") {
        myPlayer.src(eventJSON.url);
    }
    if (eventJSON.action === "roomID") {
        if(eventJSON.id === "-1") {
            document.getElementById("room-id-out").innerHTML = "Room ID: invalid";
            roomJoined = false;
        } else {
            document.getElementById("room-id-out").innerHTML = "Room ID: " + eventJSON.id;
            roomJoined = true;
        }
    }
}

function sendBufferedInd() {
    var userAction = {
        action: "bufferedIndication",
        current: myPlayer.currentTime(),
        buffered: myPlayer.bufferedEnd(),
        percentage: myPlayer.bufferedPercent()
    };
    socket.send(JSON.stringify(userAction));
}

function handlePlayEvent() {
    if (isOwner && !syncing) {
        var userAction = {
            action: "play"
        };
        socket.send(JSON.stringify(userAction));
        myPlayer.pause();
    }
}

function handleStopEvent() {
    if (syncing) {
        var userAction = {
            action: "stopped",
            current: myPlayer.currentTime(),
            buffered: myPlayer.bufferedEnd()
        };
        socket.send(JSON.stringify(userAction));
        syncing = false;
    }
}
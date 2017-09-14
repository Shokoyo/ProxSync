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
var startTime;
var roomId;

//add onload function
if (window.attachEvent) {
    window.attachEvent('onload', onloadFunction);
} else {
    if (window.onload) {
        var curronload = window.onload;
        var newonload = function (evt) {
            curronload(evt);
            onloadFunction();
        };
        window.onload = newonload;
    } else {
        window.onload = onloadFunction;
    }
}

var myPlayer = videojs('my-player');
myPlayer.on('stalled', handleStopEvent);
myPlayer.on('pause', handleStopEvent);
myPlayer.on('play', handlePlayEvent);


myPlayer.ready(function () {
    var myPlayer = this;
    var aspectRatio = 9 / 16;
    var maxWidth = 1280;
    var parent = document.getElementById(myPlayer.id()).parentElement;
    function resizeVideoJS() {
        var width = parent.getBoundingClientRect().width - 30;
        if (width > maxWidth) {
            width = maxWidth;
        }
        myPlayer.width(width);
        myPlayer.height(width * aspectRatio);
        document.getElementById("row-player").style.height = width*aspectRatio;
    }

    // Initialize resizeVideoJS()
    resizeVideoJS();
    // Then on resize call resizeVideoJS()
    window.onresize = resizeVideoJS;
});


//hide video url and text field (when not connected to a room)
function onloadFunction() {
    document.getElementById("url").style.display = 'none';
    document.getElementById("url-button").style.display = 'none';
    document.getElementById("intro-button").style.display = 'none';
    document.getElementById("invite-link").style.display = 'none';
    document.getElementById("invite-button").style.display = 'none';
    document.getElementById("leave-button").style.display = 'none';
    checkCookie();
}

function hidePlayButtons() {
    myPlayer.removeChild('BigPlayButton');
    var children = myPlayer.children();
    children[5].removeChild('PlayToggle');
}

myPlayer.ready(function () {
    myPlayer.volume(0.1);
});

function hideElement(elementId) {
    document.getElementById(elementId).style.disPlay = 'none';
}

function showElement(elementId) {
    document.getElementById(elementId).style.display = '';
}

function createRoom() {
    myPlayer.on('timeupdate', sendCurrentTime);
    if (!roomJoined) {
        isOwner = true;
        roomJoined = true;
        var userAction = {
            action: "create",
            name: getCookie("username")
        };
        socket.send(JSON.stringify(userAction));
        document.getElementById("url").style.display = '';
        document.getElementById("url-button").style.display = '';
        document.getElementById("intro-button").style.display = '';
    }
}

function joinRoom() {
    if (!roomJoined) {
        document.getElementById("debug-out").innerHTML = "";
        isOwner = false;
        var id = document.getElementById("room-id-in").value;
        var userAction = {
            action: "join",
            name: getCookie("username"),
            id: id
        };
        socket.send(JSON.stringify(userAction));
    }
}

function leaveRoom() {
    window.location = window.location.pathname;
}

$("#room-id-in").keypress(function (event) {
    if (event.which === 13) {
        joinRoom();
    }
});

$("#url").keypress(function (event) {
    if (event.which === 13) {
        loadVideo();
    }
});

$("#name").keypress(function (event) {
    if (event.which === 13) {
        changeName();
    }
});

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
    if (eventJSON.action === "resync") {
        myPlayer.pause();
        syncing = false;
        var userAction = {
            action: "resync",
            current: myPlayer.currentTime(),
            buffered: myPlayer.bufferedEnd()
        };
        socket.send(JSON.stringify(userAction));
    }
    if (eventJSON.action === "stop") {
        myPlayer.pause();
        syncing = false;
        sendBufferedInd();
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
        var SourceString = eventJSON.url;
        var SourceObject;
        startTime = eventJSON.current;
        if (SourceString.indexOf(".mp4") !== -1) {
            SourceObject = {src: SourceString, type: 'video/mp4'}
        } else {
            SourceObject = {src: SourceString, type: 'video/webm'}
        }
        myPlayer.src(SourceObject);
        myPlayer.pause();
        myPlayer.one('canplay', setStartTime);
        //syncing = true;
        //setTimeout(myPlayer.play,20);
        //setTimeout(function() { syncing = false; }, 20);
        //setTimeout(myPlayer.pause,20);
    }
    if (eventJSON.action === "roomID") {
        if (eventJSON.id === "-1") {
            roomId = -1;
            document.getElementById("debug").innerHTML = "invalid Room ID";
            roomJoined = false;
        } else {
            if (!isOwner) {
                hidePlayButtons();
            }
            document.getElementById("room-join-button").style.display = 'none';
            document.getElementById("room-id-in").style.display = 'none';
            document.getElementById("create-button").style.display = 'none';
            document.getElementById("leave-button").style.display = '';
            roomId = eventJSON.id;
            document.getElementById("invite-button").style.display = '';
            document.getElementById("invite-link").innerHTML = "http://" + loc.host + loc.pathname + "?r=" + roomId;
            roomJoined = true;
        }
    }
    if (eventJSON.action === "debug") {
        document.getElementById("debug-out").innerHTML = eventJSON.message;
    }
    if (eventJSON.action === "room-list") {
        document.getElementById("room-list").innerHTML = "<h4>User List (ID: " + roomId + ")</h4><div class=\"pre-scrollable\">" + eventJSON.roomString + "</div>";
    }
}

function skipIntro() {
    var currentTime = myPlayer.currentTime();
    myPlayer.currentTime(currentTime + 80);
    myPlayer.pause();
    setTimeout(handlePlayEvent, 500);
    setTimeout(handlePlayEvent, 1000);
}

function changeName() {
    var userAction = {
        action: "changeName",
        name: document.getElementById("name").value
    };
    setCookie("username", userAction.name, 365);
    socket.send(JSON.stringify(userAction));
}

function sendBufferedInd() {
    var userAction = {
        action: "bufferedIndication",
        readyState: myPlayer.readyState()
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
        var buffered = myPlayer.readyState();
        var intended = (buffered === 4);
        var userAction = {
            action: "stopped",
            current: myPlayer.currentTime(),
            intended: intended,
            buffered: myPlayer.bufferedEnd()
        };
        socket.send(JSON.stringify(userAction));
        syncing = false;
    }
}

function copyToClipboard(element) {
    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val($(element).text()).select();
    document.execCommand("copy");
    $temp.remove();
}

function sendCurrentTime() {
    var userAction = {
        action: "current",
        current: myPlayer.currentTime()
    };
    socket.send(JSON.stringify(userAction));
}

function setStartTime() {
    myPlayer.currentTime(startTime);
    myPlayer.pause();
    myPlayer.on('canplaythrough', sendBufferedInd);
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

function checkCookie() {
    var username = getCookie("username");
    if (username !== "") {
        document.getElementById("name").value = username;
    } else {
        username = prompt("Enter your name:", "");
        if (username !== "" && username !== null) {
            setCookie("username", username, 365);
        }
    }
}
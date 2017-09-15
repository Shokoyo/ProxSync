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
var lastName;
var roomDialog;
var cookieDialog;
var skipButton;
var firstVideo = true;
var timeUpdate = false;
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
myPlayer.on('volumechange', function () {
    setCookie("volume", myPlayer.volume(), 365);
});

function initCheckbox() {
    if(document.getElementById("auto-next-checkbox").checked === false) {
        document.getElementById("auto-next-checkbox").click();
    }
    $('#auto-next-checkbox').click(function () {
        console.log(document.getElementById("auto-next-checkbox").checked);
        var userAction = {
            "action": "autoNext",
            "value": document.getElementById("auto-next-checkbox").checked
        };
        socket.send(JSON.stringify(userAction));
    });
    if(document.getElementById("auto-play-checkbox").checked === false) {
        document.getElementById("auto-play-checkbox").click();
    }
}

myPlayer.ready(function () {

    var myPlayer = this;
    var aspectRatio = 9 / 16;
    var maxWidth = 1280;
    var parent = document.getElementById(myPlayer.id()).parentElement;

    function resizeVideoJS() {
        var width = parent.getBoundingClientRect().width;
        if (width > maxWidth) {
            width = maxWidth;
        }
        myPlayer.width(width);
        myPlayer.height(width * aspectRatio);
    }

    var y = document.getElementsByClassName("vjs-big-play-button");
    y[0].setAttribute("tabIndex", "-1");
    var x = document.getElementsByClassName("vjs-control");
    var i;
    for (i = 0; i < x.length; i++) {
        x[i].setAttribute("tabIndex", "-1");
    }

    // Initialize resizeVideoJS()
    resizeVideoJS();
    // Then on resize call resizeVideoJS()
    window.onresize = resizeVideoJS;
    var volume = getCookie("volume");
    if (volume === "") {
        volume = 1;
    }
    myPlayer.volume(volume);
});

function disableSeeking() {
    document.getElementsByClassName("vjs-progress-control")[0].style.pointerEvents = 'none';
}

function enableSeeking() {
    document.getElementsByClassName("vjs-progress-control")[0].style.pointerEvents = '';
}

$("#name").blur(function () {
    lastName = document.getElementById("name").value;
    document.getElementById("name").value = getCookie("username");
});
//hide video url and text field (when not connected to a room)
function onloadFunction() {
    initCheckbox();
    cookieDialog = new mdc.dialog.MDCDialog(document.querySelector('#cookie-dialog'));
    document.getElementById("url-field").style.display = 'none';
    document.getElementById("url-button").style.display = 'none';
    document.getElementById("intro-button").style.display = 'none';
    document.getElementById("invite-link").style.display = 'none';
    document.getElementById("invite-button").style.display = 'none';
    document.getElementById("leave-button").style.display = 'none';
    document.getElementById("auto-next-container").style.display = 'none';
    document.getElementById("auto-play-container").style.display = 'none';
    checkCookie();
}

$(document).on('keydown', function (e) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 27) {
        if (roomDialog != null && roomDialog.open) {
            e.stopImmediatePropagation();
        }
        if (cookieDialog != null && cookieDialog.open) {
            e.stopImmediatePropagation();
        }
    }
});

document.addEventListener("click", handler, true);

function handler(e) {
    if (roomDialog != null && roomDialog.open && !(e.target.id === "room-id-in" || e.target.id === "create-button-dialog" || e.target.id === "join-button-dialog")) {
        e.stopPropagation();
        e.preventDefault();
    }
    if (cookieDialog != null && cookieDialog.open && !(e.target.id === "cookie-field" || e.target.id === "cookie-button")) {
        e.stopPropagation();
        e.preventDefault();
    }
}

function makeRoomDialog() {
    roomDialog = new mdc.dialog.MDCDialog(document.querySelector('#room-dialog'));
    roomDialog.show();
}

function hidePlayButtons() {
    myPlayer.removeChild('BigPlayButton');
    var children = myPlayer.children();
    children[5].removeChild('PlayToggle');
}

function showSpecialControl() {
    addSkipButton();
}

function addSkipButton() {
    var videoJsButtonClass = videojs.getComponent('Button');
    var concreteButtonClass = videojs.extend(videoJsButtonClass, {

        // The `init()` method will also work for constructor logic here, but it is
        // deprecated. If you provide an `init()` method, it will override the
        // `constructor()` method!
        constructor: function () {
            videoJsButtonClass.call(this, myPlayer);
        }, // notice the comma
        handleClick: skipIntro
    });

    skipButton = myPlayer.controlBar.addChild(new concreteButtonClass(), {}, myPlayer.controlBar.children().length - 2);
    skipButton.addClass("vjs-skip-button");
    var buttonDOM = document.getElementsByClassName("vjs-skip-button");
    buttonDOM[0].setAttribute("title", "Skip OP/ED");
    var loc = location.protocol + '//' + location.host + location.pathname;
    buttonDOM[0].firstChild.innerHTML = "<img src=\"" + loc + "res/skip.png\" height='14' align='middle'></img>";
}

function showPlayButtons() {
    myPlayer.addChild('BigPlayButton', {}, 3);
    myPlayer.getChild('ControlBar').addChild('PlayToggle', {}, 0);
    var playButton = myPlayer.getChild('ControlBar').getChild('PlayToggle');
    if (!myPlayer.paused()) {
        playButton.toggleClass("vjs-playing");
        playButton.toggleClass("vjs-paused");
    }
}

function hideElement(elementId) {
    document.getElementById(elementId).style.disPlay = 'none';
}

function showElement(elementId) {
    document.getElementById(elementId).style.display = '';
}

function createRoom() {
    bindTimeUpdate();
    if (!roomJoined) {
        isOwner = true;
        roomJoined = true;
        var userAction = {
            action: "create",
            roomName: "" + document.getElementById("room-id-in").value,
            name: getCookie("username")
        };
        socket.send(JSON.stringify(userAction));
    }
}

function bindTimeUpdate() {
    timeUpdate = true;
}

function unbindTimeUpdate() {
    timeUpdate = false;
}

function joinRoom() {
    if (!roomJoined) {
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

$("#url").keypress(function (event) {
    if (event.which === 13) {
        loadVideo();
    }
});

$("#cookie-field").keypress(function (event) {
    if (event.which === 13) {
        submitCookie();
    }
});

$("#name").keypress(function (event) {
    if (event.which === 13) {
        lastName = document.getElementById("name").value;
        changeName();
        document.getElementById("name").blur();
    }
});

$("#room-id-in").keypress(function (event) {
    if (event.which === 13) {
        createRoom();
    }
});

function loadVideo() {
    if (myPlayer.src() !== undefined) {
        myPlayer.reset();
        sendCurrentTime();
    }
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
    if (eventJSON.action === "owner") {
        enableSeeking();
        isOwner = true;
        showPlayButtons();
        showSpecialControl();
        document.getElementById("url-field").style.display = '';
        document.getElementById("url-button").style.display = '';
        document.getElementById("intro-button").style.display = '';
    }
    if (eventJSON.action === "jump") {
        myPlayer.currentTime(eventJSON.time);
    }
    if (eventJSON.action === "bufferedRequest") {
        sendBufferedInd();
    }
    if (eventJSON.action === "debug") {
        console.log(eventJSON.message);
    }
    if (eventJSON.action === "video") {
        var SourceString = eventJSON.url;
        console.log("URL: " + SourceString);
        var SourceObject;
        startTime = eventJSON.current;
        if (SourceString.indexOf(".mp4") !== -1) {
            SourceObject = {src: SourceString, type: 'video/mp4'}
        } else {
            SourceObject = {src: SourceString, type: 'video/webm'}
        }
        myPlayer.reset();
        myPlayer.src(SourceObject);
        myPlayer.pause();
        bindTimeUpdate();
        bindPauseEvent();
        if(!firstVideo && document.getElementById("auto-play-checkbox").checked) {
            myPlayer.one('canplay', function() {
                setStartTime();
                setTimeout(function() {
                myPlayer.play();
            }, 1000)});
        } else {
            myPlayer.one('canplay', setStartTime);
        }
        myPlayer.one('ended', function () {
            unbindPauseEvent();
            if (isOwner) {
                unbindTimeUpdate();
                var userAction = {
                    "action": "finished"
                };
                socket.send(JSON.stringify(userAction));
            }
        });
        firstVideo = false;
        //syncing = true;
        //setTimeout(myPlayer.play,20);
        //setTimeout(function() { syncing = false; }, 20);
        //setTimeout(myPlayer.pause,20);
    }

    function bindPauseEvent() {
        myPlayer.on('pause', handleStopEvent);
        myPlayer.on('stalled', handleStopEvent);
    }

    function unbindPauseEvent() {
        myPlayer.off('pause');
        myPlayer.off('stalled');
    }

    if (eventJSON.action === "roomID") {
        if (eventJSON.id === "-1") {
            if (roomDialog == null) {
                makeRoomDialog();
            } else {
                roomDialog.show();
            }
            document.getElementById("room-id-in").focus();
            document.getElementById("room-id-in").value = "invalid";
            document.getElementById("room-id-in").blur();
            roomId = -1;
            roomJoined = false;
            isOwner = false;
        } else if (eventJSON.id === "-2") {
            if (roomDialog == null) {
                makeRoomDialog();
            } else {
                roomDialog.show();
            }
            document.getElementById("room-id-in").focus();
            document.getElementById("room-id-in").value = "already taken";
            document.getElementById("room-id-in").blur();
            roomId = -2;
            roomJoined = false;
            isOwner = false;
        } else {
            if (roomDialog != null) {
                roomDialog.close();
            }
            if (!isOwner) {
                disableSeeking();
                hidePlayButtons();
            } else {
                var userAction = {
                    "action": "autoNext",
                    "value": document.getElementById("auto-next-checkbox").checked
                };
                socket.send(JSON.stringify(userAction));
                document.getElementById("url-field").style.display = '';
                document.getElementById("url-button").style.display = '';
                document.getElementById("intro-button").style.display = '';
                document.getElementById("auto-next-container").style.display = '';
                document.getElementById("auto-play-container").style.display = '';
                showSpecialControl();
            }
            document.getElementById("room-id-in").style.display = 'none';
            document.getElementById("leave-button").style.display = '';
            roomId = eventJSON.id;
            document.getElementById("invite-button").style.display = '';
            document.getElementById("invite-link").innerHTML = "http://" + loc.host + loc.pathname + "?r=" + roomId;
            roomJoined = true;
        }
    }
    if (eventJSON.action === "room-list") {
        var roomString = eventJSON.roomString.replace("res/", location.protocol + '//' + location.host + location.pathname + "res/");
        document.getElementById("room-list").innerHTML = "<h4>User List (ID: " + roomId + ")</h4>" + roomString;
    }
}

function skipIntro() {
    var currentTime = myPlayer.currentTime();
    myPlayer.currentTime(currentTime + 80);
    myPlayer.pause();
    if (currentTime + 83 < myPlayer.duration()) {
        setTimeout(handlePlayEvent, 500);
        setTimeout(handlePlayEvent, 1000);
    }
}

function changeName() {
    if (lastName === "") {
        return;
    }
    var userAction = {
        action: "changeName",
        name: lastName
    };
    document.getElementById("name").value = lastName;
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

function handleSeekEvent() {

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
    if(timeUpdate) {
        var userAction = {
            action: "current",
            current: myPlayer.currentTime()
        };
        socket.send(JSON.stringify(userAction));
    }
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
        document.getElementById("name").focus();
        document.getElementById("name").value = username;
        document.getElementById("name").blur();
        var curUrl = "" + window.location;
        if (curUrl.indexOf("?") === -1) {
            if (!socket.opened_) {
                socket.onopen = makeRoomDialog;
            } else {
                makeRoomDialog();
            }
        }
    } else {
        cookieDialog.show();
        document.getElementById("cookie-field").focus();
    }
}

function submitCookie() {
    var username = document.getElementById("cookie-field").value;
    if (username !== "" && username !== null) {
        setCookie("username", username, 365);
        document.getElementById("name").focus();
        document.getElementById("name").value = username;
        document.getElementById("name").blur();
        cookieDialog.close();
        makeRoomDialog();
    } else {
        document.getElementById("cookie-field").focus();
    }
}
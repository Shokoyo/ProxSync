/**
 * Created by Jeremias on 06.08.2017.
 */


var new_uri = "ws://dieser1memesprech.de/ProxSync/actions";
var socket = new WebSocket(new_uri);
socket.onmessage = onMessage;
var inSearchField = false;
var isOwner = true;
var roomJoined = false;
var syncing = false;
var startTime;
var firstVideo = true;
var timeUpdate = false;
var pauseFlag = true;
var finishedFlag = false;

playerManager.addEventListener(cast.framework.events.EventType.BUFFERING,
    event => {
        console.log(event);
        handleStopEvent();
    });
playerManager.addEventListener(cast.framework.events.EventType.PAUSE,
    event => {console.log(event);
        handleStopEvent();
    });
playerManager.addEventListener(cast.framework.events.EventType.PLAY,
    event => {
        console.log(event);
        handlePlayEvent();
    });
playerManager.addEventListener(cast.framework.events.EventType.ERROR,
    event => {
        console.log(event);
        unbindFinishedEvent();
        playerManager.removeEventListener(cast.framework.events.EventType.CAN_PLAY_THROUGH)
    });

function createRoom() {
    if (!roomJoined) {
        bindTimeUpdate();
        isOwner = true;
        roomJoined = true;
        var userAction = {
            action: "create",
            uid: "chromecast",
            name: "Chromecast",
            anonymous: true
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

function joinRoom(id) {
    if (!roomJoined) {
        isOwner = false;
        var userAction = {
            action: "join",
            uid: "chromecast",
            name: "Chromecast",
            anonymous: true,
            id: id
        };
        socket.send(JSON.stringify(userAction));
    }
}

function onMessage(event) {
    var eventJSON = JSON.parse(event.data);
    if (eventJSON.action === "pause") {
        playerManager.pause();
        playerManager.seek(eventJSON.current);
        sendBufferedInd();
        syncing = false;
    }
    if (eventJSON.action === "resync") {
        playerManager.pause();
        syncing = false;
        var userAction = {
            action: "resync",
            current: playerManager.getCurrentTimeSec(),
            buffered: 600
        };
        socket.send(JSON.stringify(userAction));
    }
    if (eventJSON.action === "stop") {
        playerManager.pause();
        syncing = false;
        sendBufferedInd();
    }
    if (eventJSON.action === "play") {
        if (!syncing) {
            playerManager.play();
            syncing = true;
        }
    }
    if (eventJSON.action === "jump") {
        playerManager.seek(eventJSON.time);
    }
    if (eventJSON.action === "bufferedRequest") {
        sendBufferedInd();
    }
    if (eventJSON.action === "video") {
        var SourceString = eventJSON.url;
        startTime = eventJSON.current;
        var mediaInformation = new cast.framework.messages.MediaInformation();
        mediaInformation.contentType = "video/mp4";
        mediaInformation.contentUrl = SourceString;
        mediaInformation.metadata = new cast.framework.messages.MediaMetadata(cast.framework.messages.MetadataType.GENERIC);
        console.log('swag');
        mediaInformation.streamType = cast.framework.messages.StreamType.NONE;
        var loadRequestData = new cast.framework.messages.LoadRequestData();
        loadRequestData.autoplay = false;
        loadRequestData.currentTime = startTime;
        loadRequestData.media = mediaInformation;
        playerManager.load(loadRequestData);
        playerManager.pause();
        bindTimeUpdate();
        bindPauseEvent();
        playerManager.addEventListener(cast.framework.events.EventType.PAUSE,
            event => {
                console.log(event);
                bindFinishedEvent();
            });

        playerManager.addEventListener(cast.framework.events.EventType.CAN_PLAY_THROUGH,
            event => {
                sendBufferedInd();
            });

        firstVideo = false;
        //syncing = true;
        //setTimeout(playerManager.play,20);
        //setTimeout(function() { syncing = false; }, 20);
        //setTimeout(playerManager.pause,20);
    }
}

function skipIntro() {
    var currentTime = playerManager.getCurrentTimeSec();
    playerManager.seek(currentTime + 80);
    playerManager.pause();
    if (currentTime + 83 < playerManager.getDurationSec()) {
        setTimeout(handlePlayEvent, 500);
        setTimeout(handlePlayEvent, 1000);
    }
}


function bindFinishedEvent() {
    finishedFlag = true;
}

function unbindFinishedEvent() {
    finishedFlag = false;
}

playerManager.addEventListener(cast.framework.events.EventType.TIME_UPDATE,
    event => {
        sendCurrentTime();
    });

playerManager.addEventListener(cast.framework.events.EventType.ENDED,
    event => {
        if (finishedFlag) {
            unbindPauseEvent();
            if (isOwner) {
                unbindTimeUpdate();
                nextEpisode();
            }
            unbindFinishedEvent();
        }
    });

function nextEpisode() {
    var userAction = {
        "action": "finished"
    };
    socket.send(JSON.stringify(userAction));
}

function bindPauseEvent() {
    pauseFlag = true;
}

function unbindPauseEvent() {
    pauseFlag = false;
}

function sendBufferedInd() {
    var readyState = 4;
    if (playerManager.getPlayerState ===  cast.framework.messages.PlayerState.BUFFERING) {
        readyState = 2;
    }
    var userAction = {
        action: "bufferedIndication",
        readyState: readyState
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
        playerManager.pause();
    }
}

function handleStopEvent() {
    if (syncing && pauseFlag) {
        var buffered = 4;
        if (playerManager.getPlayerState ===  cast.framework.messages.PlayerState.BUFFERING) {
            buffered = 2;
        }
        var intended = (buffered === 4 || buffered === 3);
        var userAction = {
            action: "stopped",
            current: playerManager.getCurrentTimeSec(),
            intended: intended,
            buffered: playerManager.getCurrentTimeSec() + 10
        };
        socket.send(JSON.stringify(userAction));
        syncing = false;
    }
}

function sendCurrentTime() {
    if (timeUpdate) {
        var userAction = {
            action: "current",
            current: playerManager.getCurrentTimeSec()
        };
        socket.send(JSON.stringify(userAction));
    }
}

function setStartTime() {
    playerManager.seek(startTime);
    playerManager.pause();
    playerManager.addEventListener(cast.framework.events.EventType.CAN_PLAY_THROUGH,
        event => {
            sendBufferedInd();
            sendBufferedInd();
        });
}
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
var autoplay = true;

var myPlayer = videojs('my-player');
myPlayer.on('stalled', handleStopEvent);
myPlayer.on('pause', handleStopEvent);
myPlayer.on('play', handlePlayEvent);
myPlayer.on('error', function () {
    unbindFinishedEvent();
    myPlayer.off('canplay');
});

function loadSampleVideo() {
    var playerManager = context.getPlayerManager();
    var SourceString = "http://www.sample-videos.com/video/mp4/720/big_buck_bunny_720p_30mb.mp4";
    var mediaInformation = new cast.framework.messages.MediaInformation();
    mediaInformation.contentType = "video/mp4";
    mediaInformation.contentUrl = SourceString;
    mediaInformation.metadata = new cast.framework.messages.MediaMetadata(cast.framework.messages.MetadataType.GENERIC);
    console.log('swag');
    mediaInformation.streamType = cast.framework.messages.StreamType.NONE;
    var loadRequestData = new cast.framework.messages.LoadRequestData();
    loadRequestData.autoplay = false;
    loadRequestData.currentTime = 0;
    loadRequestData.media = mediaInformation;
    playerManager.load(loadRequestData);
    setTimeout(loadSampleVideo, 200);
}

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
            buffered: 600
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
        console.log("URL: " + SourceString);
        if (SourceString === "") {
            return;
        }
        var SourceObject;
        var isYT = false;
        startTime = eventJSON.current;
        SourceObject = {src: SourceString, type: 'video/mp4'};
        myPlayer.reset();
        myPlayer.src(SourceObject);
        myPlayer.pause();
        bindTimeUpdate();
        bindPauseEvent();
        if (!firstVideo && isOwner && autoplay) {
            myPlayer.one('canplay', function () {
                setStartTime();
                bindFinishedEvent();
                setTimeout(function () {
                    myPlayer.play();
                }, 1000);
            });
        } else {
            myPlayer.one('canplay', function () {
                bindFinishedEvent();
                setStartTime();
            });
        }
        firstVideo = false;
        //syncing = true;
        //setTimeout(myPlayer.play,20);
        //setTimeout(function() { syncing = false; }, 20);
        //setTimeout(myPlayer.pause,20);
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

function bindFinishedEvent() {
    finishedFlag = true;
}

function unbindFinishedEvent() {
    finishedFlag = false;
}

myPlayer.on('timeupdate', sendCurrentTime);

myPlayer.on('ended', function () {
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
    if (syncing && pauseFlag) {
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

function sendCurrentTime() {
    if (timeUpdate) {
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
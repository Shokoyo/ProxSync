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
var pauseFlag = true;
var finishedFlag = false;
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
myPlayer.on('error', function () {
    unbindFinishedEvent();
    myPlayer.off('canplay');
});

function initCheckbox() {
    if (document.getElementById("auto-next-checkbox").checked === false) {
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
    if (document.getElementById("auto-play-checkbox").checked === false) {
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
        var marginCellPanelUsers = $('#cell-panel-users').outerHeight(true) - $('#cell-panel-users').outerHeight();
        var marginCellToolbar = $('#cell-toolbar').outerHeight(true) - $('#cell-toolbar').outerHeight();
        document.getElementById("mdc-users-list").style.maxHeight = ((width * aspectRatio) - $('#cell-toolbar').outerHeight(false) - marginCellToolbar - marginCellPanelUsers) + "px";
        document.getElementById("playlist-list-section").style.maxHeight = ((width * aspectRatio) - $('#cell-toolbar').outerHeight(false) - marginCellToolbar - marginCellPanelUsers) + "px";
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
    mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
    cookieDialog = new mdc.dialog.MDCDialog(document.querySelector('#cookie-dialog'));
    document.getElementById("url-field").style.display = 'none';
    document.getElementById("url-button").style.display = 'none';
    document.getElementById("invite-link").style.display = 'none';
    document.getElementById("invite-button").style.display = 'none';
    document.getElementById("auto-next-container").style.display = 'none';
    document.getElementById("auto-play-container").style.display = 'none';
    checkCookie();
    updateAuthButtons();
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

$('#url-button').on('keyup', function (e) {
    if (e.keyCode === 32 || e.which === 32) {
        e.preventDefault();
        e.stopPropagation();
    }
});

$(document).on('keydown', function (e) {
    var nodeName = e.target.nodeName;
    if ('INPUT' == nodeName || 'TEXTAREA' == nodeName || e.target.title === "Play" || e.target.title === 'Pause') {

        return;
    }
    if (e.keyCode === 32 || e.which === 32 && isOwner) {
        if (myPlayer.paused()) {
            myPlayer.play();
        } else {
            myPlayer.pause();
        }
        e.preventDefault();
        e.stopPropagation();
    }
});

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
    if (!roomJoined) {
        bindTimeUpdate();
        isOwner = true;
        roomJoined = true;
        var userAction = {
            action: "create",
            uid: uid,
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
            uid: uid,
            id: id
        };
        socket.send(JSON.stringify(userAction));
    }
}

function searchRequest(keyword, old) {
    console.log(keyword === old);
    if (keyword === old && keyword != "") {
        var userAction = {
            action: "search",
            keyword: keyword
        };
        socket.send(JSON.stringify(userAction));
    } else {
        $('#mdc-search-list').addClass("hidden");
    }
}

$('#tf-box-search-field').on('input', function () {
    var old = document.getElementById("tf-box-search-field").value;
    setTimeout(function () {
        searchRequest(document.getElementById("tf-box-search-field").value, old);
    }, 400);
});

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

$("#room-id-in").keypress(function (event) {
    if (event.which === 13) {
        createRoom();
    }
});

$("#user-name").keypress(function (event) {
    if (event.which === 13) {
        console.log("test");
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
    if (eventJSON.action === "owner") {
        enableSeeking();
        isOwner = true;
        showPlayButtons();
        showSpecialControl();
        document.getElementById("url-field").style.display = '';
        document.getElementById("url-button").style.display = '';
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
        if (url === "") {
            return;
        }
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
        if (!firstVideo && document.getElementById("auto-play-checkbox").checked) {
            myPlayer.one('canplay', function () {
                setStartTime();
                bindFinishedEvent();
                setTimeout(function () {
                    myPlayer.play();
                }, 1000)
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

    if (eventJSON.action === "animeInfo") {
        document.getElementById("anime-title").innerHTML = "" + eventJSON.title + ", Episode: " + eventJSON.episode
            + "/" + eventJSON.episodeCount;
    }

    if (eventJSON.action === "roomID") {
        if (eventJSON.id === "-1") {
            leaveRoom();
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
                document.getElementById("auto-next-container").style.display = '';
                document.getElementById("auto-play-container").style.display = '';
                showSpecialControl();
            }
            document.getElementById("room-id-in").style.display = 'none';
            roomId = eventJSON.id;
            document.getElementById("room-id-out").innerHTML = "Room ID: " + roomId;
            document.getElementById("invite-button").style.display = '';
            document.getElementById("invite-link").innerHTML = "http://" + loc.host + loc.pathname + "?r=" + roomId;
            roomJoined = true;
        }
    }
    if (eventJSON.action === "playlist") {
        var playListString = buildHtmlPlaylist(eventJSON.playlist);
        document.getElementById("playlist-list").innerHTML = playListString;
    }
    if (eventJSON.action === "room-list") {
        var roomString = buildHtmlList(eventJSON.userList);
        document.getElementById("user-list").innerHTML = "" + roomString;
    }
    if (eventJSON.action === "search-result") {
        var resultSearchString = buildHtmlListSearch(eventJSON.result);
        document.getElementById("mdc-search-list").innerHTML = resultSearchString;
    }
}

function editName() {
    document.getElementById("user-self").style.display = "none";
    document.getElementById("user-self-field").style.display = "block";
    document.getElementById("name").focus();
}

$(document).on("keypress", "#name", function (e) {
    if (e.keyCode == 13 || e.which == '13') {
        lastName = document.getElementById("name").value;
        changeName();
        document.getElementById("name").blur();
    }
});

function addSearchResultToPlaylist(url) {
    var userAction = {
        action: "video",
        url: url
    };
    socket.send(JSON.stringify(userAction));
}

$(document).on('click', function (e) {
    if ($(e.target).closest("#mdc-search-list").length === 0) {
        $("#mdc-search-list").addClass("hidden");
    }
});

function buildHtmlListSearch(resultList) {
    var res = "";
    for (var i = 0; i < resultList.length; i++) {
        res = res + "<a href='#' onclick='addSearchResultToPlaylist(\"" + resultList[i].link + "\");' class=\"mdc-list-item\" style='height:96px;'>" +
            "<img style='height:78px;padding-right:14px;' src='" + resultList[i].image + "' role='presentation'></img>";
        res += "<span class='mdc-list-item__text'>" + resultList[i].title + "";
        res += "<span class='mdc-list-item__text__secondary'>" + "1 2 3 4 5 6 7 8 9 10 11 12" + "</span></span>";
        res += "</i>";
        if (i != resultList.length - 1) {
            res = res + "</a><hr class=\"mdc-list-divider\">";
        }
    }
    if (res === "") {
        $('#mdc-search-list').addClass("hidden");
    } else {
        $('#mdc-search-list').removeClass("hidden");
    }
    if (document.getElementById("tf-box-search-field").value === "") {
        $('#mdc-search-list').addClass("hidden");
    }
    return res;
}

function buildHtmlPlaylist(playList) {
    var res = "";
    for (var i = 0; i < playList.length; i++) {
        res += "<li class='mdc-list-item'>" +
            "<img style='height:78px;padding-right:14px;' src='" + playList[i].episodePoster + "' role='presentation'></img>";
        res += "<span class='mdc-list-item__text'>" + playList[i].title;
        if (playList[i].episode !== 0) {
            res += ", " + playList[i].episode + "/" + playList[i].episodeCount;
        }
        res += "<span class='mdc-list-item__text__secondary'>" + playList[i].episodeTitle + "</span></span>";
        res += "<a href='#' class='mdc-list-item__end-detail material-icons' " +
            "aria-label='Play now' title='Play now'" +
            "onclick='playNow(" + i + ");' >play_arrow</a>";
        res += "<a href='#' class='mdc-list-item__end-detail material-icons' style='margin-left: 8px' " +
            "aria-label='Delete' title='Delete'" +
            "onclick='deleteFromPlaylist(" + i + ");' >delete</a>";
        res += "</li>";
        if (i != playList.length - 1) {
            res += "</li><hr class=\"mdc-list-divider\">";
        }
    }
    return res;
}

function playNow(i) {

}

function deleteFromPlaylist(i) {

}

function buildHtmlList(userList) {
    var res = "";
    for (var i = 0; i < userList.length; i++) {
        res = res + "<li class=\"mdc-list-item\">" +
            "<img class=\"mdc-list-item__start-detail grey-bg\" src=\"" + userList[i].avatar + "\"" +
            "width=\"56\" height=\"56\" alt=\"Brown Bear\">";
        if (userList[i].uid === uid) {
            res += "<span id='user-self' style=\"display: block; margin-right: 16px\">" + userList[i].name + "</span>";
            res += "<div id='user-self-field' class=\"mdc-form-field\" style='display: none'>" +
                "<div class=\"mdc-textfield\" data-mdc-auto-init=\"MDCTextfield\">" +
                "<input onfocus=\"this.select();\" onblur=\"enterName();\" type=\"text\" id=\"name\" class=\"mdc-textfield__input\" value='" + userList[i].name + "'>" +
                "<label for=\"name\" class=\"mdc-textfield__label\"></label>" +
                "</div>" +
                "</div>";
            res += "<a href='#' onclick='editName()' class=\"material-icons mdc-toolbar__icon mdc-theme--secondary\">create</a>";
        } else {
            res += "<span>" + userList[i].name + "</span>";
        }
        if (userList[i].isOwner) {
            res += "<i class=\"mdc-list-item__end-detail material-icons mdc-theme--secondary\">star</i>";
        }
        res += "</i>";
        if (i != userList.length - 1) {
            res = res + "</li><hr class=\"mdc-list-divider\">";
        }
    }
    return res;
}

function enterName() {
    lastName = document.getElementById("name").value;
    changeName();
    document.getElementById("name").blur();
}

function copyInviteLink() {
    copyToClipboard(document.getElementById('invite-link'));
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

myPlayer.on('ended', function () {
    if (finishedFlag) {
        unbindPauseEvent();
        if (isOwner) {
            unbindTimeUpdate();
            var userAction = {
                "action": "finished"
            };
            socket.send(JSON.stringify(userAction));
        }
        unbindFinishedEvent();
    }
});

function bindPauseEvent() {
    pauseFlag = true;
}

function unbindPauseEvent() {
    pauseFlag = false;
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
    document.getElementById("user-self").style.display = "block";
    document.getElementById("user-self").value = lastName;
    document.getElementById("user-self-field").style.display = "none";
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

function copyToClipboard(element) {
    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val($(element).text()).select();
    document.execCommand("copy");
    $temp.remove();
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
        if (document.getElementById("name") != null) {
            document.getElementById("name").focus();
            document.getElementById("name").value = username;
            document.getElementById("name").blur();
        }
    } else {
        cookieDialog.show();
        document.getElementById("cookie-field").focus();
    }
}
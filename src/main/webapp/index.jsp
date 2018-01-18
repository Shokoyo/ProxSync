<%@ page import="de.dieser1memesprech.proxsync.database.Database" %>
<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<%@ page import="de.dieser1memesprech.proxsync.database.Notification" %>
<%@ page import="java.util.List" %>
<html language="de" class="mdc-typography">
<head>
    <%@include file="res/template/head.jsp" %>
    <link rel="stylesheet" href="res/player-style.css">
    <link rel="stylesheet" href="http://vjs.zencdn.net/6.2.4/video-js.css">
    <script src="http://vjs.zencdn.net/6.2.4/video.js"></script>
    <script src="res/videojs-contrib-hls.js"></script>
    <style>
        .video-js.vjs-playing .vjs-tech {
            pointer-events: none;
        }

        .video-js.vjs-paused .vjs-tech {
            pointer-events: none;
        }
    </style>
    <link rel="stylesheet" href="res/template.css">
</head>
<body class="mdc-theme--background">
<%@include file="res/template/header.jsp" %>
<main class="mdc-toolbar-fixed-adjust">
    <div class="mdc-simple-menu" id="search-menu" tabindex="-1">
        <ul class="mdc-dialog__body--scrollable mdc-simple-menu__items mdc-list mdc-list--avatar-list menu-search"
            role="menu" id="mdc-search-list">
        </ul>
    </div>
    <div class="mdc-layout-grid">
        <div class="mdc-layout-grid__inner">
            <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-4">
                <span id="anime-title" class="mdc-typography--headline"></span>
            </div>
            <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-4">
                <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg"
                        onclick="addToWatchlist(true)"
                        id="add-next-watchlist-button"
                        style="align-self: center;float: right;">watchlist: next
                </button>
                <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg"
                        onclick="addToWatchlist(false)"
                        id="add-watchlist-button"
                        style="align-self: center;float: right;margin-right: 16px;">watchlist: this
                </button>
            </div>
        </div>
        <div class="mdc-layout-grid__inner">
            <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-8">
                <video id="my-player" class="video-js vjs-default-skin" controls preload="auto"
                       data-setup='{ "techOrder": ["html5", "youtube"]}'></video>
            </div>
            <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-4">
                <div class="mdc-layout-grid__inner">
                    <div id="cell-toolbar" class="mdc-layout-grid__cell mdc-layout-grid__cell--span-12">
                        <header class="mdc-toolbar">
                            <div class="mdc-toolbar__row">
                                <section class="mdc-toolbar__section mdc-toolbar__section--align-start"
                                         style="height:0;">
                                    <div id='room-name-field' class="mdc-toolbar__title light-font"
                                         style='display: none;float:left;'>
                                        <div class="mdc-text-field light-font">
                                            <input onfocus="this.select();" onblur="enterRoomName();" type="text"
                                                   id="room-name-in" class="mdc-text-field__input light-font">
                                            <label for="room-name-in" class="mdc-text-field__label light-font"></label>
                                            <div class="mdc-text-field__bottom-line mdc-theme--background"
                                                 style="height:1px;"></div>
                                        </div>
                                    </div>
                                    <span class="mdc-toolbar__title" id="room-id-out" style="float:left;"></span>
                                    <a href="#" id="room-name-changer" onclick="editRoomName(event);"
                                       class="material-icons mdc-toolbar__icon mdc-theme--secondary"
                                       style="float:left;align-self:center;display:none;">create</a>
                                </section>
                                <section class="mdc-toolbar__section mdc-toolbar__section--align-end"
                                         style="height:0;">
                                    <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg"
                                            style="margin-right: 16px;align-self: center;"
                                            onclick="copyInviteLink();"
                                            id="invite-button">Invite Link
                                    </button>
                                </section>
                            </div>
                            <div class="mdc-toolbar__row">
                                <nav id="dynamic-tab-bar" class="mdc-tab-bar mdc-tab-bar--indicator-accent"
                                     role="tablist">
                                    <a role="tab" aria-controls="tab-users" href="#one" onclick="return false;"
                                       class="mdc-tab">Users</a>
                                    <span class="mdc-tab-bar__indicator"></span>
                                    <a role="tab" aria-controls="tab-playlist" href="#two"
                                       onclick="return false;"
                                       class="mdc-tab mdc-tab--active">Playlist</a>
                                </nav>
                            </div>
                        </header>
                    </div>
                </div>
                <div class="mdc-layout-grid__inner">
                    <div id="cell-panel-users" class="mdc-layout-grid__cell mdc-layout-grid__cell--span-12">
                        <section>
                            <div class="panels" id="panels">
                                <div class="panel" id="panel-users" role="tabpanel" aria-hidden="true">
                                    <div style="margin-left:12px;">
                                        <section id="mdc-users-list"
                                                 class="mdc-dialog__body mdc-dialog__body--scrollable"
                                                 style="max-height: 100px">
                                            <ul class="mdc-list mdc-list--avatar-list" id="user-list">
                                            </ul>
                                        </section>
                                    </div>
                                </div>
                                <div class="panel active" id="panel-playlist" role="tabpanel" aria-hidden="false">
                                    <div class="mdc-layout-grid__inner">
                                        <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-8">
                                            <button class="mdc-button mdc-button--raised mdc-theme--primary-bg"
                                                    onclick="loadVideo()" id="url-button"
                                                    style="margin-right: 16px; margin-top: 32px;margin-left:16px;">
                                                Load Video URL
                                            </button>
                                            <div class="mdc-form-field">
                                                <div id="url-field" class="mdc-text-field"
                                                     data-mdc-auto-init="MDCTextfield">
                                                    <input type="text" id="url" class="mdc-text-field__input"
                                                           onclick="this.focus();this.select()">
                                                    <label for="url" class="mdc-text-field__label">URL</label>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-4">
                                            <div class="mdc-form-field" id="auto-next-container"
                                                 style="margin-right: 10px; margin-top: 20px;">
                                                <div class="mdc-switch">
                                                    <input type="checkbox" id="auto-next-checkbox"
                                                           class="mdc-switch__native-control"/>
                                                    <div class="mdc-switch__background">
                                                        <div class="mdc-switch__knob"></div>
                                                    </div>
                                                </div>
                                                <label for="auto-next-checkbox" class="mdc-switch-label">Auto
                                                    next
                                                    episode</label>
                                            </div>
                                            <div class="mdc-form-field" id="auto-play-container"
                                                 style="margin-right: 10px; margin-top: 10px;">
                                                <div class="mdc-switch">
                                                    <input type="checkbox" id="auto-play-checkbox"
                                                           class="mdc-switch__native-control"/>
                                                    <div class="mdc-switch__background">
                                                        <div class="mdc-switch__knob"></div>
                                                    </div>
                                                </div>
                                                <label for="auto-play-checkbox"
                                                       class="mdc-switch-label">Auto-Play</label>
                                            </div>
                                        </div>
                                    </div>
                                    <section id="playlist-list-section"
                                             class="mdc-dialog__body mdc-dialog__body--scrollable">
                                        <ul id="playlist-list"
                                            class="mdc-list mdc-list--two-line mdc-list--avatar-list two-line-avatar-text-icon-demo">
                                        </ul>
                                    </section>
                                </div>
                            </div>
                        </section>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="mdc-snackbar"
         aria-live="assertive"
         aria-atomic="true"
         aria-hidden="true">
        <div class="mdc-snackbar__text"></div>
        <div class="mdc-snackbar__action-wrapper">
            <button type="button" class="mdc-button mdc-snackbar__action-button"></button>
        </div>
    </div>
</main>
<p id="invite-link"></p>
<script src="https://www.gstatic.com/firebasejs/4.8.2/firebase.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.8.2/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.8.2/firebase-auth.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.8.2/firebase-database.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.8.2/firebase-firestore.js"></script>
<script>
    var oldConfig = {
        apiKey: "AIzaSyDDD68tM8V5yNi3aiZco8FnK6IiXTOAhi8",
        authDomain: "proxsync.firebaseapp.com",
        databaseURL: "https://proxsync.firebaseio.com",
        projectId: "proxsync",
        storageBucket: "",
        messagingSenderId: "424948078611"
    };
    var config = {
        apiKey: "AIzaSyCHMFCl1SAsC9VDeunRsIU3UpuCQ5JQdA4",
        authDomain: "anisync-be184.firebaseapp.com",
        databaseURL: "https://anisync-be184.firebaseio.com",
        projectId: "anisync-be184",
        storageBucket: "anisync-be184.appspot.com",
        messagingSenderId: "908197635545"
    };
    firebase.initializeApp(config);
    firebase.initializeApp(oldConfig, "oldFirebase");
    var db = firebase.firestore();
</script>
<script src="res/firebase.js"></script>
<script src="res/firebaseauth.js"></script>
<script src="https://unpkg.com/material-components-web@0.26.0/dist/material-components-web.min.js"></script>
<script src="res/videojs.disableProgress.js"></script>
<script src="res/player-script.js?v=0.0.0.4.10"></script>
<script src="res/tab-switch.js?v=0.1"></script>
<script src="res/Youtube.js"></script>
<script src="res/notification-script.js"></script>
<script>
    var notificationsEl = document.querySelector('#notification-menu');
    if(notificationsEl != null) {
        var menuNotifications = new mdc.menu.MDCSimpleMenu(notificationsEl);
    }
    var timeOut;
    mdc.textField.MDCTextField.attachTo(document.querySelector('.mdc-text-field'));
    var menuEl = document.querySelector('#profile-menu');
    if(menuEl != null) {
        var menuTop = new mdc.menu.MDCSimpleMenu(menuEl);
        menuEl.addEventListener('MDCSimpleMenu:selected', function (evt) {
            menuTop.open = false;
            var detail = evt.detail;
            switch (detail.index) {
                case 0:
                    followLink("/profile/");
                    break;
                case 1:
                    followLink("/watchlist/");
                    break;
                case 2:
                    followLink("/settings/");
                    break;
                case 3:
                    signout();
            }
        });
    }

    function followLink(loc) {
        var path = window.location.pathname;
        console.log(path);
        if (path.indexOf("/") !== -1) {
            path = path.substring(0, path.length - 1);
        }
        console.log(path);
        window.location = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + path + loc;
    }
    var urlFieldEl = document.querySelector('#url-field');
    var urlField = new mdc.textField.MDCTextField(urlFieldEl);
</script>
</body>
</html>
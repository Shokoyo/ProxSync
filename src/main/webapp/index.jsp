<%@ page import="de.dieser1memesprech.proxsync.database.Database" %>
<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<html language="de" class="mdc-typography">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet"
          href="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.css">
    <link rel="stylesheet" href="res/template.css">
    <link rel="stylesheet" href="res/theme-standard.css">
    <link href="http://vjs.zencdn.net/6.2.4/video-js.css" rel="stylesheet">
    <link href="res/player-style.css" rel="stylesheet">
    <link href="res/style.css" rel="stylesheet">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto+Mono">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500">
    <script src="http://vjs.zencdn.net/6.2.4/video.js"></script>
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <style>
        .video-js.vjs-playing .vjs-tech {
            pointer-events: none;
        }

        .video-js.vjs-paused .vjs-tech {
            pointer-events: none;
        }
    </style>
    <title>Prox-Sync</title>
</head>
<body class="mdc-theme--background">
<header id="page-header"
        class="mdc-toolbar mdc-toolbar--fixed">
    <div class="mdc-toolbar__row">
        <section class="mdc-toolbar__section mdc-toolbar__section--align-start">
            <div id="tf-box-search"
                 class="mdc-textfield mdc-textfield--box mdc-textfield--upgraded mdc-ripple-upgraded"
                 data-demo-no-auto-js=""
                 style="--mdc-ripple-surface-width:214px; --mdc-ripple-surface-height:56px; --mdc-ripple-fg-size:128.4px; --mdc-ripple-fg-scale:1.80067; --mdc-ripple-fg-translate-start:22.8px, -28.6375px; --mdc-ripple-fg-translate-end:42.8px, -36.2px;">
                <input type="text" id="tf-box-search-field"
                       class="mdc-textfield__input mdc-theme--primary-light"
                       style="color: rgba(255,255,255,0.7)!important;">
                <label for="tf-box-search-field" class="mdc-textfield__label mdc-theme--primary-light"
                       style="color: rgba(255,255,255,0.7)!important;">SEARCH</label>
                <div class="mdc-textfield__bottom-line"></div>
            </div>
            <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg"
                    onclick="leaveRoom()"
                    id="leave-button"
                    style="align-self: center;margin-left:16px;margin-right: 16px;">New Room
            </button>
        </section>
        <section class="mdc-toolbar__section mdc-toolbar__section--align-middle">
            <span class="mdc-toolbar__title">ProxSync</span>
        </section>
        <section class="mdc-toolbar__section mdc-toolbar__section--align-end">
            <section class="mdc-toolbar__section mdc-toolbar__section--align-start">
                <div id="register-row"
                     style="align-self:center;float:right;margin-right:16px;margin-left:auto;">
                    <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg mdc-button--align-middle"
                            onclick="register()"
                            id="register-button"
                            style="
                                    align-self: center;
                                    margin-left:10px;">Sign in
                    </button>
                </div>
                <div id="signout-row" style="align-self: center; margin-right: 16px; margin-left: auto;">
                    <div style="float:right;" onmouseover="clearTimeout(timeOut); menuTop.open = true;"
                         onmouseout="timeOut = setTimeout(function() {menuTop.open = false;},200);"
                         id="profile-mouseaction">
                        <a href="/profile">
                            <img src="<%
                            String url= "https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/panda.svg?alt=media&token=6f4d5bf1-af69-4211-994d-66655456d91a";
                            String uid = LoginUtil.getUid(request);
                            if (!"".equals(uid)) {
                            String databaseUrl = Database.getAvatarFromDatabase(uid);
                            if(databaseUrl.equals("null")) {
                                Database.setAvatar(uid, url);
                            } else {
                                url = databaseUrl;
                            }
                            }
                            %><%=url%>" id="avatar-toolbar" class="user-avatar-toolbar">
                        </a>
                        <div class="mdc-simple-menu mdc-simple-menu--open-from-top-right" id="profile-menu"
                             tabindex="-1" style="top:64px;right:-14px;">
                            <ul class="mdc-simple-menu__items mdc-list" role="menu" id="profile-list"
                                aria-hidden="true">
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0">
                                    <span style="align-self:center;">Profile</span>
                                </li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0">
                                    <span style="align-self:center;">Watchlist</span>
                                </li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0">
                                    <span style="align-self:center;">Settings</span>
                                </li>
                                <li class="mdc-list-divider" role="separator"></li>
                                <li class="mdc-list-item profile-list" role="menuitem" tabindex="0">
                                    <span style="align-self:center;">Sign Out</span>
                                </li>
                            </ul>
                        </div>
                    </div>
                    <span id="welcome-msg" class="mdc-toolbar__title"
                          style="margin-top:4px;float:right;align-self:center;"></span>
                </div>
            </section>
        </section>
    </div>
</header>
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
                        onclick="watchlistNext()"
                        id="add-next-watchlist-button"
                        style="align-self: center;float: right;">watchlist: next
                </button>
                <button class="mdc-button mdc-button--raised mdc-theme--secondary-bg"
                        onclick="addToWatchlist()"
                        id="add-watchlist-button"
                        style="align-self: center;float: right;margin-right: 16px;">watchlist: this
                </button>
            </div>
        </div>
        <div class="mdc-layout-grid__inner">
            <div class="mdc-layout-grid__cell mdc-layout-grid__cell--span-8">
                <video id="my-player" class="video-js vjs-default-skin" controls preload="auto"
                       data-setup='{}'></video>
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
                                        <div class="mdc-textfield light-font">
                                            <input onfocus="this.select();" onblur="enterRoomName();" type="text"
                                                   id="room-name-in" class="mdc-textfield__input light-font">
                                            <label for="room-name-in" class="mdc-textfield__label light-font"></label>
                                            <div class="mdc-textfield__bottom-line mdc-theme--background"
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
                                                <div id="url-field" class="mdc-textfield"
                                                     data-mdc-auto-init="MDCTextfield">
                                                    <input type="text" id="url" class="mdc-textfield__input"
                                                           onclick="this.focus();this.select()">
                                                    <label for="url" class="mdc-textfield__label">URL</label>
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
</main>
<p id="invite-link"></p>
<script src="https://www.gstatic.com/firebasejs/4.3.1/firebase.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.0/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.0/firebase-auth.js"></script>
<script>
    // Initialize Firebase
    var config = {
        apiKey: "AIzaSyDDD68tM8V5yNi3aiZco8FnK6IiXTOAhi8",
        authDomain: "proxsync.firebaseapp.com",
        databaseURL: "https://proxsync.firebaseio.com",
        projectId: "proxsync",
        storageBucket: "",
        messagingSenderId: "424948078611"
    };
    firebase.initializeApp(config);
</script>
<script src="res/firebase.js"></script>
<script src="res/firebaseauth.js"></script>
<script src="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"></script>
<script src="res/videojs.disableProgress.js"></script>
<script src="res/player-script.js?v=0.0.0.4.10"></script>
<script src="res/tab-switch.js?v=0.1"></script>
<script>
    var timeOut;
    mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
    var menuEl = document.querySelector('#profile-menu');
    var menuTop = new mdc.menu.MDCSimpleMenu(menuEl);
    var urlFieldEl = document.querySelector('#url-field');
    var urlField = new mdc.textfield.MDCTextfield(urlFieldEl);

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

    function followLink(loc) {
        window.location = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + loc;
    }
</script>
</body>
</html>
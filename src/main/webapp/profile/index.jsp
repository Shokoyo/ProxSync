<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="de.dieser1memesprech.proxsync.database.*" %>
<!--TODO make responsive! fit to screen-->
<html language="de" class="mdc-typography">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
    <link rel="stylesheet"
          href="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.css">
    <link rel="stylesheet" href="../res/theme-standard.css">
    <link rel="stylesheet" href="../res/style.css">
    <link rel="stylesheet" href="res/style.css">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto+Mono">
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
    <title>Prox-Sync</title>
</head>
<body class="mdc-theme--background mdc-typography adjusted-body">
<header id="page-header"
        class="mdc-toolbar mdc-toolbar--fixed">
    <div class="mdc-toolbar__row">
        <section class="mdc-toolbar__section mdc-toolbar__section--align-start">
            <div id="tf-box-search"
                 class="mdc-textfield mdc-textfield--box">
                <input type="text" id="tf-box-search-field"
                       class="mdc-textfield__input mdc-theme--primary-light"
                       style="color: rgba(255,255,255,0.7)!important;">
                <label for="tf-box-search-field" class="mdc-textfield__label mdc-theme--primary-light"
                       style="color: rgba(255,255,255,0.7)!important;">SEARCH</label>
                <div class="mdc-textfield__bottom-line"></div>
            </div>
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
                    <div style="float:right;" onmouseover="clearTimeout(timeOut); menu.open = true;"
                         onmouseout="timeOut = setTimeout(function() {menu.open = false;},200);"
                         id="profile-mouseaction">
                        <a href="../profile">
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
                    <i class="material-icons mdc-toolbar__icon"
                       style="float:right;font-size:40px;padding:8px!important;"
                       onclick="menuNotifications.open = !menuNotifications.open">
                        <%
                            List<Notification> notifications = Database.getNotifications(uid);
                            if (notifications.isEmpty()) {
                        %>
                        notifications_none
                        <%
                        } else {
                        %>
                        notifications_active
                        <%
                            }
                        %>
                    </i>
                    <div class="mdc-simple-menu mdc-simple-menu--open-from-top-right" tabindex="-1"
                         id="notification-menu" style="top:64px;right:72px;">
                        <ul class="mdc-simple-menu__items mdc-list" role="menu" aria-hidden="true">
                            <%
                                for (int i = 0; i < notifications.size(); i++) {
                                    Notification n = notifications.get(i);
                            %>
                            <li class="mdc-list-item profile-list" role="menuitem" tabindex="0">
                                <span style="align-self:center"><%=n.getTitle()%>: <%=n.getLatestEpisode()%>/<%=n.getEpisodeCount()%></span>
                            </li>
                            <% if (i < notifications.size() - 1) {%>
                            <li role="separator" class="mdc-list-divider"></li>
                            <%}%>
                            <%}%>
                        </ul>
                    </div>
                    <span id="welcome-msg" class="mdc-toolbar__title"
                          style="margin-top:4px;float:right;align-self:center;"></span>
                </div>
            </section>
        </section>
    </div>
</header>
<div class="content mdc-toolbar-fixed-adjust">
    <nav class="mdc-permanent-drawer">
        <nav class="mdc-list mdc-drawer__content">
            <a class="mdc-list-item left-list mdc-permanent-drawer--selected" href="../profile">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">person</i><span
                    class="text-in-list">Profile</span>
            </a>
            <a class="mdc-list-item left-list" href="../watchlist">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">video_library</i><span
                    class="text-in-list">Watch List</span>
            </a>
            <a class="mdc-list-item left-list" href="../airing">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">new_releases</i><span
                    class="text-in-list">Airing</span>
            </a>
            <a class="mdc-list-item left-list" href="../">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">ondemand_video</i><span
                    class="text-in-list">ProxSync</span>
            </a>
            <a class="mdc-list-item left-list" href="../settings">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">settings</i><span
                    class="text-in-list">Settings</span>
            </a>
        </nav>
    </nav>
    <main class="main">
        <% if (!"".equals(uid)) {%>
        <div class="mdc-card mdc-card--theme-dark user-card"
             id="banner-div"
             style="background-image: url(
                 <% String urlBanner = "https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/banner-default.png?alt=media&token=424d9e70-d360-4842-94ca-133ba9bb71ec";
                                               String databaseUrl = Database.getBannerFromDatabase(uid);
                                               if(databaseUrl.equals("null")) {
                                               Database.setBanner(uid, urlBanner);
                                               } else {
                                               urlBanner = databaseUrl;
                                               }
             %>
                 <%=urlBanner%>);">
            <input type="file" id="file-banner" name="file" style="display: none;"/>
            <button class="mdc-fab material-icons" id="banner-button" aria-label="Favorite">
                <span class="mdc-fab__icon">
                    edit
                </span>
            </button>
            <section class="mdc-card__primary" style="width:auto;">
                <img class="user-card__avatar" id="avatar-on-card" src="<%=url%>">
                <input type="file" id="files" name="files" style="display: none;"/>
                <h1 class="mdc-card__title mdc-card__title--large" id="user-name">&nbsp;</h1>
                <h2 class="mdc-card__subtitle">10000000000 Punkte (Kami-Sama)</h2>
            </section>
        </div>
        <h2>Favorites</h2>
        <div class="mdc-grid-list">
            <ul class="mdc-grid-list__tiles">
                <%
                    for (WatchlistEntry e : Database.getFavorites(uid)) {
                %>
                <li class="mdc-grid-title"
                    id="card-<%=e.getKey()%>">
                    <div class="mdc-grid-tile__primary">

                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                             style="background-image:url(<%=e.getPoster()%>);">
                            <a href="javascript:void(0);"
                               class="remove-from-watchlist-button-background material-icons mdc-theme--secondary mdc-24">fiber_manual_record</a>
                            <a href="#"
                               onclick="removeFromFavorites('<%=e.getKey()%>');return false;"
                               class="remove-from-watchlist-button material-icons mdc-theme--secondary">
                                cancel
                            </a>
                            <section class="mdc-card__primary watchlist-item">
                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                </h1>
                            </section>
                        </div>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </div>
        <%} else {%>
        <h4>Please log in</h4>
        <%}%>
    </main>
</div>

<script src="https://www.gstatic.com/firebasejs/4.3.1/firebase.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.0/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.0/firebase-auth.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.0/firebase-storage.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.0/firebase-database.js"></script>
<script>
    // Initialize Firebase
    var config = {
        apiKey: "AIzaSyDDD68tM8V5yNi3aiZco8FnK6IiXTOAhi8",
        authDomain: "proxsync.firebaseapp.com",
        databaseURL: "https://proxsync.firebaseio.com",
        projectId: "proxsync",
        storageBucket: "gs://proxsync.appspot.com",
        messagingSenderId: "424948078611"
    };
    firebase.initializeApp(config);
</script>
<script src="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"></script>
<script src="../res/firebase.js"></script>
<script src="res/avatarChange.js"></script>
<script src="res/manageFavorites.js"></script>
<script>
    var notificationsEl = document.querySelector('#notification-menu');
    var menuNotifications = new mdc.menu.MDCSimpleMenu(notificationsEl);
    var timeOut;
    mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
    var menuEl = document.querySelector('#profile-menu');
    var menu = new mdc.menu.MDCSimpleMenu(menuEl);

    menuEl.addEventListener('MDCSimpleMenu:selected', function (evt) {
        menu.open = false;
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
        var path = window.location.pathname;
        path = path.substring(0, path.lastIndexOf("/"));
        path = path.substring(0, path.lastIndexOf("/"));
        window.location = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + path + loc;
    }
</script>
<script>
    $("#avatar-on-card").click(function () {
        $("#files").click();
    });
    $("#banner-button").click(function () {
        $("#file-banner").click();
    });
    function handleAvatarSelect(evt) {
        var files = evt.target.files; // FileList object

        uploadAvatar(files[0]);
    }

    function handleBannerSelect(evt) {
        var files = evt.target.files;

        uploadBanner(files[0]);
    }
    if (document.getElementById("file-banner") != null) {
        document.getElementById("file-banner").addEventListener('change', handleBannerSelect, false);
    }
    if (document.getElementById("files") != null) {
        document.getElementById('files').addEventListener('change', handleAvatarSelect, false);
    }
    firebase.auth().onAuthStateChanged(function (authData) {
        if (authData) {
            console.log("Logged in as:", authData.uid);
            if (getCookie("anonymous") === "true" && !authData.isAnonymous) {
                console.log("non anonymous login");
                setCookie("loginData", authData.uid, 10000);
                setCookie("anonymous", "false", 10000);
                location.reload();
                return;
            }
            currentUser = authData.currentUser;
            uid = authData.uid;
            anonymous = authData.isAnonymous;
            if (!anonymous) {
                document.getElementById("user-name").innerHTML = "" + authData.displayName;
            }
            updateAuthButtons(authData);
        }
        else {
            console.log("Not logged in; going to log in as anonymous");
            currentUser = null;
            setCookie("anonymous", "true", 10000);
            firebase.auth().signInAnonymously().catch(function (error) {
                console.error("Anonymous authentication failed:", error);
            });
        }
    });
</script>
<script>
    var autoSizeText;

    autoSizeText = function () {
        var el, elements, _i, _len, _results;
        elements = $('.resize');
        console.log(elements);
        if (elements.length < 0) {
            return;
        }
        _results = [];
        for (_i = 0, _len = elements.length; _i < _len; _i++) {
            el = elements[_i];
            _results.push((function (el) {
                var resizeText, _results1;
                resizeText = function () {
                    var elNewFontSize;
                    elNewFontSize = (parseInt($(el).css('font-size').slice(0, -2)) - 1) + 'px';
                    return $(el).css('font-size', elNewFontSize);
                };
                _results1 = [];
                while (el.scrollHeight > el.offsetHeight) {
                    _results1.push(resizeText());
                }
                return _results1;
            })(el));
        }
        return _results;
    };

    $(document).ready(function () {
        return autoSizeText();
    });
</script>
</body>
</html>
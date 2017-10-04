<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="de.dieser1memesprech.proxsync.database.*" %>
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
                            <%if (!notifications.isEmpty() && !"".equals(uid)) {%>
                            <%
                                for (int i = 0; i < notifications.size(); i++) {
                                    Notification n = notifications.get(i);
                            %>
                            <li class="mdc-list-item profile-list" role="menuitem" aria-disabled="true"
                                id="notifications-<%=n.getKey()%>">
                                <span style="align-self:center"><a href="javascript:void(0)"
                                                                   onclick="watchNext(event,'<%=n.getKey()%>');return false;"
                                                                   style="text-decoration:none;color:inherit;"><%=n.getTitle()%>: <%=n.getLatestEpisode()%>/<%=n.getEpisodeCount()%></a><i
                                        onclick="removeNotification('<%=n.getKey()%>');return false;"
                                        class="material-icons remove-notification">clear</i></span>
                            </li>
                            <% if (i < notifications.size() - 1) {%>
                            <li role="separator" class="mdc-list-divider" id="divider-<%=n.getKey()%>"></li>
                            <%}%>
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
    <div class="mdc-simple-menu" id="search-menu" tabindex="-1">
        <ul class="mdc-dialog__body--scrollable mdc-simple-menu__items mdc-list mdc-list--avatar-list menu-search"
            role="menu" id="mdc-search-list">
        </ul>
    </div>
    <nav class="mdc-permanent-drawer">
        <nav class="mdc-list">
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
    <main>
        <div class="mdc-layout-grid main" style="margin-left:auto;margin-right:auto;">
            <div class="mdc-layout-grid__inner">
                <div class="mdc-layout-grid__cell--span-1"></div>
                <div class="mdc-layout-grid__cell--span-10">
                    <div id="watchlist-toolbar">
                        <nav id="dynamic-tab-bar" class="mdc-tab-bar">
                            <a class="mdc-tab mdc-tab--active" id="tv-tab" href="#tv">TV</a>
                            <a class="mdc-tab" id="short-tab" href="#short">Short</a>
                            <a class="mdc-tab" href="#movie" id="movie-tab">Movie</a>
                            <a class="mdc-tab" href="#ova" id="ova-tab">OVA</a>
                            <span class="mdc-tab-bar__indicator"></span>
                        </nav>
                    </div>
                </div>
                <div class="mdc-layout-grid__cell--span-1"></div>
            </div>
            <section class="main">
                <div class="panels" id="panels">
                    <%
                        AiringList airinglist = Database.getAiringList();
                    %>
                    <div class="panel active" id="panel-tv" role="tabpanel" aria-hidden="false">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (AiringEntry e : airinglist.getTvList()) {
                                        long epCount = e.getEpisodes();
                                        String episodes = "" + epCount;
                                        if (epCount == 0) {
                                            episodes = "?";
                                        }
                                        long durationNumber = e.getDuration();
                                        String duration = "" + durationNumber;
                                        if (durationNumber == 0) {
                                            duration = "?";
                                        }
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getId()%>">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=episodes%> episode(s) x <%=duration%>
                                                    min.
                                                </h2>
                                                <h2 class="mdc-card__subtitle resize" style="height:20px;max-width:216px;">
                                                    <%=e.getGenresOverflow()%>
                                                </h2>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%}%>
                            </ul>
                        </div>
                    </div>
                    <div class="panel" id="panel-short" role="tabpanel" aria-hidden="true">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (AiringEntry e : airinglist.getShortList()) {
                                        long epCount = e.getEpisodes();
                                        String episodes = "" + epCount;
                                        if (epCount == 0) {
                                            episodes = "?";
                                        }
                                        long durationNumber = e.getDuration();
                                        String duration = "" + durationNumber;
                                        if (durationNumber == 0) {
                                            duration = "?";
                                        }
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getId()%>">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=episodes%> episode(s) x <%=duration%>
                                                    min.
                                                </h2>
                                                <h2 class="mdc-card__subtitle resize" style="height:20px;max-width:216px;">
                                                    <%=e.getGenresOverflow()%>
                                                </h2>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%}%>
                            </ul>
                        </div>
                    </div>
                    <div class="panel" id="panel-movie" role="tabpanel" aria-hidden="true">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (AiringEntry e : airinglist.getMovieList()) {
                                        long epCount = e.getEpisodes();
                                        String episodes = "" + epCount;
                                        if (epCount == 0) {
                                            episodes = "?";
                                        }
                                        long durationNumber = e.getDuration();
                                        String duration = "" + durationNumber;
                                        if (durationNumber == 0) {
                                            duration = "?";
                                        }
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getId()%>">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=episodes%> episode(s) x <%=duration%>
                                                    min.
                                                </h2>
                                                <h2 class="mdc-card__subtitle resize" style="height:20px;max-width:216px;">
                                                    <%=e.getGenresOverflow()%>
                                                </h2>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%}%>
                            </ul>
                        </div>
                    </div>
                    <div class="panel" id="panel-ova" role="tabpanel" aria-hidden="true">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (AiringEntry e : airinglist.getOvaList()) {
                                        long epCount = e.getEpisodes();
                                        String episodes = "" + epCount;
                                        if (epCount == 0) {
                                            episodes = "?";
                                        }
                                        long durationNumber = e.getDuration();
                                        String duration = "" + durationNumber;
                                        if (durationNumber == 0) {
                                            duration = "?";
                                        }
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getId()%>">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=episodes%> episode(s) x <%=duration%>
                                                    min.
                                                </h2>
                                                <h2 class="mdc-card__subtitle resize" style="height:20px;max-width:216px;">
                                                    <%=e.getGenresOverflow()%>
                                                </h2>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%}%>
                            </ul>
                        </div>
                    </div>
                </div>
            </section>
        </div>
    </main>
    <div class="mdc-snackbar"
         aria-live="assertive"
         aria-atomic="true"
         aria-hidden="true">
        <div class="mdc-snackbar__text"></div>
        <div class="mdc-snackbar__action-wrapper">
            <button type="button" class="mdc-button mdc-snackbar__action-button"></button>
        </div>
    </div>
</div>
<script src="https://www.gstatic.com/firebasejs/4.3.1/firebase.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.1/firebase-app.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.1/firebase-auth.js"></script>
<script src="https://www.gstatic.com/firebasejs/4.3.1/firebase-database.js"></script>
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
<script src="../res/firebaseauth-normal.js"></script>
<script src="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"></script>
<script src="res/tab-switch.js"></script>
<script src="../res/firebase.js"></script>
<script src="../res/menus.js"></script>
<script src="../res/search-script.js"></script>
</body>
</html>
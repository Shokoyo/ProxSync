<%@ page import="de.dieser1memesprech.proxsync.database.Database" %>
<%@ page import="de.dieser1memesprech.proxsync.database.Watchlist" %>
<%@ page import="de.dieser1memesprech.proxsync.database.WatchlistEntry" %>
<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
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
                         onmouseout = "timeOut = setTimeout(function() {menu.open = false;},200);" id="profile-mouseaction">
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
                        <div class="mdc-simple-menu mdc-simple-menu--open-from-top-right" id="profile-menu" tabindex="-1"style="top:64px;right:-14px;">
                            <ul class="mdc-simple-menu__items mdc-list" role="menu" id="profile-list" aria-hidden="true">
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
<div class="content mdc-toolbar-fixed-adjust">
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
                <div class="mdc-layout-grid__cell"></div>
                <div class="mdc-layout-grid__cell">
                    <div id="watchlist-toolbar">
                        <nav id="dynamic-tab-bar" class="mdc-tab-bar">
                            <a class="mdc-tab mdc-tab--active" id="watching-tab" href="#watching">Watching</a>
                            <a class="mdc-tab" href="#completed" id="completed-tab">Completed</a>
                            <a class="mdc-tab" href="#plantowatch" id="plan-tab">Plan to Watch</a>
                            <span class="mdc-tab-bar__indicator"></span>
                        </nav>
                    </div>
                    <div class="mdc-layout-grid__cell"></div>
                </div>
            </div>
            <section class="main">
                <div class="panels" id="panels">
                    <%
                        Cookie[] cookies = request.getCookies();
                        if (cookies != null) {
                            for (Cookie cookie : cookies) {
                                if (cookie.getName().equals("loginData")) {
                                    uid = cookie.getValue();
                                }
                            }
                        }
                        if ("".equals(uid)) { %>
                    <div class="panel active" id="panel-watching" role="tabpanel" aria-hidden="false">
                        <h4> Log in to view your watch list </h4>
                    </div>
                    <div class="panel" id="panel-completed" role="tabpanel" aria-hidden="true">
                        <h4> Log in to view your watch list </h4>
                    </div>
                    <div class="panel" id="panel-plan" role="tabpanel" aria-hidden="true">
                        <h4> Log in to view your watch list </h4>
                    </div>
                    <%
                    } else {
                        Watchlist watchlist = Database.getWatchlistObjectFromDatabase(uid);
                    %>
                    <div class="panel active" id="panel-watching" role="tabpanel" aria-hidden="false">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (WatchlistEntry e : watchlist.getWatching()) {
                                %>
                                <li class="mdc-grid-title">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large"><%=e.getAnimeTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=e.getEpisode()%>
                                                    /
                                                    <%=e.getEpisodeCount()%>
                                                </h2>
                                                <span class="mdc-card__subtitle">
                                                    <i class="material-icons watchlist-star">star</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                </span>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%
                                    }
                                %>
                            </ul>
                        </div>
                    </div>
                    <div class="panel" id="panel-completed" role="tabpanel" aria-hidden="true">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (WatchlistEntry e : watchlist.getCompleted()) {
                                %>
                                <li class="mdc-grid-title">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large"><%=e.getAnimeTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=e.getEpisode()%>
                                                    /<%=e.getEpisodeCount()%>
                                                </h2>
                                                <span class="mdc-card__subtitle">
                                                    <i class="material-icons watchlist-star">star</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                </span>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%
                                    }
                                %>
                            </ul>
                        </div>
                    </div>
                    <div class="panel" id="panel-plan" role="tabpanel" aria-hidden="true">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (WatchlistEntry e : watchlist.getPlanned()) {
                                %>
                                <li class="mdc-grid-title">
                                    <div class="mdc-grid-tile__primary">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large"><%=e.getAnimeTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=e.getEpisode()%>
                                                    /<%=e.getEpisodeCount()%>
                                                </h2>
                                                <span class="mdc-card__subtitle">
                                                    <i class="material-icons watchlist-star">star</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                </span>
                                            </section>
                                        </div>
                                    </div>
                                </li>
                                <%
                                    }
                                %>
                            </ul>
                        </div>
                    </div>
                    <%
                        }
                    %>
                </div>
            </section>
        </div>
    </main>
</div>
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
<script src="res/firebaseauth.js"></script>
<script src="https://unpkg.com/material-components-web@latest/dist/material-components-web.min.js"></script>
<script src="res/tab-switch.js"></script>
<script src="../res/firebase.js"></script>
<script>
    var timeOut;
    mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
    var menuEl = document.querySelector('#profile-menu');
    var menu = new mdc.menu.MDCSimpleMenu(menuEl);

    menuEl.addEventListener('MDCSimpleMenu:selected', function(evt) {
        menu.open = false;
        var detail = evt.detail;
        switch(detail.index) {
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
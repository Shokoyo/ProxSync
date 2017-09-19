<%@ page import="de.dieser1memesprech.proxsync.database.Database" %>
<%@ page import="net.thegreshams.firebase4j.model.FirebaseResponse" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="org.codehaus.jackson.map.ObjectMapper" %>
<%@ page import="com.google.gson.JsonElement" %>
<%@ page import="com.google.gson.JsonParser" %>
<%@ page import="com.google.gson.JsonArray" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.google.gson.JsonObject" %>
<%@ page import="de.dieser1memesprech.proxsync.database.Watchlist" %>
<%@ page import="de.dieser1memesprech.proxsync.database.WatchlistEntry" %>
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
<body class="mdc-theme--background mdc-typography">
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
                    <div style="float:right;"><a href="#" onclick="signout();"
                                                 class="material-icons mdc-toolbar__icon mdc-theme--secondary"
                                                 aria-label="Download" alt="Download" style="font-size: 32px;">account_circle</a>
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
            <a class="mdc-list-item mdc-permanent-drawer--selected" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">person</i><span
                    class="text-in-list">Profile</span>
            </a>
            <a class="mdc-list-item" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">video_library</i><span
                    class="text-in-list">Watch List</span>
            </a>
            <a class="mdc-list-item" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">new_releases</i><span
                    class="text-in-list">Airing</span>
            </a>
            <a class="mdc-list-item" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">ondemand_video</i><span
                    class="text-in-list">ProxSync</span>
            </a>
            <a class="mdc-list-item" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">settings</i><span
                    class="text-in-list">Settings</span>
            </a>
        </nav>
    </nav>
    <main>
        <div class="mdc-layout-grid" style="width:100%;">
            <div class="mdc-layout-grid__inner">
                <div class="mdc-layout-grid__cell"></div>
                <div class="mdc-layout-grid__cell">
                    <div id="watchlist-toolbar">
                        <nav id="dynamic-tab-bar" class="mdc-tab-bar">
                            <a class="mdc-tab mdc-tab--active" href="#one">Watching</a>
                            <a class="mdc-tab" href="#two">Completed</a>
                            <a class="mdc-tab" href="#three">Plan to Watch</a>
                            <span class="mdc-tab-bar__indicator"></span>
                        </nav>
                    </div>
                    <div class="mdc-layout-grid__cell"></div>
                </div>
            </div>
            <section>
                <div class="panels" id="panels">
                        <%
                        String uid = "";
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
                            <ul class="mdc-grid-list__tiles" style="width: 1632px;">
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
                        <div class="panel" id="panel-completed" role="tabpanel" aria-hidden="true">
                            <ul class="mdc-grid-list__tiles" style="width: 1632px;">
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
                        <div class="panel" id="panel-plan" role="tabpanel" aria-hidden="true">
                            <ul class="mdc-grid-list__tiles" style="width: 1632px;">
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
    mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
    window.mdc.autoInit();
</script>
</body>
</html>
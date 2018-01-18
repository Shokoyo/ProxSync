<%@ page import="de.dieser1memesprech.proxsync.database.Database" %>
<%@ page import="de.dieser1memesprech.proxsync.database.Watchlist" %>
<%@ page import="de.dieser1memesprech.proxsync.database.WatchlistEntry" %>
<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<%@ page import="de.dieser1memesprech.proxsync.database.Notification" %>
<%@ page import="java.util.List" %>
<html language="de" class="mdc-typography">
<head>
    <%@include file="../res/template/head.jsp" %>
</head>
<body class="mdc-theme--background mdc-typography adjusted-body">
<%@include file="../res/template/header.jsp" %>
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
                        List<String> favoriteKeys = Database.getFavoriteKeys(uid);
                    %>
                    <div class="panel active" id="panel-watching" role="tabpanel" aria-hidden="false">
                        <div class="mdc-grid-list">
                            <ul class="mdc-grid-list__tiles">
                                <%
                                    for (WatchlistEntry e : watchlist.getWatching()) {
                                        long rating = e.getRating();
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getKey()%>">
                                    <div class="mdc-grid-tile__primary">

                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <a href="javascript:void(0);"
                                               class="remove-from-watchlist-button-background material-icons mdc-theme--secondary mdc-24">fiber_manual_record</a>
                                            <a href="#"
                                               onclick="removeFromWatchlist('<%=e.getKey()%>');return false;"
                                               class="remove-from-watchlist-button material-icons mdc-theme--secondary">
                                                cancel
                                            </a>
                                            <a href="javascript:void(0);"
                                               class="favorite-button-background material-icons mdc-theme--secondary-light">favorite</a>
                                            <%if(favoriteKeys.contains(e.getKey().replaceAll("\\.", "-"))) {%>
                                            <a href="#" onclick="removeFromFavorites('<%=e.getKey()%>');return false;"
                                               class="favorite-button material-icons mdc-theme--secondary"
                                               id="favorites-<%=e.getKey()%>">
                                                favorite
                                            </a>
                                            <%} else {%>
                                            <a href="#" onclick="addToFavorites('<%=e.getKey()%>');return false;"
                                               class="favorite-button material-icons mdc-theme--secondary"
                                               id="favorites-<%=e.getKey()%>">
                                                favorite_border
                                            </a>
                                            <%}%>
                                            <section class="mdc-card__primary watchlist-item"
                                                     onclick="window.open('../?title=<%=e.getTitle()%>&episode=<%=(Integer.parseInt(e.getEpisode())+1)%>','_blank')">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=e.getEpisode()%>
                                                    /
                                                    <%=e.getEpisodeCount()%>
                                                </h2>
                                                <span class="mdc-card__subtitle">
                                                    <span id="stars-<%=e.getKey().replaceAll("\\.", "-")%>"
                                                          onmouseenter="over = 'stars-<%=e.getKey().replaceAll("\\.", "-")%>';oldScore = getScore('stars-<%=e.getKey().replaceAll("\\.", "-")%>');"
                                                          onmousemove="updateStars(event,'stars-<%=e.getKey().replaceAll("\\.", "-")%>');"
                                                          onclick="event.stopPropagation();updateRating('<%=e.getKey().replaceAll("\\.", "-")%>');"
                                                          onmouseleave="over=undefined;updateStarDisplay($('#stars-<%=e.getKey().replaceAll("\\.", "-")%>'), oldScore);">
                                                    <%
                                                        for (int i = 0; i < 5; i++) {
                                                            if (rating < 1) {
                                                    %>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <%
                                                    } else if (rating < 2) {
                                                    %>
                                                    <i class="material-icons watchlist-star">star_half</i>
                                                    <%
                                                    } else {
                                                    %>
                                                        <i class="material-icons watchlist-star">star</i>
                                                                <%
                                                                        }
                                                                        rating -= 2;
                                                                    }
                                                                %>
                                                    </span>
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
                                        long rating = e.getRating();
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getKey()%>">
                                    <div class="mdc-grid-tile__primary">

                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <a href="javascript:void(0);"
                                               class="remove-from-watchlist-button-background material-icons mdc-theme--secondary mdc-24">fiber_manual_record</a>
                                            <a href="#"
                                               onclick="removeFromWatchlist('<%=e.getKey()%>');return false;"
                                               class="remove-from-watchlist-button material-icons mdc-theme--secondary">
                                                cancel
                                            </a>
                                            <a href="javascript:void(0);"
                                               class="favorite-button-background material-icons mdc-theme--secondary-light">favorite</a>
                                            <%if(favoriteKeys.contains(e.getKey().replaceAll("\\.", "-"))) {%>
                                            <a href="#" onclick="removeFromFavorites('<%=e.getKey()%>');return false;"
                                               class="favorite-button material-icons mdc-theme--secondary"
                                               id="favorites-<%=e.getKey()%>">
                                                favorite
                                            </a>
                                            <%} else {%>
                                            <a href="#" onclick="addToFavorites('<%=e.getKey()%>');return false;"
                                               class="favorite-button material-icons mdc-theme--secondary"
                                               id="favorites-<%=e.getKey()%>">
                                                favorite_border
                                            </a>
                                            <%}%>
                                            <section class="mdc-card__primary watchlist-item"
                                                     onclick="window.open('../?title=<%=e.getTitle()%>&episode=<%=(Integer.parseInt(e.getEpisode())+1)%>','_self')">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=e.getEpisode()%>
                                                    /
                                                    <%=e.getEpisodeCount()%>
                                                </h2>
                                                <span class="mdc-card__subtitle">
                                                    <span id="stars-<%=e.getKey().replaceAll("\\.", "-")%>"
                                                          onmouseenter="over = 'stars-<%=e.getKey().replaceAll("\\.", "-")%>';oldScore = getScore('stars-<%=e.getKey().replaceAll("\\.", "-")%>');"
                                                          onmousemove="updateStars(event,'stars-<%=e.getKey().replaceAll("\\.", "-")%>');"
                                                          onclick="event.stopPropagation();updateRating('<%=e.getKey().replaceAll("\\.", "-")%>');"
                                                          onmouseleave="over=undefined;updateStarDisplay($('#stars-<%=e.getKey().replaceAll("\\.", "-")%>'), oldScore);">
                                                    <%
                                                        for (int i = 0; i < 5; i++) {
                                                            if (rating < 1) {
                                                    %>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <%
                                                    } else if (rating < 2) {
                                                    %>
                                                    <i class="material-icons watchlist-star">star_half</i>
                                                    <%
                                                    } else {
                                                    %>
                                                        <i class="material-icons watchlist-star">star</i>
                                                                <%
                                                                        }
                                                                        rating -= 2;
                                                                    }
                                                                %>
                                                    </span>
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
                                        long rating = e.getRating();
                                %>
                                <li class="mdc-grid-title"
                                    id="card-<%=e.getKey()%>">
                                    <div class="mdc-grid-tile__primary"
                                         onclick="window.open('../?title=<%=e.getTitle()%>&episode=1')">
                                        <div class="mdc-card mdc-card--theme-dark watchlist-card mdc-grid-title__primary-content"
                                             style="background-image:url(<%=e.getPoster()%>);">
                                            <a href="javascript:void(0);"
                                               class="remove-from-watchlist-button-background material-icons mdc-theme--secondary mdc-24">fiber_manual_record</a>
                                            <a href="#"
                                               onclick="removeFromWatchlist('<%=e.getKey()%>');return false;"
                                               class="remove-from-watchlist-button material-icons mdc-theme--secondary">
                                                cancel
                                            </a>
                                            <a href="javascript:void(0);"
                                               class="favorite-button-background material-icons mdc-theme--secondary-light">favorite</a>
                                            <%if(favoriteKeys.contains(e.getKey().replaceAll("\\.", "-"))) {%>
                                            <a href="#" onclick="removeFromFavorites('<%=e.getKey()%>');return false;"
                                               class="favorite-button material-icons mdc-theme--secondary"
                                               id="favorites-<%=e.getKey()%>">
                                                favorite
                                            </a>
                                            <%} else {%>
                                            <a href="#" onclick="addToFavorites('<%=e.getKey()%>');return false;"
                                               class="favorite-button material-icons mdc-theme--secondary"
                                               id="favorites-<%=e.getKey()%>">
                                                favorite_border
                                            </a>
                                            <%}%>
                                            <section class="mdc-card__primary">
                                                <h1 class="mdc-card__title mdc-card__title--large title-container resize"><%=e.getTitle()%>
                                                </h1>
                                                <h2 class="mdc-card__subtitle"><%=e.getEpisode()%>
                                                    /<%=e.getEpisodeCount()%>
                                                </h2>
                                                <span class="mdc-card__subtitle">
                                                    <%
                                                        for (int i = 0; i < 5; i++) {
                                                            if (rating < 1) {
                                                    %>
                                                    <i class="material-icons watchlist-star">star_border</i>
                                                    <%
                                                    } else if (rating < 2) {
                                                    %>
                                                    <i class="material-icons watchlist-star">star_half</i>
                                                    <%
                                                    } else {
                                                    %>
                                                    <i class="material-icons watchlist-star">star</i>
                                                                <%
                                                                        }
                                                                        rating -= 2;
                                                                    }
                                                                %>
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
<script>
    var autoSizeText;

    autoSizeText = function() {
        var el, elements, _i, _len, _results;
        elements = $('.resize');
        console.log(elements);
        if (elements.length < 0) {
            return;
        }
        _results = [];
        for (_i = 0, _len = elements.length; _i < _len; _i++) {
            el = elements[_i];
            _results.push((function(el) {
                var resizeText, _results1;
                resizeText = function() {
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

    $(document).ready(function() {
        return autoSizeText();
    });
</script>
<script src="https://unpkg.com/material-components-web@0.26.0/dist/material-components-web.min.js"></script>
<script src="res/tab-switch.js"></script>
<script src="../res/firebase.js"></script>
<script src="../res/firebaseauth-normal.js"></script>
<script src="res/watchlistManager.js"></script>
<script src="../res/menus.js"></script>
<script>
    var oldScore;
    var score = 0;
    var over;
    function updateStars(event, id) {
        var elem = $("#" + id);
        var x = event.pageX - /*event.currentTarget.offsetLeft -*/ elem.offset().left;
        updateStarDisplayByPos(elem, x);
    }
    function getScore(id) {
        var elem = $("#" + id);
        var stars = elem.children();
        var tempScore = 0;
        for(var i = 0; i < 5; i++) {
            if(stars[i].innerHTML === "star") {
                tempScore += 2;
            } else if(stars[i].innerHTML === "star_half") {
                tempScore += 1;
            }
        }
        return tempScore;
    }
    function updateStarDisplayByPos(elem, pos) {
        score = Math.floor(pos/(elem.width()/10)) + 1;
        var stars = elem.children();
        var tempScore = score;
        for(var i = 0; i < 5; i++) {
            if(tempScore > 1) {
                stars[i].innerHTML = "star";
            } else if(tempScore > 0) {
                stars[i].innerHTML = "star_half";
            } else {
                stars[i].innerHTML = "star_border";
            }
            tempScore -= 2;
        }
    }

    function updateStarDisplay(elem, score) {
        var stars = elem.children();
        var tempScore = score;
        for(var i = 0; i < 5; i++) {
            if(tempScore > 1) {
                stars[i].innerHTML = "star";
            } else if(tempScore > 0) {
                stars[i].innerHTML = "star_half";
            } else {
                stars[i].innerHTML = "star_border";
            }
            tempScore -= 2;
        }
    }

    function updateRating(key) {
        /*var submittedScore = score;
        var ratingRef = firebase.database().ref("/users/" + uid + "/watchlist/" + key + "/rating");
        ratingRef.once('value', function(snap) {
            if(over === "stars-" + key) {
                oldScore = submittedScore;
            }
            updateStarDisplay($('#stars-' + key), submittedScore);
        });
        ratingRef.set(score);*/
    }
</script>
<script src="../res/search-script.js"></script>
</body>
</html>
<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="de.dieser1memesprech.proxsync.database.*" %>
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
             style="background-image: url(https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/banner-default.png?alt=media&token=424d9e70-d360-4842-94ca-133ba9bb71ec);">
            <input type="file" id="file-banner" name="file" style="display: none;"/>
            <button class="mdc-fab material-icons" id="banner-button" aria-label="Favorite">
                <span class="mdc-fab__icon">
                    edit
                </span>
            </button>
            <section class="mdc-card__primary" style="width:auto;">
                <img class="user-card__avatar" id="avatar-on-card" src="https://firebasestorage.googleapis.com/v0/b/proxsync.appspot.com/o/panda.svg?alt=media&token=6f4d5bf1-af69-4211-994d-66655456d91a">
                <input type="file" id="files" name="files" style="display: none;"/>
                <h1 class="mdc-card__title mdc-card__title--large" id="user-name">&nbsp;</h1>
                <h2 class="mdc-card__subtitle">10000000000 Punkte (Kami-Sama)</h2>
            </section>
        </div>
        <h2 style="margin-bottom:0px;">Favorites</h2>
        <div class="mdc-grid-list">
            <ul class="mdc-grid-list__tiles" style="margin-left:-20px;">
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
<script src="https://www.gstatic.com/firebasejs/4.8.2/firebase-storage.js"></script>
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
<script src="https://unpkg.com/material-components-web@0.26.0/dist/material-components-web.min.js"></script>
<script src="../res/firebase.js"></script>
<script src="res/avatarChange.js"></script>
<script src="res/manageFavorites.js"></script>
<script src="../res/firebaseauth-normal.js"></script>
<script src="../res/menus.js"></script>
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
<script src="../res/notification-script.js"></script>
<script src="../res/search-script.js"></script>
</body>
</html>
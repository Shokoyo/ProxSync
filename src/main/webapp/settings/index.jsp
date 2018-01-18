<%@ page import="de.dieser1memesprech.proxsync.util.LoginUtil" %>
<%@ page import="de.dieser1memesprech.proxsync.database.Database" %>
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
<script src="https://unpkg.com/material-components-web@0.26.0/dist/material-components-web.min.js"></script>
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

    $(document).ready(function() {
        mdc.textField.MDCTextField.attachTo(document.querySelector('.mdc-text-field'));
    });
</script>
<script src="../res/firebaseauth-normal.js"></script>
<script src="../res/firebase.js"></script>
<script src="../res/menus.js"></script>
<script src="../res/search-script.js"></script>
</body>
</html>
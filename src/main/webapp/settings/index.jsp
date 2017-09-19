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
            <a class="mdc-list-item left-list mdc-permanent-drawer--selected" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">person</i><span class="text-in-list">Profile</span>
            </a>
            <a class="mdc-list-item left-list" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">video_library</i><span class="text-in-list">Watch List</span>
            </a>
            <a class="mdc-list-item left-list" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">new_releases</i><span class="text-in-list">Airing</span>
            </a>
            <a class="mdc-list-item left-list" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">ondemand_video</i><span class="text-in-list">ProxSync</span>
            </a>
            <a class="mdc-list-item left-list" href="#">
                <i class="material-icons mdc-list-item__start-detail" aria-hidden="true">settings</i><span class="text-in-list">Settings</span>
            </a>
        </nav>
    </nav>
    <main>
        <section style="margin-left:14px;">
                <h3>Full-Width Dividers</h3>
                <ul class="mdc-list" style="border:none;">
                    <li class="mdc-list-item">Import from Proxer</li>
                    <li class="mdc-list-divider" role="separator"></li>
                </ul>
        </section>
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
<script src="../res/firebase.js"></script>
<script>
    mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
</script>
</body>
</html>
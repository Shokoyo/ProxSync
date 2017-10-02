var notificationsEl = document.querySelector('#notification-menu');
if(notificationsEl != null) {
    var menuNotifications = new mdc.menu.MDCSimpleMenu(notificationsEl);
}
var timeOut;
mdc.textfield.MDCTextfield.attachTo(document.querySelector('.mdc-textfield'));
var menuEl = document.querySelector('#profile-menu');
if(menuEl != null) {
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
}

function followLink(loc) {
    var path = window.location.pathname;
    console.log(path);
    if (path.indexOf("/") !== -1) {
        path = path.substring(0, path.length - 1);
    }
    console.log(path);
    if (path.lastIndexOf("/") !== -1) {
        path = path.substring(0, path.lastIndexOf("/"));
    }
    console.log(path);
    window.location = window.location.protocol + "//" + window.location.hostname + ":" + window.location.port + path + loc;
}
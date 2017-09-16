function usersTab() {
    document.getElementById("tab-users").classList.add("mdc-tab--active");
    document.getElementById("tab-playlist").classList.remove("mdc-tab--active");
    document.getElementById("panel-users").classList.add("active");
    document.getElementById("panel-playlist").classList.remove("active");
}

function playlistTab() {
    document.getElementById("tab-users").classList.remove("mdc-tab--active");
    document.getElementById("tab-playlist").classList.add("mdc-tab--active");
    document.getElementById("panel-users").classList.remove("active");
    document.getElementById("panel-playlist").classList.add("active");
}
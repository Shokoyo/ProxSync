let menuSearch = new mdc.menu.MDCSimpleMenu(document.querySelector('#search-menu'));
const snackbar = mdc.snackbar.MDCSnackbar.attachTo(document.querySelector('.mdc-snackbar'));

function doSearch(keyword, old) {
    if (keyword === old && keyword != "") {
        var request = new XMLHttpRequest();
        keyword = encodeURI(keyword);
        request.open("GET", "?search=" + keyword + "&uid=" + uid);
        request.addEventListener('load', function (event) {
            if (request.status >= 200 && request.status < 300) {
                document.getElementById("mdc-search-list").innerHTML = buildHtmlListSearch(JSON.parse(request.responseText));
            } else {
                console.warn(request.statusText, request.responseText);
            }
        });
        request.send();
    } else {
        $("#mdc-search-list").animate({scrollTop: 0}, "fast");
        $('#mdc-search-list').addClass("hidden");
    }
}

function blurredSearch() {
    if (!menuSearch.open) {
        console.log("resetting search");
        document.getElementById("tf-box-search-field").value = '';
        $('#tf-box-search-field').blur();
    } else {
        $('#tf-box-search-field').focus();
        console.log("search menu is open!");
    }
}

$(document).on('click', function (e) {
    if ($(e.target).closest("#mdc-search-list").length === 0 && $(e.target).closest("#tf-box-search").length === 0) {
        menuSearch.open = false;
        blurredSearch();
    } else if ($(e.target).closest("#tf-box-search").length === 0) {
        // TODO: do not close when clicked
        inSearchField = true;
        menuSearch.open = true;
    } else {
        inSearchField = false;
    }
});

$('#tf-box-search-field').on('input', function () {
    var old = document.getElementById("tf-box-search-field").value;
    setTimeout(function () {
        doSearch(document.getElementById("tf-box-search-field").value, old);
    }, 400);
});

function buildHtmlListSearch(resultList) {
    var res = "";
    for (var i = 0; i < resultList.length; i++) {
        var key = resultList[i].link;
        key = key.substring(key.lastIndexOf('/') + 1);
        res = res + "<li class='mdc-list-item list-item-search' role='menuitem' aria-disabled=\"true\">" +
            "<img style='height:78px;padding-right:14px;' src='" + resultList[i].image + "' role='presentation'/>";
        res += "<span class='mdc-list-item__text'><span style='z-index:5;'><span style='vertical-align: middle'>" + resultList[i].title + " </span>";
        res += "<i title='Plan to Watch' class='mdc-list-item__text material-icons' style='vertical-align: middle' onclick='addPlanToWatch(\"" + key + "\",\"" + resultList[i].image + "\",\"" + resultList[i].title + "\",\"" + resultList[i].episodeCount + "\");'>schedule</i></span>";
        res += "<span class='episode-text-span'>";
        if (resultList[i].episodeCount > 0) {
            for (var j = 1; j <= resultList[i].episodeCount; j++) {
                res += "<a href='#' class='text square-box mdc-toolbar__icon";
                if (resultList[i].watchlist + 1 > j) {
                    res += " mdc-theme--secondary-dark-bg'";
                } else {
                    if (j > resultList[i].lastEpisode) {
                        res += " mdc-theme--primary-bg'";
                    } else {
                        res += " mdc-theme--secondary-bg'";
                    }
                }
                res += "onclick='addSearchEpisodeToPlaylist(event,\"" + resultList[i].link + "\"," + j + ");'>" +
                    "<b> " +
                    (j).pad(resultList[i].episodeCount.toString().length) +
                    " </b></a>";
                if (j % 10 === 0) {
                    res += "<br/>";
                }
            }
        } else if (resultList[i].episodeCount === "?") {
            for (var j = 1; j <= resultList[i].lastEpisode; j++) {
                res += "<a href='#' class='text square-box mdc-toolbar__icon";
                if (resultList[i].watchlist + 1 > j) {
                    res += " mdc-theme--secondary-dark-bg'";
                } else {
                    if (j > resultList[i].lastEpisode) {
                        res += " mdc-theme--primary-bg'";
                    } else {
                        res += " mdc-theme--secondary-bg'";
                    }
                }
                res += "onclick='addSearchEpisodeToPlaylist(event,\"" + resultList[i].link + "\"," + j + ");'>" +
                    "<b> " +
                    (j).pad(resultList[i].episodeCount.toString().length) +
                    " </b></a>";
                if (j % 10 === 0) {
                    res += "<br/>";
                }
            }
            res += "<a href='#' class='text square-box mdc-toolbar__icon";
            res += " mdc-theme--primary-bg'";
            res += "><b> ? </b></a>";
        }
        else {
            res += "<a href='#' class='text square-box mdc-toolbar__icon  mdc-theme--secondary-bg' " +
                "onclick='addSearchResultToPlaylist(\"" + resultList[i].link + "\");'>" +
                "<b>" +
                "load" +
                "</b></a>";
        }
        res += "</span></span>";
        if (i != resultList.length - 1) {
            res = res + "</li>" +
                "<hr class=\"mdc-list-divider\">";
        }
    }
    if (res === "") {
        menuSearch.open = false;
    } else {
        menuSearch.open = true;
    }
    $("#mdc-search-list").animate({scrollTop: 0}, "fast");
    if (res === "") {
        $('#mdc-search-list').addClass("hidden");
    } else {
        $('#mdc-search-list').removeClass("hidden");
        setTimeout(function () {
            document.getElementById("tf-box-search-field").focus();
        }, 80);
    }
    if (document.getElementById("tf-box-search-field").value === "") {
        $('#mdc-search-list').addClass("hidden");
    }
    return res;
}

Number.prototype.pad = function (size) {
    size = Math.max(size, 2);
    var s = String(this);
    while (s.length < (size || 2)) {
        s = "0" + s;
    }
    return s;
};

function addPlanToWatch(key, poster, title, episodeCount) {
    /*var entry = {
        episode: "0",
        episodeCount: episodeCount,
        key: key,
        poster: poster,
        rating: 0,
        status: "planned",
        title: title
    };
    var ref = firebase.database().ref("users/" + uid + "/watchlist/" + key.replace(".", "-"));
    ref.once('value', function (snapshot) {
        const dataObj = {
            message: "Successfully added " + title + " to your watchlist"
        };
        snackbar.show(dataObj);
        document.getElementsByClassName("mdc-snackbar")[0].classList.add("mdc-snackbar--active");
        setTimeout(function () {
            document.getElementsByClassName("mdc-snackbar")[0].classList.remove("mdc-snackbar--active");
        }, 2750);
    });
    ref.set(entry);*/
}
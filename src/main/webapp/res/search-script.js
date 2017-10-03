function doSearch(keyword) {
    var request = new XMLHttpRequest();
    keyword = encodeURI(keyword);
    request.open("GET","?search=" + keyword);
    request.addEventListener('load', function(event) {
        if (request.status >= 200 && request.status < 300) {
            console.log(request.responseText);
        } else {
            console.warn(request.statusText, request.responseText);
        }
    });
    request.send();
}
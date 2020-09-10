function httpRequest(mode, url, data = 0) {
	var baseUrl = window.location;

    $.ajax({
    	type : mode,
		url : baseUrl + "/" + url,
		data : "data=" + encodeURIComponent(data)
    });
    return false;
}

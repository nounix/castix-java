function myFunction(img, vid, info) {
    var table = document.getElementById("table");
    var tr = document.createElement("TR");
    var tdImg = document.createElement("TD");
    var tdInfo = document.createElement("TD");
    var imgE = document.createElement("IMG");
    tr.className = "ytSearchRows";
    imgE.setAttribute("src", img);
    imgE.onclick = function() { httpRequest("GET", "play/" + vid, vid); };
    table.appendChild(tr);
    tr.appendChild(tdImg);
    tr.appendChild(tdInfo);
    tdImg.appendChild(imgE);
    tdInfo.appendChild(document.createTextNode(info));
}

function httpRequest(mode, url, data = 0) {
    $('.ytSearchRows').remove()

    $.ajax({
    	type : mode,
		url : window.location + "/" + url,
        data : "data=" + encodeURIComponent(data.split(' ').join('+')),
        success: function(response){
            for (let i = 0; i < response.urls.length; i++) {
                myFunction(response.imgs[i], response.urls[i], response.infos[i]);
            }
	    }
    });
    return false;
}

$(document).ready(function() {
    $("#searchButton").click(function(e) {
        $(".response").hide();
        $(".member").remove();
        $(".iframeDiv").remove();
        if ($("#searchLimit").val() == '') {
            var limit = 20;
        } else {
            var limit = parseInt($("#searchLimit").val());
        }

        $.ajax({
            type: "GET",
            url: "/etc/eurail/videolibrary.json/" + document.querySelector('input[name=keyword]').value + "/" + limit + ".json",
            headers: { "X-Api-Key": "a2815066-2486-4d5d-a191-b6a798d29451" },
            success: function(resp) {
                $(".response").show();

                document.getElementsByClassName('youtubeTitle')[0].innerHTML = resp.youtubeTitle;
                document.getElementsByClassName('youtubeDescription')[0].innerHTML = resp.youtubeDescription;
                document.getElementsByClassName('imdbDescription')[0].innerHTML = resp.imdbDescription;
                document.getElementsByClassName('imdbTitle')[0].innerHTML = resp.imdbTitle;
                var is = $("#iframeTemplate")[0].innerHTML.trim();
                var iframeholder = document.createElement('div');
                iframeholder.innerHTML = is;
                var iframetemplate = iframeholder.childNodes;
                var iframemember = document.getElementById('iframemembers');
                var iframenewItem = $(iframetemplate).clone();
                $(iframenewItem).find(".imdbVideo").attr("src", resp.imdbTrailerLink);
                $(".iframemembers").append(iframenewItem);
                document.getElementsByClassName('youtubeTitle')[0].innerHTML = resp.youtubeTitle;
                $("#imageHref").attr("src", resp.yutubeThumbnailLink);
                $(".response a").attr("href", resp.youtubeHref);
                console.log(resp);
                append_json(JSON.parse(resp.youtubeResponse).items);
            }
        });
    });
    $(".response").hide();
});

function append_json(data) {
    //Set Up the template
    var s = $("#postTemplate")[0].innerHTML.trim();
    var holder = document.createElement('div');
    holder.innerHTML = s;
    var template = holder.childNodes;

    var member = document.getElementById('newmembers');
    for (var i = 0; i < data.length; i++) {
        //Clone Template
        var newItem = $(template).clone();
        if ($("#searchLimit").val() != '') {
            if ($(".member").length == parseInt($("#searchLimit").val())) {
                return true;
            }
        }

        //Populate it
        $(newItem).find(".type").html(data[i].snippet.title);
        $(newItem).find(".year").html($(newItem).find(".year").html() + " " + data[i].snippet.description);
        $(newItem).find(".iframeVideo").attr("src", "https://www.youtube.com/embed/" + data[i].id.videoId);
        var img = $(newItem).find(".thumb")
        $(img).attr("src", data[i].memimgsrc).attr("alt", $(img).attr("alt") + data[i].membername + " " + "etc")
            .attr("title", $(img).attr("title") + data[i].membername + " finish this off");
        $(newItem).find(".membername").html(data[i].membername);
        $(newItem).find(".group").html(data[i].group);
        $(newItem).find(".company").html(data[i].company);
        $(newItem).find(".classification").html(data[i].classification);
        //Append it
        $(".newmembers").append(newItem);
    }
}
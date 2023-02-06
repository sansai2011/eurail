package com.eurail.core.services.impl;

import com.eurail.core.beans.VideoLibraryBean;
import com.eurail.core.services.IMDBConfigService;
import com.eurail.core.services.VideoLibraryService;
import com.eurail.core.services.YouTubeConfigService;
import com.eurail.core.utils.CommonUtil;
import com.eurail.core.utils.QueryString;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Component(
        immediate = true, service = {VideoLibraryService.class},
        property = {
                Constants.SERVICE_DESCRIPTION + "= For configuring YouTube",
                Constants.SERVICE_VENDOR + "= eurail"
        })
public class VideoLibraryServiceImpl implements VideoLibraryService {

    @Reference
    private transient YouTubeConfigService youTubeConfigService;

    @Reference
    private transient IMDBConfigService imdbConfigService;

    private static final String YOUTUBE_LINK = "https://www.youtube.com/embed/";
    private VideoLibraryBean videoLibraryBean;
    JsonObject videoJsonObject = new JsonObject();

    @Override
    public JsonObject getVideoLibraryJsonObject(String keyword, String limit) {
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");

            videoLibraryBean = new VideoLibraryBean();

            QueryString qs = new QueryString("part", "snippet");
            qs.add("q", keyword.replace(" ", "+"));
            qs.add("topicId", "/m/02vxn");
            qs.add("maxResults", limit);
            qs.add("key", youTubeConfigService.youTubeApiKey());

            String videoApiUrl = youTubeConfigService.getYouTubeApiURL() + qs;
            JsonObject youtubeJsonObject;
            youtubeJsonObject = JsonParser.parseString(CommonUtil.PerformGetRequest(videoApiUrl)).getAsJsonObject();

            if (youtubeJsonObject.getAsJsonArray("items").size() > 0) {
                parseYoutubeData(youtubeJsonObject);
            }

            String imdbApiUrl = imdbConfigService.getImdbApiURL() + "SearchMovie/" + imdbConfigService.getApiKey() + "/" + keyword.replace("+", "%20");
            JsonObject IMDBJsonObject;
            IMDBJsonObject = JsonParser.parseString(CommonUtil.PerformGetRequest(imdbApiUrl)).getAsJsonObject();

            if (IMDBJsonObject.get("results").getAsJsonArray().size() > 0)
                parseIMDBDate(IMDBJsonObject);
            if (videoJsonObject == null) {
                videoJsonObject.addProperty("error", "Error in response");
                log.error("API Response error");
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Unsupported Encoding Format:", e);
        }
        return videoJsonObject;
    }

    private void parseYoutubeData(JsonObject youtubeJsonObject) {
        if (youtubeJsonObject != null) {
            try {
                videoJsonObject.addProperty("youtubeHref", YOUTUBE_LINK + youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString());
                videoLibraryBean.setVideoHref(YOUTUBE_LINK + youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("id").getAsJsonObject().get("videoId").getAsString());
                videoLibraryBean.setVideoTitle(youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
                videoLibraryBean.setVideoThumbnail(youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("high").getAsJsonObject().get("url").getAsString());
                videoJsonObject.addProperty("youtubeTitle", youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("snippet").getAsJsonObject().get("title").getAsString());
                videoJsonObject.addProperty("youtubeDescription", youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("snippet").getAsJsonObject().get("description").getAsString());
                videoJsonObject.addProperty("yutubeThumbnailLink", youtubeJsonObject.getAsJsonArray("items").get(0).getAsJsonObject().get("snippet").getAsJsonObject().get("thumbnails").getAsJsonObject().get("high").getAsJsonObject().get("url").getAsString());
                videoJsonObject.addProperty("youtubeResponse", youtubeJsonObject.toString());
            } catch (Exception e) {
                log.error("Error in the YouTube API response parsing {}", e);
            }
        }
    }

    private void parseIMDBDate(JsonObject IMDBJsonObject) {
        if (IMDBJsonObject != null) {
            try {
                videoJsonObject.addProperty("imdbResponse", IMDBJsonObject.toString());
                videoJsonObject.addProperty("imdbImage", IMDBJsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("image").getAsString());
                videoJsonObject.addProperty("imdbDescription", IMDBJsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString());
                videoJsonObject.addProperty("imdbTitle", IMDBJsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("title").getAsString());
                getTrailerVideo(IMDBJsonObject.get("results").getAsJsonArray().get(0).getAsJsonObject().get("id").getAsString());
            } catch (Exception e) {
                log.error("Error in the IMDB API response parsing {}", e);
            }
        }
    }

    private void getTrailerVideo(String trailerID) {
        String imdbAPIURL = imdbConfigService.getImdbApiURL();
        String imdbApiUrl = imdbAPIURL + "Trailer/" + imdbConfigService.getApiKey() + "/" + trailerID;
        JsonObject trailerJsonObject;
        trailerJsonObject = JsonParser.parseString(CommonUtil.PerformGetRequest(imdbApiUrl)).getAsJsonObject();
        videoJsonObject.addProperty("imdbTrailerLink", trailerJsonObject.get("linkEmbed").getAsString());
    }
}




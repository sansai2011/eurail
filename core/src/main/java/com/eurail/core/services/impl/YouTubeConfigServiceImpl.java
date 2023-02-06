package com.eurail.core.services.impl;

import com.eurail.core.services.YouTubeConfigService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = YouTubeConfigServiceImpl.Config.class)
@Component(service = YouTubeConfigService.class, immediate = true)
public class YouTubeConfigServiceImpl implements YouTubeConfigService {

    private String youTubeApiURL;
    private String youTubeApiKey;

    @ObjectClassDefinition(name = "Eurail YouTube Configurator service", description = "Eurail YouTube Configurator Service")
    public static @interface Config {

        @AttributeDefinition(name = "YouTube API URL", description = "Enter the YouTube API URL")
        String youTubeApiURL() default "https://www.googleapis.com/youtube/v3/search?";

        @AttributeDefinition(name = "YouTube API Key", description = "Enter the YouTube API Key")
        String youTubeApiKey() default "AIzaSyBKQiUrCyjStuV1Pv31gKrwJeMgzh055Vg";
    }

    @Activate
    protected void activate(final Config config) {
        youTubeApiURL = config.youTubeApiURL();
        youTubeApiKey = config.youTubeApiKey();
    }

    @Override
    public String getYouTubeApiURL() {
        return youTubeApiURL;
    }

    @Override
    public String youTubeApiKey() {
        return youTubeApiKey;
    }
}

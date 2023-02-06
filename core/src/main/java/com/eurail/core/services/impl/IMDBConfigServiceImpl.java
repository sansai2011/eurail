package com.eurail.core.services.impl;

import com.eurail.core.services.IMDBConfigService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@Designate(ocd = IMDBConfigServiceImpl.Config.class)
@Component(service = IMDBConfigService.class, immediate = true)
public class IMDBConfigServiceImpl implements IMDBConfigService {

    public String imdbApiURL;
    public String imdbApiKey;

    @ObjectClassDefinition
    public static @interface Config {

        @AttributeDefinition(name = "IMDB API URL", description = "Enter the IMDB API URL")
        String imdbApiURL() default "https://imdb-api.com/en/API/";

        @AttributeDefinition(name = "IMDB API Key", description = "Enter the IMDB API Key")
        String imdbApiKey() default "k_67q29k8r";
    }

    @Activate
    protected void activate(Config config) {
        imdbApiURL = config.imdbApiURL();
        imdbApiKey = config.imdbApiKey();
    }

    @Override
    public String getImdbApiURL() {
        return imdbApiURL;
    }

    @Override
    public String getApiKey() {
        return imdbApiKey;
    }
}

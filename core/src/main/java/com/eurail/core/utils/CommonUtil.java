package com.eurail.core.utils;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class CommonUtil {

    public static String PerformGetRequest(String apiURL) {
        String getResponse = "";
        URI uri = null;
        JsonObject jsonObject = null;
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            uri = new URI(apiURL);
            HttpRequestBase method = new HttpGet(uri);
            CloseableHttpResponse response = client.execute(method);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    getResponse = EntityUtils.toString(entity);
                }
            } else {
                getResponse = "Error in the API response";
                log.error("Error in the API response , Status Code is -- {}", response.getStatusLine().getStatusCode());
            }
        } catch (URISyntaxException | IOException e) {
            log.error("error calling video api:", e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                log.error("error closing httpclient:", e);
            }
        }
        return getResponse;
    }
}

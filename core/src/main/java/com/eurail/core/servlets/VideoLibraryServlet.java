package com.eurail.core.servlets;

import com.eurail.core.services.VideoLibraryService;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.eclipse.jetty.http.HttpStatus;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

@Slf4j
@Component(service = Servlet.class, immediate = true,
        property = {
                "description" + "=Video Library Servlet", "sling.servlet.methods=" + "GET", "sling.servlet.extensions=" + "json",
                "sling.servlet.paths=" + "/etc/eurail/videolibrary"
        })
public class VideoLibraryServlet extends SlingAllMethodsServlet {

    private static final long serialVersionUID = 1L;

    @Reference
    private VideoLibraryService videoLibraryService;

    @Override
    protected void doGet(final SlingHttpServletRequest req,
                         final SlingHttpServletResponse resp) {

        final String API_KEY = "a2815066-2486-4d5d-a191-b6a798d29451";

        try {
            String suffix = req.getRequestPathInfo().getSuffix();
            String keyword = suffix.split("/")[1];
            String limit = suffix.split(".json")[0].split("/")[2];
            String apiKey = req.getHeader("X-Api-Key");

            if (!apiKey.equalsIgnoreCase(API_KEY) || keyword == null) {
                resp.setStatus(HttpStatus.NOT_ACCEPTABLE_406);
            }

            JsonObject jsonObject = null;
            jsonObject = videoLibraryService.getVideoLibraryJsonObject(keyword, limit);

            resp.setHeader("Cache-Control", "public");
            resp.setHeader("Cache-Control", "max-age=86400");

            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf-8");

            resp.getWriter().write(jsonObject.toString());

        } catch (IOException e) {
            log.error("IOException:: VideoLibraryServlet() :: doGet {0}", e);
        }
    }
}

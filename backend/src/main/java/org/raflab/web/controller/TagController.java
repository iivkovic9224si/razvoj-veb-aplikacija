package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.service.TagService;
import static spark.Spark.*;

public class TagController {
    private final TagService tagService = new TagService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        get("/api/tagovi", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(tagService.getAll(page));
        });
    }
}
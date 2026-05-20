package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.KomentarRequest;
import org.raflab.web.service.KomentarService;
import static spark.Spark.*;

public class KomentarController {
    private final KomentarService komentarService = new KomentarService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        get("/api/vesti/:id/komentari", (req, res) -> {
            int vestId = Integer.parseInt(req.params(":id"));
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(komentarService.getByVest(vestId, page));
        });

        post("/api/komentari", (req, res) -> {
            KomentarRequest reqDto = gson.fromJson(req.body(), KomentarRequest.class);
            komentarService.addComment(reqDto);
            res.status(201);
            return gson.toJson("Komentar dodat");
        });

        post("/api/komentari/:id/like", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            komentarService.likeComment(id);
            return gson.toJson("Komentar uspesno lajkovan");
        });

        post("/api/komentari/:id/dislike", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            komentarService.dislikeComment(id);
            return gson.toJson("Komentar uspesno dislajkovan");
        });
    }
}
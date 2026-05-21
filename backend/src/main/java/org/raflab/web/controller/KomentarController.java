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
            try {
                int vestId = Integer.parseInt(req.params(":id"));
                int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
                // Vraća PagedResponse sa podacima i brojem strana
                return gson.toJson(komentarService.getByVest(vestId, page));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            }
        });

        post("/api/komentari", (req, res) -> {
            try {
                KomentarRequest reqDto = gson.fromJson(req.body(), KomentarRequest.class);
                komentarService.addComment(reqDto);
                res.status(201);
                return gson.toJson("Komentar dodat");
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            }
        });

        post("/api/komentari/:id/like", (req, res) -> {
            komentarService.likeComment(Integer.parseInt(req.params(":id")));
            return gson.toJson("OK");
        });

        post("/api/komentari/:id/dislike", (req, res) -> {
            komentarService.dislikeComment(Integer.parseInt(req.params(":id")));
            return gson.toJson("OK");
        });
    }
}
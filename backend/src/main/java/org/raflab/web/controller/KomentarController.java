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

        // LAJK KOMENTARA SA KONTROLOM SESIJE
        post("/api/komentari/:id/like", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                var session = req.session(true);
                String sessionKey = "voted_komentar_" + id;

                // Provera da li je već glasao (isto kao kod vesti)
                if (session.attribute(sessionKey) != null) {
                    res.status(400);
                    return gson.toJson("Već ste dali svoj glas za ovaj komentar!");
                }

                komentarService.likeComment(id);
                session.attribute(sessionKey, "LIKE");
                return gson.toJson("Komentar uspešno lajkovan");
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            }
        });

        // DISLAJK KOMENTARA SA KONTROLOM SESIJE
        post("/api/komentari/:id/dislike", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                var session = req.session(true);
                String sessionKey = "voted_komentar_" + id;

                // Provera da li je već glasao
                if (session.attribute(sessionKey) != null) {
                    res.status(400);
                    return gson.toJson("Već ste dali svoj glas za ovaj komentar!");
                }

                komentarService.dislikeComment(id);
                session.attribute(sessionKey, "DISLIKE");
                return gson.toJson("Komentar uspešno dislajkovan");
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            }
        });
    }
}
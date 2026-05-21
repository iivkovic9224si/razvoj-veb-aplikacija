package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.service.VestService;
import static spark.Spark.*;

public class VestController {
    private final VestService vestService = new VestService();
    private final Gson gson = new Gson();

    public void registerRoutes() {

        get("/api/vesti", (req, res) -> {
            Integer katId = req.queryParams("kategorija") != null ? Integer.parseInt(req.queryParams("kategorija")) : null;
            String tag = req.queryParams("tag");
            String search = req.queryParams("search");
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(vestService.getFiltered(katId, tag, search, page));
        });

        get("/api/vesti/najcitanije", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(vestService.getMostRead(page));
        });

        // NOVO: Top reakcije (sidebar)
        get("/api/vesti/top-reakcije", (req, res) -> {
            return gson.toJson(vestService.getTopReactions());
        });

        // NOVO: Povezane vesti (ispod same vesti)
        get("/api/vesti/:id/povezane", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            return gson.toJson(vestService.getRelated(id));
        });

        get("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            var session = req.session(true);
            String sessionKey = "viewed_vest_" + id;
            boolean vecPosetio = session.attribute(sessionKey) != null;

            var v = vestService.getByIdWithVisitLogic(id, vecPosetio);
            if (v == null) { res.status(404); return gson.toJson("Vest ne postoji"); }
            if (!vecPosetio) session.attribute(sessionKey, true);
            return gson.toJson(v);
        });

        // CMS RUTE - Sa autorizacijom
        post("/api/vesti", (req, res) -> {
            VestRequest vr = gson.fromJson(req.body(), VestRequest.class);
            int autorId = req.attribute("trenutniKorisnikId");
            vestService.create(vr, autorId);
            res.status(201);
            return gson.toJson("Vest uspesno kreirana");
        });

        put("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            VestRequest vr = gson.fromJson(req.body(), VestRequest.class);
            int userId = req.attribute("trenutniKorisnikId");
            String userTip = req.attribute("trenutniKorisnikTip");

            vestService.update(id, vr, userId, userTip);
            return gson.toJson("Vest uspesno izmenjena");
        });

        delete("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            int userId = req.attribute("trenutniKorisnikId");
            String userTip = req.attribute("trenutniKorisnikTip");

            vestService.delete(id, userId, userTip);
            return gson.toJson("Vest uspesno obrisana");
        });

        // REAKCIJE
        post("/api/vesti/:id/like", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            var session = req.session(true);
            if (session.attribute("voted_vest_" + id) != null) throw new RuntimeException("Vec ste glasali!");
            vestService.likeVest(id);
            session.attribute("voted_vest_" + id, "LIKE");
            return gson.toJson("Lajkovano");
        });

        post("/api/vesti/:id/dislike", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            var session = req.session(true);
            if (session.attribute("voted_vest_" + id) != null) throw new RuntimeException("Vec ste glasali!");
            vestService.dislikeVest(id);
            session.attribute("voted_vest_" + id, "DISLIKE");
            return gson.toJson("Dislajkovano");
        });
    }
}
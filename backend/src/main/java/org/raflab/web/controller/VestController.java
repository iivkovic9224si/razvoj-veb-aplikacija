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

        get("/api/vesti/top-reakcije", (req, res) -> {
            res.type("application/json");
            return gson.toJson(vestService.getTopByReactions());
        });

        get("/api/vesti/:id/povezane", (req, res) -> {
            res.type("application/json");

            int id = Integer.parseInt(req.params(":id"));

            return gson.toJson(vestService.getRelatedByTags(id));
        });

        get("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            var session = req.session(true);
            String sessionKey = "viewed_vest_" + id;
            boolean vecPosetio = session.attribute(sessionKey) != null;

            var v = vestService.getByIdWithVisitLogic(id, vecPosetio);
            if (v == null) {
                res.status(404);
                return gson.toJson("Vest ne postoji");
            }

            if (!vecPosetio) {
                session.attribute(sessionKey, true);
            }
            return gson.toJson(v);
        });

        // ==========================================
        // CMS RUTE (ZAHTEVAJU JWT TOKEN - AUTORIZACIJA)
        // ==========================================

        post("/api/vesti", (req, res) -> {
            VestRequest vr = gson.fromJson(req.body(), VestRequest.class);
            int autorId = req.attribute("trenutniKorisnikId");
            vestService.create(vr, autorId);
            res.status(201);
            return gson.toJson("Vest uspesno kreirana");
        });

        put("/api/vesti/:id", (req, res) -> {
            VestRequest vr = gson.fromJson(req.body(), VestRequest.class);
            int id = Integer.parseInt(req.params(":id"));
            vestService.update(id, vr);
            return gson.toJson("Vest uspesno izmenjena");
        });

        delete("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            vestService.delete(id);
            return gson.toJson("Vest uspesno obrisana");
        });

        // ==========================================
        // REAKCIJE (LAJK / DISLAJK SA KONTROLOM SESIJE)
        // ==========================================

        post("/api/vesti/:id/like", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            var session = req.session(true);
            String sessionKey = "voted_vest_" + id;

            String prethodniGlas = session.attribute(sessionKey);

            vestService.changeVoteToLike(id, prethodniGlas);

            session.attribute(sessionKey, "LIKE");

            return gson.toJson("Vest uspesno lajkovana");
        });

        post("/api/vesti/:id/dislike", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            var session = req.session(true);
            String sessionKey = "voted_vest_" + id;

            String prethodniGlas = session.attribute(sessionKey);

            vestService.changeVoteToDislike(id, prethodniGlas);

            session.attribute(sessionKey, "DISLIKE");

            return gson.toJson("Vest uspesno dislajkovana");
        });
    }
}
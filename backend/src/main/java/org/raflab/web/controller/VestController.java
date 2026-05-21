package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.service.VestService;
import static spark.Spark.*;

public class VestController {
    private final VestService vestService = new VestService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // LISTANJE I PRETRAGA
        get("/api/vesti", (req, res) -> {
            String q = req.queryParams("query");
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            if (q != null && !q.isEmpty()) return gson.toJson(vestService.search(q, page));
            return gson.toJson(vestService.getAll(page));
        });

        get("/api/vesti/:id", (req, res) -> {
            var vest = vestService.getById(Integer.parseInt(req.params(":id")));
            if (vest == null) { res.status(404); return gson.toJson("Vest nije pronadjena"); }
            return gson.toJson(vest);
        });

        // CMS OPERACIJE
        post("/api/vesti", (req, res) -> {
            VestRequest vestReq = gson.fromJson(req.body(), VestRequest.class);
            int autorId = req.attribute("trenutniKorisnikId"); // Iz JWT Filter-a
            vestService.create(vestReq, autorId);
            res.status(201);
            return gson.toJson("Vest uspesno kreirana");
        });

        put("/api/vesti/:id", (req, res) -> {
            VestRequest vestReq = gson.fromJson(req.body(), VestRequest.class);
            vestService.update(Integer.parseInt(req.params(":id")), vestReq);
            return gson.toJson("Vest uspesno izmenjena");
        });

        delete("/api/vesti/:id", (req, res) -> {
            vestService.delete(Integer.parseInt(req.params(":id")));
            return gson.toJson("Vest uspesno obrisana");
        });

        // REAKCIJE
        post("/api/vesti/:id/like", (req, res) -> {
            vestService.likeVest(Integer.parseInt(req.params(":id")));
            return gson.toJson("OK");
        });

        post("/api/vesti/:id/dislike", (req, res) -> {
            vestService.dislikeVest(Integer.parseInt(req.params(":id")));
            return gson.toJson("OK");
        });
    }
}
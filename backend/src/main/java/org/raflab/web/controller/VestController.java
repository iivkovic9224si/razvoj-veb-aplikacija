package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.service.VestService;
import static spark.Spark.*;

public class VestController {
    private final VestService vestService = new VestService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // UNIVERZALNA RUTA ZA LISTANJE, PRETRAGU I FILTERE
        get("/api/vesti", (req, res) -> {
            Integer katId = req.queryParams("kategorija") != null ? Integer.parseInt(req.queryParams("kategorija")) : null;
            String tag = req.queryParams("tag");
            String search = req.queryParams("search");
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;

            return gson.toJson(vestService.getFiltered(katId, tag, search, page));
        });

        get("/api/vesti/najcitanije", (req, res) -> gson.toJson(vestService.getMostRead()));

        get("/api/vesti/:id", (req, res) -> {
            var v = vestService.getById(Integer.parseInt(req.params(":id")));
            return v != null ? gson.toJson(v) : halt(404, "Vest ne postoji");
        });

        // CMS (ZAHTEVA JWT TOKEN)
        post("/api/vesti", (req, res) -> {
            VestRequest vr = gson.fromJson(req.body(), VestRequest.class);
            int autorId = req.attribute("trenutniKorisnikId");
            vestService.create(vr, autorId);
            return gson.toJson("Kreirano");
        });

        put("/api/vesti/:id", (req, res) -> {
            VestRequest vr = gson.fromJson(req.body(), VestRequest.class);
            vestService.update(Integer.parseInt(req.params(":id")), vr);
            return gson.toJson("Izmenjeno");
        });

        delete("/api/vesti/:id", (req, res) -> {
            vestService.delete(Integer.parseInt(req.params(":id")));
            return gson.toJson("Obrisano");
        });

        post("/api/vesti/:id/like", (req, res) -> { vestService.likeVest(Integer.parseInt(req.params(":id"))); return "OK"; });
        post("/api/vesti/:id/dislike", (req, res) -> { vestService.dislikeVest(Integer.parseInt(req.params(":id"))); return "OK"; });
    }
}
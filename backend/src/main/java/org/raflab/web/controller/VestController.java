package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.dto.response.VestResponse;
import org.raflab.web.service.VestService;
import static spark.Spark.*;

public class VestController {
    private final VestService vestService = new VestService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        get("/api/vesti", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(vestService.getAll(page));
        });

        get("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));

            // Pridobijamo ili kreiramo sesiju neulogovanog/ulogovanog posetioca
            var session = req.session(true);
            // Kreiramo jedinstveni ključ za sesiju, npr. "viewed_vest_1"
            String sessionKey = "viewed_vest_" + id;

            boolean vecPosetio = session.attribute(sessionKey) != null;

            VestResponse vest = vestService.getByIdWithVisitLogic(id, vecPosetio);

            if (vest == null) {
                res.status(404);
                return gson.toJson("Vest nije pronadjena");
            }

            // Ako nije posetio do sada, zabeleži u sesiji da jeste da mu sledeći put ne uvećava broj
            if (!vecPosetio) {
                session.attribute(sessionKey, true);
            }

            return gson.toJson(vest);
        });

        post("/api/vesti/:id/like", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            var session = req.session(true);
            String sessionKey = "voted_vest_" + id;

            if (session.attribute(sessionKey) != null) {
                throw new RuntimeException("Vec ste dali svoj glas (lajk/dislajk) za ovu vest!");
            }

            vestService.likeVest(id);
            session.attribute(sessionKey, "LIKE");
            return gson.toJson("Vest uspesno lajkovana");
        });

        post("/api/vesti/:id/dislike", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            var session = req.session(true);
            String sessionKey = "voted_vest_" + id;

            if (session.attribute(sessionKey) != null) {
                throw new RuntimeException("Vec ste dali svoj glas (lajk/dislajk) za ovu vest!");
            }

            vestService.dislikeVest(id);
            session.attribute(sessionKey, "DISLIKE");
            return gson.toJson("Vest uspesno dislajkovana");
        });

        // Ruta za pretragu vesti: npr. /api/vesti/search?query=Java&page=1
        get("/api/vesti/search", (req, res) -> {
            String query = req.queryParams("query");
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(vestService.search(query, page));
        });

// Ruta za najčitanije vesti: /api/vesti/najcitanije
        get("/api/vesti/najcitanije", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(vestService.getMostRead(page));
        });

// Rute za izmenu i brisanje koje ti fale za CMS deo aplikacije:
        put("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            VestRequest vestReq = gson.fromJson(req.body(), VestRequest.class);
            vestService.update(id, vestReq);
            return gson.toJson("Vest uspesno izmenjena");
        });

        delete("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            vestService.delete(id);
            return gson.toJson("Vest uspesno obrisana");
        });
    }
}
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
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(vestService.getAll(page));
        });

        get("/api/vesti/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            var vest = vestService.getById(id);
            if (vest == null) {
                res.status(404);
                return gson.toJson("Vest nije pronadjena");
            }
            return gson.toJson(vest);
        });

        post("/api/vesti", (req, res) -> {
            VestRequest vestReq = gson.fromJson(req.body(), VestRequest.class);
            // Ovde bi autorId trebalo da ide iz JWT-a, za sad stavljamo fiksno 1
            vestService.create(vestReq, 1);
            res.status(201);
            return gson.toJson("Vest uspesno kreirana");
        });
    }
}
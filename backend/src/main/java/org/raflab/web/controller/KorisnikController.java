package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.KorisnikRequest;
import org.raflab.web.service.KorisnikService;
import static spark.Spark.*;

public class KorisnikController {
    private final KorisnikService korisnikService = new KorisnikService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        post("/api/login", (req, res) -> {
            KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
            var responseDto = korisnikService.login(requestDto);
            if (responseDto == null) {
                res.status(401);
                return gson.toJson("Greska: Neispravni podaci");
            }
            return gson.toJson(responseDto);
        });

        get("/api/korisnici", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(korisnikService.findAll(page));
        });
    }
}
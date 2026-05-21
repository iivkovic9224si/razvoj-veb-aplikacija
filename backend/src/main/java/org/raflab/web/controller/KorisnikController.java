package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.KorisnikRequest;
import org.raflab.web.service.KorisnikService;
import static spark.Spark.*;

public class KorisnikController {
    private final KorisnikService korisnikService = new KorisnikService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // JAVNO
        post("/api/login", (req, res) -> {
            KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
            String token = org.raflab.web.auth.AuthService.login(requestDto.getEmail(), requestDto.getLozinka());

            if (token == null) { res.status(401); return gson.toJson("Greska: Neispravni podaci"); }
            if (token.equals("DEAKTIVIRAN")) { res.status(403); return gson.toJson("Greska: Nalog je deaktiviran"); }

            var korisnikResponse = korisnikService.login(requestDto);
            korisnikResponse.setJwt(token);
            return gson.toJson(korisnikResponse);
        });

        // ADMIN RUTE
        get("/api/korisnici", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(korisnikService.findAll(page));
        });

        get("/api/korisnici/:id", (req, res) -> {
            return gson.toJson(korisnikService.getById(Integer.parseInt(req.params(":id"))));
        });

        post("/api/korisnici", (req, res) -> {
            KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
            korisnikService.addKorisnik(requestDto);
            res.status(201);
            return gson.toJson("Korisnik uspešno kreiran.");
        });

        put("/api/korisnici/:id", (req, res) -> {
            KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
            korisnikService.updateKorisnik(Integer.parseInt(req.params(":id")), requestDto);
            return gson.toJson("Korisnik uspešno izmenjen.");
        });

        post("/api/korisnici/:id/status", (req, res) -> {
            korisnikService.toggleStatus(Integer.parseInt(req.params(":id")));
            return gson.toJson("Status uspešno promenjen.");
        });
    }
}
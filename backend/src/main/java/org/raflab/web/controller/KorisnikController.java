package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.KorisnikRequest;
import org.raflab.web.service.KorisnikService;
import static spark.Spark.*;

public class KorisnikController {
    private final KorisnikService korisnikService = new KorisnikService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // 1. Ruta za LOGIN (ovo već imaš)
        post("/api/login", (req, res) -> {
            KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
            String token = org.raflab.web.auth.AuthService.login(requestDto.getEmail(), requestDto.getLozinka());

            if (token == null) {
                res.status(401);
                return gson.toJson("Greska: Neispravni podaci");
            }
            if (token.equals("DEAKTIVIRAN")) {
                res.status(403);
                return gson.toJson("Greska: Nalog je deaktiviran");
            }

            var korisnikResponse = korisnikService.login(requestDto);
            korisnikResponse.setJwt(token);
            return gson.toJson(korisnikResponse);
        });

        // 2. DODAJ OVU RUTU KOJA TI NEDOSTAJE!
        get("/api/korisnici", (req, res) -> {
            // Uzimamo query parametar ?page=X, ako ga nema default je 1
            String pageStr = req.queryParams("page");
            int page = (pageStr != null) ? Integer.parseInt(pageStr) : 1;

            // Pozivamo servis da nam vrati listu
            return gson.toJson(korisnikService.findAll(page));
        });
    }
}

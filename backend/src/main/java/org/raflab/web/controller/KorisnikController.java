package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.KorisnikRequest;
import org.raflab.web.service.KorisnikService;
import static spark.Spark.*;

public class KorisnikController {
    private final KorisnikService korisnikService = new KorisnikService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        // --- JAVNE RUTE ---

        post("/api/login", (req, res) -> {
            try {
                KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
                // Pozivamo AuthService za JWT
                String token = org.raflab.web.auth.AuthService.login(requestDto.getEmail(), requestDto.getLozinka());

                if (token == null) {
                    res.status(401);
                    return gson.toJson("Greška: Neispravni podaci za prijavu.");
                }
                if (token.equals("DEAKTIVIRAN")) {
                    res.status(403);
                    return gson.toJson("Greška: Vaš nalog je deaktiviran od strane administratora.");
                }

                // Ako je JWT prošao, uzimamo podatke o korisniku
                var korisnikResponse = korisnikService.login(requestDto);
                korisnikResponse.setJwt(token);
                return gson.toJson(korisnikResponse);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            }
        });

        // --- ADMIN RUTE (Zahtevaju JWT filter koji smo ranije podesili) ---

        get("/api/korisnici", (req, res) -> {
            try {
                int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
                // Vraća PagedResponse<KorisnikResponse> (podaci + ukupnoStranica)
                return gson.toJson(korisnikService.findAll(page));
            } catch (Exception e) {
                res.status(400);
                return gson.toJson("Greška pri dobavljanju korisnika: " + e.getMessage());
            }
        });

        get("/api/korisnici/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                var korisnik = korisnikService.getById(id);
                if (korisnik == null) {
                    res.status(404);
                    return gson.toJson("Korisnik nije pronađen.");
                }
                return gson.toJson(korisnik);
            } catch (Exception e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            }
        });

        post("/api/korisnici", (req, res) -> {
            try {
                KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
                korisnikService.addKorisnik(requestDto);
                res.status(201);
                return gson.toJson("Korisnik uspešno kreiran.");
            } catch (RuntimeException e) {
                // Ovde hvatamo poruke tipa "Email je zauzet" ili "Lozinke se ne podudaraju"
                res.status(400);
                return gson.toJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return gson.toJson("Serverska greška pri kreiranju korisnika.");
            }
        });

        put("/api/korisnici/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                KorisnikRequest requestDto = gson.fromJson(req.body(), KorisnikRequest.class);
                korisnikService.updateKorisnik(id, requestDto);
                return gson.toJson("Korisnik uspešno izmenjen.");
            } catch (RuntimeException e) {
                res.status(400);
                return gson.toJson(e.getMessage());
            } catch (Exception e) {
                res.status(500);
                return gson.toJson("Serverska greška pri izmeni korisnika.");
            }
        });

        post("/api/korisnici/:id/status", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                korisnikService.toggleStatus(id);
                return gson.toJson("Status uspešno promenjen.");
            } catch (Exception e) {
                res.status(400);
                return gson.toJson("Greška pri promeni statusa.");
            }
        });
    }
}
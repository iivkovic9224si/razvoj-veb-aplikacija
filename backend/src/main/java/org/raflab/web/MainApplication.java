package org.raflab.web;

import com.google.gson.Gson;
import org.raflab.web.auth.JwtAuthorizationFilter;
import org.raflab.web.controller.*;
import static spark.Spark.*;

public class MainApplication {

    public static void main(String[] args) {
        // 1. Postavljanje porta
        port(4567);

        // 2. Globalni Exception Handler za VALIDACIJU
        // Ovo hvata svaki "throw new RuntimeException" iz tvojih servisa
        exception(RuntimeException.class, (e, req, res) -> {
            res.status(400); // Bad Request
            res.type("application/json");
            res.body("{\"error\": \"" + e.getMessage() + "\"}");
            System.err.println("Validation Error: " + e.getMessage());
        });

        // 3. CORS podešavanja (Neophodno za React)
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }
            String accessControlRequestMethod = request.requestMethod();
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        // 4. Globalni filter za zaglavlja
        before("/*", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:5173");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.type("application/json");
        });

        // 5. JWT Autorizacija (Tvoj zaštitni zid)
        before("/*", JwtAuthorizationFilter.handleAuth);

        // 6. Registracija svih 5 kontrolera
        new KorisnikController().registerRoutes();
        new VestController().registerRoutes();
        new KategorijaController().registerRoutes();
        new KomentarController().registerRoutes();
        new TagController().registerRoutes();

        // 7. Test rute
        get("/api/hello", (req, res) -> "{\"poruka\": \"Sistem je online i validacija je aktivna!\"}");

        System.out.println("=================================================");
        System.out.println(">>> BACKEND SERVER STARTUJE NA PORTU 4567 <<<");
        System.out.println(">>> SVI SERVISI I VALIDACIJA SU UCITANI <<<");
        System.out.println("=================================================");
    }
}
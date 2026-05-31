package org.raflab.web;

import org.raflab.web.auth.JwtAuthorizationFilter;
import org.raflab.web.controller.*;

import static spark.Spark.*;

public class MainApplication {

    public static void main(String[] args) {
        port(4567);

        options("/*", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:5173");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");

            response.status(200);
            return "";
        });

        before("/*", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:5173");
            response.header("Access-Control-Allow-Credentials", "true");
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.type("application/json");
        });

        before("/*", (request, response) -> {
            if ("OPTIONS".equalsIgnoreCase(request.requestMethod())) {
                halt(200, "");
            }
        });

        before("/*", JwtAuthorizationFilter.handleAuth);

        exception(RuntimeException.class, (e, req, res) -> {
            res.status(400);
            res.type("application/json");
            res.body("{\"error\": \"" + e.getMessage() + "\"}");
            System.err.println("Validation Error: " + e.getMessage());
        });

        new KorisnikController().registerRoutes();
        new VestController().registerRoutes();
        new KategorijaController().registerRoutes();
        new KomentarController().registerRoutes();
        new TagController().registerRoutes();

        get("/api/hello", (req, res) -> "{\"poruka\": \"Sistem je online i validacija je aktivna!\"}");

        System.out.println("=================================================");
        System.out.println(">>> BACKEND SERVER STARTUJE NA PORTU 4567 <<<");
        System.out.println(">>> SVI SERVISI I VALIDACIJA SU UCITANI <<<");
        System.out.println("=================================================");
    }
}
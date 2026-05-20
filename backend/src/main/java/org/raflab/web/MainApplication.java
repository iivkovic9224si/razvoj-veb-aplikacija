package org.raflab.web;

import com.google.gson.Gson;
import org.raflab.web.auth.JwtAuthorizationFilter;
import org.raflab.web.controller.*;
import static spark.Spark.*;

public class MainApplication {

    public static void main(String[] args) {
        // 1. Na kom portu sluša backend (React će ga gađati na 4567)
        port(4567);

        // 2. CORS - Ovo je MORANJE da bi React mogao da priča sa Javom
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

        // 3. Globalna podešavanja za svaki odgovor (pre svake rute)
        before("/*", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "http://localhost:5173"); // React adresa
            response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept");
            response.type("application/json"); // Svaki odgovor će biti JSON
        });

        // =================================================================
        // 4. TVOJ DEO: Kačenje filtera za JWT proveru i kontrolu uloga
        // =================================================================
        before("/*", JwtAuthorizationFilter.handleAuth);

        // 5. REGISTRACIJA KONTROLERA (Kolegin deo)
        // Ovde se instanciraju kontroleri i pokreću njihove rute
        new KorisnikController().registerRoutes();
        new VestController().registerRoutes();
        new KategorijaController().registerRoutes();
        new KomentarController().registerRoutes();
        new TagController().registerRoutes();

        // 6. Test ruta da proveriš da li server uopšte radi
        get("/api/hello", (req, res) -> "{\"poruka\": \"Sistem je online!\"}");

        System.out.println(">>> Backend je uspesno pokrenut na http://localhost:4567");
    }
}
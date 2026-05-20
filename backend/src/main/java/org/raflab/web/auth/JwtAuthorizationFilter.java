package org.raflab.web.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import spark.Filter;
import spark.Request;
import spark.Response;
import static spark.Spark.halt;

public class JwtAuthorizationFilter {

    public static Filter handleAuth = (Request req, Response res) -> {
        String path = req.pathInfo();
        String method = req.requestMethod();

        // 1. JAVNE RUTE - Propuštamo ih odmah, ne treba token
        if (path.equals("/login") || method.equals("OPTIONS") || path.equals("/test")) {
            return;
        }

        // Sve GET metode na javnom delu aplikacije su slobodne za čitanje
        if (method.equals("GET") && (path.startsWith("/news") || path.startsWith("/categories") || path.startsWith("/comments"))) {
            return;
        }

        // Takođe, slanje komentara (POST /comments) je javno prema specifikaciji (bilo ko može komentarisati)
        if (method.equals("POST") && path.equals("/comments")) {
            return;
        }

        // 2. ZAŠTIĆENE RUTE - Provera Authorization Header-a
        String authHeader = req.headers("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            halt(401, "{\"error\": \"Pristup odbijen. Token nedostaje ili je neispravan.\"}");
        }

        String token = authHeader.substring(7); // Uzimamo samo string tokena bez "Bearer "

        try {
            // Validacija i dekodovanje tokena pomoću našeg ključa
            Algorithm algorithm = Algorithm.HMAC256(AuthService.SECRET_KEY);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            String tip = jwt.getClaim("tip").asString();       // ADMINISTRATOR ili STVARALAC_SADRZAJA
            String status = jwt.getClaim("status").asString();   // AKTIVAN ili NEAKTIVAN

            // Dodatna odbrana: ako je admin u međuvremenu ugasio ovog korisnika
            if ("NEAKTIVAN".equals(status)) {
                halt(403, "{\"error\": \"Vaš nalog je deaktiviran.\"}");
            }

            // 3. PRAVILA AUTORIZACIJE (Uloge)

            // Pravilo: Ruti /users (CMS upravljanje korisnicima) može pristupiti SAMO Admin
            if (path.startsWith("/users") && !"ADMINISTRATOR".equals(tip)) {
                halt(403, "{\"error\": \"Nemate privilegije za ovu akciju. Samo Administrator.\"}");
            }

            // Za rute /news i /categories (POST, PUT, DELETE) - dozvoljeno i Adminu i Autoru
            // (Pošto smo prošli gornje provere, ovde samo obezbeđujemo da neulogovani nemaju pristup)
            if ((path.startsWith("/news") || path.startsWith("/categories")) &&
                    !("ADMINISTRATOR".equals(tip) || "STVARALAC_SADRZAJA".equals(tip))) {
                halt(403, "{\"error\": \"Zabranjen pristup.\"}");
            }

            // Čuvamo podatke o ulogovanom korisniku unutar request-a, zatrebaće kolegi u kontrolerima
            req.attribute("trenutniKorisnikId", jwt.getClaim("id").asInt());
            req.attribute("trenutniKorisnikTip", tip);

        } catch (Exception e) {
            halt(401, "{\"error\": \"Token je nevažeći ili je istekao.\"}");
        }
    };
}
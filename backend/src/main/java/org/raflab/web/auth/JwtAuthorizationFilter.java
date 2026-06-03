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

        if (path.equals("/api/login") || method.equals("OPTIONS") || path.equals("/api/test") || path.equals("/api/hello")) {
            return;
        }

        if (method.equals("POST") && (path.endsWith("/like") || path.endsWith("/dislike"))) {
            return;
        }

        if (method.equals("GET") && (
                path.startsWith("/api/vesti") ||
                        path.startsWith("/api/kategorije") ||
                        path.startsWith("/api/komentari") ||
                        path.startsWith("/api/tagovi"))) {
            return;
        }

        if (method.equals("POST") && path.equals("/api/komentari")) {
            return;
        }

        String authHeader = req.headers("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            halt(401, "{\"error\": \"Pristup odbijen. Token nedostaje ili je neispravan.\"}");
        }

        String token = authHeader.substring(7);

        try {
            Algorithm algorithm = Algorithm.HMAC256(AuthService.SECRET_KEY);
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);

            String tip = jwt.getClaim("tip").asString();
            String status = jwt.getClaim("status").asString();

            if ("NEAKTIVAN".equals(status)) {
                halt(403, "{\"error\": \"Vaš nalog je deaktiviran.\"}");
            }

            if (path.startsWith("/api/korisnici") && !"ADMINISTRATOR".equals(tip)) {
                halt(403, "{\"error\": \"Samo Administrator može upravljati korisnicima.\"}");
            }

            if ((path.startsWith("/api/vesti") || path.startsWith("/api/kategorije")) &&
                    !("ADMINISTRATOR".equals(tip) || "STVARALAC_SADRZAJA".equals(tip))) {
                halt(403, "{\"error\": \"Zabranjen pristup.\"}");
            }

            req.attribute("trenutniKorisnikId", jwt.getClaim("id").asInt());
            req.attribute("trenutniKorisnikTip", tip);

        } catch (Exception e) {
            halt(401, "{\"error\": \"Token je nevažeći ili je istekao.\"}");
        }
    };
}
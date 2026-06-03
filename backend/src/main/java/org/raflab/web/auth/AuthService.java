package org.raflab.web.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.raflab.web.model.Korisnik;
import org.raflab.web.utils.HashUtils;

import java.util.Date;

public class AuthService {

    public static final String SECRET_KEY = "raf_veb_aplikacije_tajni_kljuc_2026";

    private static final AuthRepository authRepository = new AuthRepository();

    public static String login(String email, String plainLozinka) {
        String hashedLozinka = HashUtils.sha256(plainLozinka);

        Korisnik korisnik = authRepository.findByEmailAndLozinka(email, hashedLozinka);

        if (korisnik == null) {
            return null;
        }

        if ("NEAKTIVAN".equals(korisnik.getStatus())) {
            return "DEAKTIVIRAN";
        }

        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

        return JWT.create()
                .withSubject(korisnik.getEmail())
                .withClaim("id", korisnik.getId())
                .withClaim("tip", korisnik.getTip())
                .withClaim("status", korisnik.getStatus())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                .sign(algorithm);
    }
}
package org.raflab.web.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.raflab.web.DatabaseConnection;
import org.raflab.web.utils.HashUtils;
import java.sql.*;
import java.util.Date;

public class AuthService {

    // Ovaj kljuc mora biti tajna i koristi se i za potpisivanje i za verifikaciju tokena
    public static final String SECRET_KEY = "raf_veb_aplikacije_tajni_kljuc_2026";

    public static String login(String email, String plainLozinka) {
        // 1. Hešujemo lozinku koja je stigla sa frontenda da bismo je uporedili sa onom u bazi
        String hashedLozinka = HashUtils.sha256(plainLozinka);

        String upit = "SELECT id, tip, status FROM korisnici WHERE email = ? AND lozinka = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setString(1, email);
            st.setString(2, hashedLozinka);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");

                    // Ako je korisnik deaktiviran, ne dozvoljavamo login
                    if ("NEAKTIVAN".equals(status)) {
                        return "DEAKTIVIRAN";
                    }

                    int id = rs.getInt("id");
                    String tip = rs.getString("tip");

                    // 2. Korisnik je uspešno uneo podatke -> Generišemo JWT Token
                    Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

                    return JWT.create()
                            .withSubject(email)
                            .withClaim("id", id)
                            .withClaim("tip", tip) // ADMINISTRATOR ili STVARALAC_SADRZAJA
                            .withClaim("status", status)
                            .withIssuedAt(new Date())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000)) // Traje 2 sata
                            .sign(algorithm);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Pogrešan email ili lozinka
    }
}
package org.raflab.web.auth;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Korisnik;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthRepository {

    public Korisnik findByEmailAndLozinka(String email, String hashedLozinka) {
        String upit = "SELECT id, email, tip, status FROM korisnici WHERE email = ? AND lozinka = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setString(1, email);
            st.setString(2, hashedLozinka);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    Korisnik korisnik = new Korisnik();

                    korisnik.setId(rs.getInt("id"));
                    korisnik.setEmail(rs.getString("email"));
                    korisnik.setTip(rs.getString("tip"));
                    korisnik.setStatus(rs.getString("status"));

                    return korisnik;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
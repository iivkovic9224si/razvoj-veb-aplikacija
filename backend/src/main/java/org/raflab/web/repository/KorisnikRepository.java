package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Korisnik;
import java.sql.*;

public class KorisnikRepository {

    public Korisnik findKorisnik(String email, String lozinka) {
        // Status je sada provera stringa 'AKTIVAN'
        String upit = "SELECT * FROM korisnici WHERE email = ? AND lozinka = ? AND status = 'AKTIVAN'";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setString(1, email);
            st.setString(2, lozinka);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Korisnik(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("ime"),
                            rs.getString("prezime"),
                            rs.getString("tip"),
                            rs.getString("status"), // rs.getString jer je ENUM
                            null
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
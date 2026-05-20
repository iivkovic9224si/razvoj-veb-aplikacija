package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Korisnik;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KorisnikRepository {
    
    public Korisnik findKorisnik(String email, String lozinka) {
        String upit = "SELECT * FROM korisnici WHERE email = ? AND lozinka = ? AND status = 1";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setString(1, email);
            st.setString(2, lozinka); // Kasnije ćemo ovde dodati SHA-256 enkripciju

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Korisnik(
                            rs.getInt("id"),
                            rs.getString("email"),
                            rs.getString("ime"),
                            rs.getString("prezime"),
                            rs.getString("tip"),
                            rs.getInt("status"),
                            null // Ne vraćamo lozinku nazad zbog bezbednosti
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
     
}
package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Korisnik;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KorisnikRepository {

    public Korisnik findKorisnik(String email, String lozinka) {
        String upit = "SELECT * FROM korisnici WHERE email = ? AND lozinka = ? AND status = 'AKTIVAN'";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, email);
            st.setString(2, lozinka);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Korisnik(rs.getInt("id"), rs.getString("email"), rs.getString("ime"),
                            rs.getString("prezime"), rs.getString("tip"), rs.getString("status"), null);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Korisnik> findAllPaginated(int offset, int limit) {
        List<Korisnik> korisnici = new ArrayList<>();
        String upit = "SELECT * FROM korisnici LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, limit);
            st.setInt(2, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    korisnici.add(new Korisnik(rs.getInt("id"), rs.getString("email"), rs.getString("ime"),
                            rs.getString("prezime"), rs.getString("tip"), rs.getString("status"), null));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return korisnici;
    }

    public int countAll() {
        String upit = "SELECT COUNT(*) FROM korisnici";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(upit)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
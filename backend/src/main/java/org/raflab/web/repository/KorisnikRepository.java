package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Korisnik;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KorisnikRepository {

    public void create(Korisnik k) {
        String upit = "INSERT INTO korisnici (email, ime, prezime, tip, status, lozinka) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, k.getEmail());
            st.setString(2, k.getIme());
            st.setString(3, k.getPrezime());
            st.setString(4, k.getTip());
            st.setString(5, k.getStatus());
            st.setString(6, k.getLozinka());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); throw new RuntimeException("Greška pri upisu korisnika."); }
    }

    public void update(Korisnik k) {
        // SQL koji uključuje i lozinku u update
        String upit = "UPDATE korisnici SET email = ?, ime = ?, prezime = ?, tip = ?, lozinka = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, k.getEmail());
            st.setString(2, k.getIme());
            st.setString(3, k.getPrezime());
            st.setString(4, k.getTip());
            st.setString(5, k.getLozinka());
            st.setInt(6, k.getId());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); throw new RuntimeException("Greška pri izmeni korisnika."); }
    }

    public Korisnik findById(int id) {
        String upit = "SELECT * FROM korisnici WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Korisnik(rs.getInt("id"), rs.getString("email"), rs.getString("ime"),
                            rs.getString("prezime"), rs.getString("tip"), rs.getString("status"), rs.getString("lozinka"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public Korisnik findByEmail(String email) {
        String upit = "SELECT * FROM korisnici WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, email);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Korisnik(rs.getInt("id"), rs.getString("email"), rs.getString("ime"),
                            rs.getString("prezime"), rs.getString("tip"), rs.getString("status"), rs.getString("lozinka"));
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

    public void updateStatus(int id, String status) {
        String upit = "UPDATE korisnici SET status = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, status);
            st.setInt(2, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
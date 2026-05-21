package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Kategorija;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategorijaRepository {

    // Dodaj ovu metodu u postojeći KategorijaRepository
    public Kategorija findByName(String ime) {
        String upit = "SELECT * FROM kategorije WHERE ime = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, ime);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return new Kategorija(rs.getInt("id"), rs.getString("ime"), rs.getString("opis"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Kategorija> findAllPaginated(int offset, int limit) {
        List<Kategorija> kategorije = new ArrayList<>();
        String upit = "SELECT * FROM kategorije LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, limit);
            st.setInt(2, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    kategorije.add(new Kategorija(rs.getInt("id"), rs.getString("ime"), rs.getString("opis")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return kategorije;
    }

    public int countAll() {
        String upit = "SELECT COUNT(*) FROM kategorije";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(upit)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void create(Kategorija k) {
        String upit = "INSERT INTO kategorije (ime, opis) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, k.getIme());
            st.setString(2, k.getOpis());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Kategorija k) {
        String upit = "UPDATE kategorije SET ime = ?, opis = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, k.getIme());
            st.setString(2, k.getOpis());
            st.setInt(3, k.getId());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public boolean hasNews(int id) {
        String upit = "SELECT COUNT(*) FROM vesti WHERE kategorija_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public void delete(int id) {
        String upit = "DELETE FROM kategorije WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Kategorija;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategorijaRepository {

    public List<Kategorija> findAll() {
        List<Kategorija> kategorije = new ArrayList<>();
        String upit = "SELECT * FROM kategorije";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(upit)) {
            while (rs.next()) {
                kategorije.add(new Kategorija(rs.getInt("id"), rs.getString("ime"), rs.getString("opis")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return kategorije;
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

    public void delete(int id) {
        String upit = "DELETE FROM kategorije WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

}
package org.raflab.web.repository;

import org.raflab.web.model.Vest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VestRepository {
    /*
    public List<Vest> findAllPaginated(int offset, int limit) {
        List<Vest> vesti = new ArrayList<>();
        String upit = "SELECT * FROM vesti ORDER BY vreme_kreiranja DESC LIMIT ?, ?";
        try (Connection con = Database.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, offset);
            st.setInt(2, limit);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    vesti.add(new Vest(rs.getInt("id"), rs.getString("naslov"), rs.getString("tekst"),
                              rs.getTimestamp("vreme_kreiranja"), rs.getInt("broj_poseta"),
                              rs.getInt("autor_id"), rs.getInt("kategorija_id")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return vesti;
    }

    public Vest findById(int id) {
        String upit = "SELECT * FROM vesti WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Vest(rs.getInt("id"), rs.getString("naslov"), rs.getString("tekst"),
                           rs.getTimestamp("vreme_kreiranja"), rs.getInt("broj_poseta"),
                           rs.getInt("autor_id"), rs.getInt("kategorija_id"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void create(Vest v) {
        String upit = "INSERT INTO vesti (naslov, tekst, autor_id, kategorija_id, vreme_kreiranja, broj_poseta) VALUES (?, ?, ?, ?, NOW(), 0)";
        try (Connection con = Database.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, v.getNaslov());
            st.setString(2, v.getTekst());
            st.setInt(3, v.getAutorId());
            st.setInt(4, v.getKategorijaId());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void incrementVisitCount(int id) {
        String upit = "UPDATE vesti SET broj_poseta = broj_poseta + 1 WHERE id = ?";
        try (Connection con = Database.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    */
}
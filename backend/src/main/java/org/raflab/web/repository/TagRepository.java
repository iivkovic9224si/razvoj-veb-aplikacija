package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Tag;
import org.raflab.web.model.Vest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public List<Tag> findAllPaginated(int offset, int limit) {
        List<Tag> tagovi = new ArrayList<>();
        String upit = "SELECT * FROM tagovi LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, limit);
            st.setInt(2, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    tagovi.add(new Tag(rs.getInt("id"), rs.getString("kljucna_rec")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tagovi;
    }

    public List<Tag> getTagsForVest(int vestId) {
        List<Tag> tagovi = new ArrayList<>();
        String upit = "SELECT t.* FROM tagovi t JOIN vest_tag vt ON t.id = vt.tag_id WHERE vt.vest_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, vestId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    tagovi.add(new Tag(rs.getInt("id"), rs.getString("kljucna_rec")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return tagovi;
    }

    // Pomoćna metoda za pretragu vesti po tagu (traži se u specifikaciji)
    public List<Integer> getVestIdsByTag(String kljucnaRec) {
        List<Integer> ids = new ArrayList<>();
        String upit = "SELECT vt.vest_id FROM vest_tag vt JOIN tagovi t ON vt.tag_id = t.id WHERE t.kljucna_rec = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, kljucnaRec);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) ids.add(rs.getInt("vest_id"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ids;
    }
}
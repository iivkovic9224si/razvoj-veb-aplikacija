package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Tag;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TagRepository {

    public List<Tag> getTagsForVest(int vestId) {
        List<Tag> tagovi = new ArrayList<>();
        // Promenjeno t.rec u t.kljucna_rec
        String upit = "SELECT t.id, t.kljucna_rec FROM tagovi t " +
                "JOIN vest_tag vt ON t.id = vt.tag_id " +
                "WHERE vt.vest_id = ?";
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

    public int getOrInsertTag(String kljucnaRec) {
        // Promenjeno u kljucna_rec
        String selectUpit = "SELECT id FROM tagovi WHERE kljucna_rec = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(selectUpit)) {
            st.setString(1, kljucnaRec);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }

            // Ako ne postoji, ubaci ga
            String insertUpit = "INSERT INTO tagovi (kljucna_rec) VALUES (?)";
            try (PreparedStatement stInsert = con.prepareStatement(insertUpit, Statement.RETURN_GENERATED_KEYS)) {
                stInsert.setString(1, kljucnaRec);
                stInsert.executeUpdate();
                try (ResultSet generatedKeys = stInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public void linkTagToVest(int vestId, int tagId) {
        String upit = "INSERT IGNORE INTO vest_tag (vest_id, tag_id) VALUES (?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, vestId);
            st.setInt(2, tagId);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
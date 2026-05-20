package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Komentar;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KomentarRepository {

    public List<Komentar> findByVestPaginated(int vestId, int offset, int limit) {
        List<Komentar> komentari = new ArrayList<>();
        String upit = "SELECT * FROM komentari WHERE vest_id = ? ORDER BY vreme_kreiranja DESC LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, vestId);
            st.setInt(2, limit);
            st.setInt(3, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    komentari.add(new Komentar(
                            rs.getInt("id"), rs.getString("autor"), rs.getString("tekst"),
                            rs.getTimestamp("vreme_kreiranja"), rs.getInt("broj_lajkova"),
                            rs.getInt("broj_dislajkova"), rs.getInt("vest_id")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return komentari;
    }

    public int countByVest(int vestId) {
        String upit = "SELECT COUNT(*) FROM komentari WHERE vest_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, vestId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public void create(Komentar k) {
        String upit = "INSERT INTO komentari (autor, tekst, vest_id, vreme_kreiranja) VALUES (?, ?, ?, NOW())";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, k.getAutor());
            st.setString(2, k.getTekst());
            st.setInt(3, k.getVestId());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void incrementLikes(int id) {
        String upit = "UPDATE komentari SET broj_lajkova = broj_lajkova + 1 WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void incrementDislikes(int id) {
        String upit = "UPDATE komentari SET broj_dislajkova = broj_dislajkova + 1 WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
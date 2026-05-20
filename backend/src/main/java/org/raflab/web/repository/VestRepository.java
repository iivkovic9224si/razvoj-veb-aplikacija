package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Vest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VestRepository {

    public List<Vest> findAllPaginated(int offset, int limit) {
        List<Vest> vesti = new ArrayList<>();
        String upit = "SELECT * FROM vesti ORDER BY vreme_kreiranja DESC LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, limit);
            st.setInt(2, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    vesti.add(new Vest(
                            rs.getInt("id"),
                            rs.getString("naslov"),
                            rs.getString("tekst"),
                            rs.getTimestamp("vreme_kreiranja"),
                            rs.getInt("broj_poseta"),
                            rs.getInt("broj_lajkova"),
                            rs.getInt("broj_dislajkova"),
                            rs.getInt("autor_id"),
                            rs.getInt("kategorija_id")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return vesti;
    }

    public int countAll() {
        String upit = "SELECT COUNT(*) FROM vesti";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(upit)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public Vest findById(int id) {
        String upit = "SELECT * FROM vesti WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Vest(
                            rs.getInt("id"),
                            rs.getString("naslov"),
                            rs.getString("tekst"),
                            rs.getTimestamp("vreme_kreiranja"),
                            rs.getInt("broj_poseta"),
                            rs.getInt("broj_lajkova"),
                            rs.getInt("broj_dislajkova"),
                            rs.getInt("autor_id"),
                            rs.getInt("kategorija_id")
                    );
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void create(Vest v, List<String> tagovi) {
        String upitVest = "INSERT INTO vesti (naslov, tekst, autor_id, kategorija_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement st = con.prepareStatement(upitVest, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, v.getNaslov());
                st.setString(2, v.getTekst());
                st.setInt(3, v.getAutorId());
                st.setInt(4, v.getKategorijaId());
                st.executeUpdate();

                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int vestId = rs.getInt(1);
                    insertTagsForVest(con, vestId, tagovi);
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void insertTagsForVest(Connection con, int vestId, List<String> tagovi) throws SQLException {
        String upitTag = "INSERT IGNORE INTO tagovi (kljucna_rec) VALUES (?)";
        String upitVeza = "INSERT INTO vest_tag (vest_id, tag_id) SELECT ?, id FROM tagovi WHERE kljucna_rec = ?";
        for (String tag : tagovi) {
            try (PreparedStatement st1 = con.prepareStatement(upitTag)) {
                st1.setString(1, tag);
                st1.executeUpdate();
            }
            try (PreparedStatement st2 = con.prepareStatement(upitVeza)) {
                st2.setInt(1, vestId);
                st2.setString(2, tag);
                st2.executeUpdate();
            }
        }
    }

    public void incrementVisitCount(int id) {
        String upit = "UPDATE vesti SET broj_poseta = broj_poseta + 1 WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Vest v) {
        String upit = "UPDATE vesti SET naslov = ?, tekst = ?, kategorija_id = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, v.getNaslov());
            st.setString(2, v.getTekst());
            st.setInt(3, v.getKategorijaId());
            st.setInt(4, v.getId());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(int id) {
        String upit = "DELETE FROM vesti WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
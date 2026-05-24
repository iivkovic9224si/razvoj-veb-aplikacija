package org.raflab.web.repository;

import org.raflab.web.DatabaseConnection;
import org.raflab.web.model.Vest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VestRepository {

    public List<Vest> findFilteredPaginated(Integer kategorijaId, String tag, String search, int offset, int limit) {
        List<Vest> vesti = new ArrayList<>();

        StringBuilder upit = new StringBuilder("SELECT DISTINCT v.* FROM vesti v ");

        if (tag != null && !tag.trim().isEmpty()) {
            upit.append("JOIN vest_tag vt ON v.id = vt.vest_id ");
            upit.append("JOIN tagovi t ON vt.tag_id = t.id ");
        }

        upit.append("WHERE 1=1 ");

        if (kategorijaId != null) {
            upit.append("AND v.kategorija_id = ? ");
        }

        if (tag != null && !tag.trim().isEmpty()) {
            upit.append("AND t.kljucna_rec = ? ");
        }

        if (search != null && !search.trim().isEmpty()) {
            upit.append("AND (v.naslov LIKE ? OR v.tekst LIKE ?) ");
        }

        upit.append("ORDER BY v.vreme_kreiranja DESC LIMIT ? OFFSET ?");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit.toString())) {

            int i = 1;

            if (kategorijaId != null) {
                st.setInt(i++, kategorijaId);
            }

            if (tag != null && !tag.trim().isEmpty()) {
                st.setString(i++, tag);
            }

            if (search != null && !search.trim().isEmpty()) {
                String p = "%" + search + "%";
                st.setString(i++, p);
                st.setString(i++, p);
            }

            st.setInt(i++, limit);
            st.setInt(i++, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Vest vest = napraviVestIzResultSeta(rs);
                    vesti.add(vest);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vesti;
    }

    public int countFiltered(Integer kategorijaId, String tag, String search) {
        StringBuilder upit = new StringBuilder("SELECT COUNT(DISTINCT v.id) FROM vesti v ");
        if (tag != null) upit.append("JOIN vest_tag vt ON v.id = vt.vest_id JOIN tagovi t ON vt.tag_id = t.id ");
        upit.append("WHERE 1=1 ");
        if (kategorijaId != null) upit.append("AND v.kategorija_id = ? ");
        if (tag != null) upit.append("AND t.kljucna_rec = ? ");
        if (search != null) upit.append("AND (v.naslov LIKE ? OR v.tekst LIKE ?) ");

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit.toString())) {
            int i = 1;
            if (kategorijaId != null) st.setInt(i++, kategorijaId);
            if (tag != null) st.setString(i++, tag);
            if (search != null) {
                String p = "%" + search + "%";
                st.setString(i++, p);
                st.setString(i++, p);
            }
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public List<Vest> findMostReadLast30Days() {
        List<Vest> vesti = new ArrayList<>();

        String upit = "SELECT * FROM vesti " +
                "WHERE vreme_kreiranja > NOW() - INTERVAL 30 DAY " +
                "ORDER BY broj_poseta DESC LIMIT 10";

        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(upit)) {

            while (rs.next()) {
                Vest vest = napraviVestIzResultSeta(rs);
                vesti.add(vest);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vesti;
    }

    public void create(Vest v, List<String> tagovi) {
        String upitVest = "INSERT INTO vesti (naslov, tekst, autor_id, kategorija_id, vreme_kreiranja) VALUES (?, ?, ?, ?, NOW())";
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement st = con.prepareStatement(upitVest, Statement.RETURN_GENERATED_KEYS)) {
                st.setString(1, v.getNaslov());
                st.setString(2, v.getTekst());
                st.setInt(3, v.getAutorId());
                st.setInt(4, v.getKategorijaId());
                st.executeUpdate();
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) insertTagsForVest(con, rs.getInt(1), tagovi);
                con.commit();
            } catch (SQLException e) { con.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(Vest v, List<String> noviTagovi) {
        String upit = "UPDATE vesti SET naslov = ?, tekst = ?, kategorija_id = ? WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement st = con.prepareStatement(upit)) {
                st.setString(1, v.getNaslov());
                st.setString(2, v.getTekst());
                st.setInt(3, v.getKategorijaId());
                st.setInt(4, v.getId());
                st.executeUpdate();
                try (PreparedStatement del = con.prepareStatement("DELETE FROM vest_tag WHERE vest_id = ?")) {
                    del.setInt(1, v.getId());
                    del.executeUpdate();
                }
                insertTagsForVest(con, v.getId(), noviTagovi);
                con.commit();
            } catch (SQLException e) { con.rollback(); throw e; }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void insertTagsForVest(Connection con, int vestId, List<String> tagovi) throws SQLException {
        if (tagovi == null) return;
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

    public Vest findById(int id) {
        String upit = "SELECT * FROM vesti WHERE id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setInt(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return napraviVestIzResultSeta(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(int id) {
        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                // 1. Obriši komentare vesti
                try (PreparedStatement st = con.prepareStatement("DELETE FROM komentari WHERE vest_id = ?")) {
                    st.setInt(1, id);
                    st.executeUpdate();
                }
                // 2. Obriši veze sa tagovima
                try (PreparedStatement st = con.prepareStatement("DELETE FROM vest_tag WHERE vest_id = ?")) {
                    st.setInt(1, id);
                    st.executeUpdate();
                }
                // 3. Obriši samu vest
                try (PreparedStatement st = con.prepareStatement("DELETE FROM vesti WHERE id = ?")) {
                    st.setInt(1, id);
                    st.executeUpdate();
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void incrementVisitCount(int id) {
        executeSimpleUpdate("UPDATE vesti SET broj_poseta = broj_poseta + 1 WHERE id = ?", id);
    }

    public void incrementLikes(int id) {
        executeSimpleUpdate("UPDATE vesti SET broj_lajkova = broj_lajkova + 1 WHERE id = ?", id);
    }

    public void incrementDislikes(int id) {
        executeSimpleUpdate("UPDATE vesti SET broj_dislajkova = broj_dislajkova + 1 WHERE id = ?", id);
    }

    public void decrementLikes(int id) {
        executeSimpleUpdate(
                "UPDATE vesti SET broj_lajkova = GREATEST(broj_lajkova - 1, 0) WHERE id = ?",
                id
        );
    }

    public void decrementDislikes(int id) {
        executeSimpleUpdate(
                "UPDATE vesti SET broj_dislajkova = GREATEST(broj_dislajkova - 1, 0) WHERE id = ?",
                id
        );
    }

    private void executeSimpleUpdate(String query, int id) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Vest> findBySearchPaginated(String query, int offset, int limit) {
        List<Vest> vesti = new ArrayList<>();
        String upit = "SELECT * FROM vesti WHERE naslov LIKE ? OR tekst LIKE ? ORDER BY vreme_kreiranja DESC LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, "%" + query + "%");
            st.setString(2, "%" + query + "%");
            st.setInt(3, limit);
            st.setInt(4, offset);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    vesti.add(new Vest(rs.getInt("id"), rs.getString("naslov"), rs.getString("tekst"),
                            rs.getTimestamp("vreme_kreiranja"), rs.getInt("broj_poseta"),
                            rs.getInt("broj_lajkova"), rs.getInt("broj_dislajkova"),
                            rs.getInt("autor_id"), rs.getInt("kategorija_id")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return vesti;
    }

    public List<Vest> findMostReadPaginated(int offset, int limit) {
        List<Vest> vesti = new ArrayList<>();
        // Ovde ide onaj SQL upit sa INTERVAL 30 DAY koji meri popularnost
        String upit = "SELECT * FROM vesti WHERE vreme_kreiranja >= NOW() - INTERVAL 30 DAY ORDER BY broj_poseta DESC LIMIT ? OFFSET ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            // Ovde idu samo limit i offset jer upit nema druge parametre!
            st.setInt(1, limit);
            st.setInt(2, offset);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    vesti.add(new Vest(rs.getInt("id"), rs.getString("naslov"), rs.getString("tekst"),
                            rs.getTimestamp("vreme_kreiranja"), rs.getInt("broj_poseta"),
                            rs.getInt("broj_lajkova"), rs.getInt("broj_dislajkova"),
                            rs.getInt("autor_id"), rs.getInt("kategorija_id")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return vesti;
    }

    public String getKategorijaIme(int id) {
        String upit = "SELECT ime FROM kategorije WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getString("ime");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Nepoznato";
    }

    public String getAutorImePrezime(int id) {
        String upit = "SELECT ime, prezime FROM korisnici WHERE id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return rs.getString("ime") + " " + rs.getString("prezime");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return "Nepoznati Autor";
    }
    public int countMostReadLast30Days() {
        String upit = "SELECT COUNT(*) FROM vesti WHERE vreme_kreiranja >= NOW() - INTERVAL 30 DAY";
        try (Connection con = DatabaseConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(upit)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private Vest napraviVestIzResultSeta(ResultSet rs) throws SQLException {
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
    public List<Vest> findTopByReactions(int limit) {
        List<Vest> vesti = new ArrayList<>();

        String upit = "SELECT * FROM vesti " +
                "ORDER BY (broj_lajkova + broj_dislajkova) DESC, vreme_kreiranja DESC " +
                "LIMIT ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setInt(1, limit);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Vest vest = napraviVestIzResultSeta(rs);
                    vesti.add(vest);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vesti;
    }

    public List<Vest> findRelatedByTags(int vestId, int limit) {
        List<Vest> vesti = new ArrayList<>();

        String upit =
                "SELECT DISTINCT v.* FROM vesti v " +
                        "JOIN vest_tag vt ON v.id = vt.vest_id " +
                        "WHERE vt.tag_id IN ( " +
                        "    SELECT tag_id FROM vest_tag WHERE vest_id = ? " +
                        ") " +
                        "AND v.id <> ? " +
                        "ORDER BY v.vreme_kreiranja DESC " +
                        "LIMIT ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {

            st.setInt(1, vestId);
            st.setInt(2, vestId);
            st.setInt(3, limit);

            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Vest vest = napraviVestIzResultSeta(rs);
                    vesti.add(vest);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vesti;
    }
}
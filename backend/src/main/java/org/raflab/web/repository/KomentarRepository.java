package org.raflab.web.repository;

import org.raflab.web.model.Komentar;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KomentarRepository {
    /*
    public List<Komentar> findByVest(int vestId) {
        List<Komentar> komentari = new ArrayList<>();
        String upit = "SELECT * FROM komentari WHERE vest_id = ? ORDER BY vreme_kreiranja DESC";
        try (Connection con = Database.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setInt(1, vestId);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    komentari.add(new Komentar(rs.getInt("id"), rs.getString("autor"),
                                  rs.getString("tekst"), rs.getTimestamp("vreme_kreiranja"), rs.getInt("vest_id")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return komentari;
    }

    public void create(Komentar k) {
        String upit = "INSERT INTO komentari (autor, tekst, vest_id, vreme_kreiranja) VALUES (?, ?, ?, NOW())";
        try (Connection con = Database.getConnection();
             PreparedStatement st = con.prepareStatement(upit)) {
            st.setString(1, k.getAutor());
            st.setString(2, k.getTekst());
            st.setInt(3, k.getVestId());
            st.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    */
}
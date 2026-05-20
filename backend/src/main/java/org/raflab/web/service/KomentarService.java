package org.raflab.web.service;

import org.raflab.web.dto.request.KomentarRequest;
import org.raflab.web.dto.response.KomentarResponse;
import org.raflab.web.model.Komentar;
import org.raflab.web.repository.KomentarRepository;
import java.util.List;
import java.util.stream.Collectors;

public class KomentarService {
    private final KomentarRepository komentarRepository = new KomentarRepository();

    public List<KomentarResponse> getByVest(int vestId, int page) {
        if (vestId <= 0) throw new RuntimeException("ID vesti nije validan!");
        if (page < 1) page = 1;
        int limit = 10;
        int offset = (page - 1) * limit;
        return komentarRepository.findByVestPaginated(vestId, offset, limit).stream()
                .map(k -> new KomentarResponse(k.getId(), k.getAutor(), k.getTekst(),
                        k.getVremeKreiranja(), k.getBrojLajkova(), k.getBrojDislajkova()))
                .collect(Collectors.toList());
    }

    public void addComment(KomentarRequest req) {
        if (req.getAutor() == null || req.getAutor().trim().isEmpty()) throw new RuntimeException("Autor je obavezan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst komentara je obavezan!");
        if (req.getVestId() <= 0) throw new RuntimeException("Komentar mora biti vezan za validnu vest!");

        Komentar k = new Komentar(0, req.getAutor(), req.getTekst(), null, 0, 0, req.getVestId());
        komentarRepository.create(k);
    }

    public void likeComment(int id) {
        komentarRepository.incrementLikes(id);
    }

    public void dislikeComment(int id) {
        komentarRepository.incrementDislikes(id);
    }
}
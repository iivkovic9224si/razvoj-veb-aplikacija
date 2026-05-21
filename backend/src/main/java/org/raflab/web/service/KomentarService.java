package org.raflab.web.service;

import org.raflab.web.dto.request.KomentarRequest;
import org.raflab.web.dto.response.KomentarResponse;
import org.raflab.web.dto.response.PagedResponse;
import org.raflab.web.model.Komentar;
import org.raflab.web.repository.KomentarRepository;
import org.raflab.web.repository.VestRepository;

import java.util.List;
import java.util.stream.Collectors;

public class KomentarService {
    private final KomentarRepository komentarRepository = new KomentarRepository();
    private final VestRepository vestRepository = new VestRepository();

    public PagedResponse<KomentarResponse> getByVest(int vestId, int page) {
        if (vestId <= 0) throw new RuntimeException("ID vesti nije validan!");
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;

        List<KomentarResponse> komentari = komentarRepository.findByVestPaginated(vestId, offset, limit).stream()
                .map(k -> new KomentarResponse(k.getId(), k.getAutor(), k.getTekst(),
                        k.getVremeKreiranja(), k.getBrojLajkova(), k.getBrojDislajkova()))
                .collect(Collectors.toList());

        int totalItems = komentarRepository.countByVest(vestId);
        int totalPages = (int) Math.ceil((double) totalItems / limit);

        return new PagedResponse<>(komentari, totalPages);
    }

    public void addComment(KomentarRequest reqDto) {
        // 1. Validacija unosa
        if (reqDto.getAutor() == null || reqDto.getAutor().trim().isEmpty()) {
            throw new RuntimeException("Ime autora komentara je obavezno!");
        }
        if (reqDto.getTekst() == null || reqDto.getTekst().trim().isEmpty()) {
            throw new RuntimeException("Tekst komentara ne može biti prazan!");
        }

        // 2. Provera da li vest uopšte postoji u bazi pre upisa komentara
        if (vestRepository.findById(reqDto.getVestId()) == null) {
            throw new RuntimeException("Nemoguće dodati komentar. Vest sa ID-jem " + reqDto.getVestId() + " ne postoji!");
        }

        // 3. Ako je sve u redu, pravimo model i šaljemo repozitorijumu
        Komentar k = new Komentar(0, reqDto.getAutor(), reqDto.getTekst(), null, 0, 0, reqDto.getVestId());
        komentarRepository.create(k);
    }

    public void likeComment(int id) { komentarRepository.incrementLikes(id); }
    public void dislikeComment(int id) { komentarRepository.incrementDislikes(id); }
}
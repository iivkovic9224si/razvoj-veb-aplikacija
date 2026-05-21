package org.raflab.web.service;

import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.dto.response.VestResponse;
import org.raflab.web.model.Vest;
import org.raflab.web.repository.VestRepository;
import org.raflab.web.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;

public class VestService {
    private final VestRepository vestRepository = new VestRepository();
    private final TagRepository tagRepository = new TagRepository();

    // 1. Dobavljanje svih vesti (standardno hronološki)
    public List<VestResponse> getAll(int page) {
        if (page < 1) page = 1;
        int limit = 10;
        int offset = (page - 1) * limit;
        return vestRepository.findAllPaginated(offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 2. [NOVO / SPECIFIKACIJA] Dobavljanje najčitanijih vesti
    public List<VestResponse> getMostRead(int page) {
        if (page < 1) page = 1;
        int limit = 10;
        int offset = (page - 1) * limit;
        return vestRepository.findMostReadPaginated(offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 3. [NOVO / SPECIFIKACIJA] Pretraga vesti po tekstu/naslovu
    public List<VestResponse> search(String query, int page) {
        if (page < 1) page = 1;
        if (query == null || query.trim().isEmpty()) {
            return getAll(page); // Ako je prazan string, vrati sve
        }
        int limit = 10;
        int offset = (page - 1) * limit;
        return vestRepository.findBySearchPaginated(query.trim(), offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VestResponse getById(int id) {
        if (id <= 0) throw new RuntimeException("ID vesti nije validan!");
        Vest v = vestRepository.findById(id);
        if (v == null) return null;
        vestRepository.incrementVisitCount(id);
        return mapToResponse(v);
    }

    public void create(VestRequest req, int autorId) {
        if (req.getNaslov() == null || req.getNaslov().trim().isEmpty()) throw new RuntimeException("Naslov je obavezan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst vesti je obavezan!");
        if (req.getKategorijaId() <= 0) throw new RuntimeException("Morate izabrati kategoriju!");
        if (autorId <= 0) throw new RuntimeException("Autor vesti nije prepoznat!");

        Vest v = new Vest(0, req.getNaslov(), req.getTekst(), null, 0, 0, 0, autorId, req.getKategorijaId());
        vestRepository.create(v, req.getTagovi());
    }

    // 4. [NOVO] Izmena vesti
    public void update(int id, VestRequest req) {
        if (id <= 0) throw new RuntimeException("ID vesti nije validan!");
        Vest postojeca = vestRepository.findById(id);
        if (postojeca == null) throw new RuntimeException("Vest ne postoji!");

        if (req.getNaslov() == null || req.getNaslov().trim().isEmpty()) throw new RuntimeException("Naslov ne može biti prazan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst ne može biti prazan!");

        postojeca.setNaslov(req.getNaslov());
        postojeca.setTekst(req.getTekst());
        postojeca.setKategorijaId(req.getKategorijaId());

        vestRepository.update(postojeca);
        // Opciono: ovde možeš dodati i ponovno vezivanje tagova ako to frontend zahteva pri izmeni
    }

    // 5. [NOVO] Brisanje vesti
    public void delete(int id) {
        if (id <= 0) throw new RuntimeException("ID vesti nije validan!");
        Vest postojeca = vestRepository.findById(id);
        if (postojeca == null) throw new RuntimeException("Vest ne postoji!");

        vestRepository.delete(id);
    }

    // ISPRAVLJENO MAPIRANJE: Sada puni imena autora i kategorije
    private VestResponse mapToResponse(Vest v) {
        VestResponse res = new VestResponse();
        res.setId(v.getId());
        res.setNaslov(v.getNaslov());
        res.setTekst(v.getTekst());
        res.setVremeKreiranja(v.getVremeKreiranja());
        res.setBrojPoseta(v.getBrojPoseta());
        res.setBrojLajkova(v.getBrojLajkova());
        res.setBrojDislajkova(v.getBrojDislajkova());

        // POVEZIVANJE IMENA KOJA SU FALILA
        res.setKategorijaIme(vestRepository.getKategorijaIme(v.getKategorijaId()));
        res.setAutorIme(vestRepository.getAutorImePrezime(v.getAutorId()));

        // Čitanje tagova
        List<String> tagoviReči = tagRepository.getTagsForVest(v.getId()).stream()
                .map(t -> t.getKljucnaRec())
                .collect(Collectors.toList());
        res.setTagovi(tagoviReči);

        return res;
    }

    public VestResponse getByIdWithVisitLogic(int id, boolean vecPosetio) {
        if (id <= 0) throw new RuntimeException("ID vesti nije validan!");
        Vest v = vestRepository.findById(id);
        if (v == null) return null;

        if (!vecPosetio) {
            vestRepository.incrementVisitCount(id);
            v.setBrojPoseta(v.getBrojPoseta() + 1); // Da vrati ažuriran broj odmah u odgovoru
        }
        return mapToResponse(v);
    }

    public void likeVest(int id) {
        vestRepository.incrementLikes(id);
    }

    public void dislikeVest(int id) {
        vestRepository.incrementDislikes(id);
    }
}
package org.raflab.web.service;

import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.dto.response.VestResponse;
import org.raflab.web.dto.response.PagedResponse;
import org.raflab.web.model.Vest;
import org.raflab.web.repository.VestRepository;
import org.raflab.web.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;

public class VestService {
    private final VestRepository vestRepository = new VestRepository();
    private final TagRepository tagRepository = new TagRepository();

    public PagedResponse<VestResponse> getFiltered(Integer katId, String tag, String search, int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;
        List<VestResponse> podaci = vestRepository.findFilteredPaginated(katId, tag, search, offset, limit).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
        int total = vestRepository.countFiltered(katId, tag, search);
        int totalPages = (int) Math.ceil((double) total / limit);
        return new PagedResponse<>(podaci, totalPages);
    }

    public PagedResponse<VestResponse> getMostRead(int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;
        List<VestResponse> podaci = vestRepository.findMostReadPaginated(offset, limit).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
        int total = vestRepository.countMostReadLast30Days();
        int totalPages = (int) Math.ceil((double) total / limit);
        return new PagedResponse<>(podaci, totalPages);
    }

    // NOVO: Najbolje reakcije
    public List<VestResponse> getTopReactions() {
        return vestRepository.findTop3ByReactions().stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    // NOVO: Povezane vesti
    public List<VestResponse> getRelated(int vestId) {
        return vestRepository.findRelatedByTags(vestId).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public VestResponse getByIdWithVisitLogic(int id, boolean vecPosetio) {
        Vest v = vestRepository.findById(id);
        if (v == null) return null;
        if (!vecPosetio) {
            vestRepository.incrementVisitCount(id);
            v.setBrojPoseta(v.getBrojPoseta() + 1);
        }
        return mapToResponse(v);
    }

    public void create(VestRequest req, int autorId) {
        validate(req);
        Vest v = new Vest(0, req.getNaslov(), req.getTekst(), null, 0, 0, 0, autorId, req.getKategorijaId());
        vestRepository.create(v, req.getTagovi());
    }

    // AŽURIRANO: Provera autorizacije za update
    public void update(int id, VestRequest req, int userId, String userTip) {
        validate(req);
        Vest v = vestRepository.findById(id);
        if (v == null) throw new RuntimeException("Vest ne postoji.");

        if (!userTip.equals("ADMINISTRATOR") && v.getAutorId() != userId) {
            throw new RuntimeException("Nemate dozvolu za izmenu ove vesti.");
        }

        v.setNaslov(req.getNaslov());
        v.setTekst(req.getTekst());
        v.setKategorijaId(req.getKategorijaId());
        vestRepository.update(v, req.getTagovi());
    }

    // AŽURIRANO: Provera autorizacije za delete
    public void delete(int id, int userId, String userTip) {
        Vest v = vestRepository.findById(id);
        if (v == null) throw new RuntimeException("Vest ne postoji.");

        if (!userTip.equals("ADMINISTRATOR") && v.getAutorId() != userId) {
            throw new RuntimeException("Nemate dozvolu za brisanje ove vesti.");
        }

        vestRepository.delete(id);
    }

    public void likeVest(int id) { vestRepository.incrementLikes(id); }
    public void dislikeVest(int id) { vestRepository.incrementDislikes(id); }

    private void validate(VestRequest req) {
        if (req.getNaslov() == null || req.getNaslov().trim().isEmpty()) throw new RuntimeException("Naslov je obavezan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst je obavezan!");
        if (req.getKategorijaId() <= 0) throw new RuntimeException("Kategorija je obavezna!");
    }

    private VestResponse mapToResponse(Vest v) {
        VestResponse res = new VestResponse();
        res.setId(v.getId());
        res.setNaslov(v.getNaslov());
        res.setTekst(v.getTekst());
        res.setVremeKreiranja(v.getVremeKreiranja());
        res.setBrojPoseta(v.getBrojPoseta());
        res.setBrojLajkova(v.getBrojLajkova());
        res.setBrojDislajkova(v.getBrojDislajkova());
        res.setKategorijaIme(vestRepository.getKategorijaIme(v.getKategorijaId()));
        res.setAutorIme(vestRepository.getAutorImePrezime(v.getAutorId()));
        res.setTagovi(tagRepository.getTagsForVest(v.getId()).stream()
                .map(t -> t.getKljucnaRec()).collect(Collectors.toList()));
        return res;
    }
}
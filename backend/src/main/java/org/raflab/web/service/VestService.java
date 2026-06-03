package org.raflab.web.service;

import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.dto.response.VestResponse;
import org.raflab.web.dto.response.PagedResponse;
import org.raflab.web.model.Vest;
import org.raflab.web.repository.VestRepository;
import org.raflab.web.repository.TagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VestService {
    private final VestRepository vestRepository = new VestRepository();
    private final TagRepository tagRepository = new TagRepository();

    public PagedResponse<VestResponse> getFiltered(Integer katId, String tag, String search, int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;

        List<VestResponse> podaci = vestRepository.findFilteredPaginated(katId, tag, search, offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int total = vestRepository.countFiltered(katId, tag, search);
        int totalPages = (int) Math.ceil((double) total / limit);

        return new PagedResponse<>(podaci, totalPages);
    }

    public PagedResponse<VestResponse> getMostRead(int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;

        List<Vest> entitetiIzBaze = vestRepository.findMostReadPaginated(offset, limit);

        List<VestResponse> podaci = entitetiIzBaze.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        int total = vestRepository.countMostReadLast30Days();
        int totalPages = (int) Math.ceil((double) total / limit);

        return new PagedResponse<>(podaci, totalPages);
    }

    public VestResponse getByIdWithVisitLogic(int id, boolean vecPosetio) {
        if (id <= 0) throw new RuntimeException("ID vesti nije validan!");
        Vest v = vestRepository.findById(id);
        if (v == null) return null;

        if (!vecPosetio) {
            vestRepository.incrementVisitCount(id);
            v.setBrojPoseta(v.getBrojPoseta() + 1);
        }
        return mapToResponse(v);
    }

    public VestResponse getById(int id) {
        return getByIdWithVisitLogic(id, true);
    }

    public void create(VestRequest req, int autorId) {
        validate(req);
        if (autorId <= 0) throw new RuntimeException("Autor vesti nije prepoznat!");

        Vest v = new Vest(0, req.getNaslov(), req.getTekst(), null, 0, 0, 0, autorId, req.getKategorijaId());
        vestRepository.create(v, req.getTagovi());
    }

    public void update(int id, VestRequest req) {
        validate(req);
        Vest v = vestRepository.findById(id);
        if (v == null) throw new RuntimeException("Vest ne postoji.");
        
        v.setNaslov(req.getNaslov());
        v.setTekst(req.getTekst());
        v.setKategorijaId(req.getKategorijaId());
        
        vestRepository.update(v, req.getTagovi());
    }

    public void delete(int id) { 
        vestRepository.delete(id); 
    }

    public void likeVest(int id) {
        if (vestRepository.findById(id) == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        vestRepository.incrementLikes(id);
    }

    public void dislikeVest(int id) {
        if (vestRepository.findById(id) == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        vestRepository.incrementDislikes(id);
    }

    public void changeVoteToLike(int id, String prethodniGlas) {
        if (vestRepository.findById(id) == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        if ("LIKE".equals(prethodniGlas)) {
            throw new RuntimeException("Već ste lajkovali ovu vest.");
        }

        if ("DISLIKE".equals(prethodniGlas)) {
            vestRepository.decrementDislikes(id);
        }

        vestRepository.incrementLikes(id);
    }

    public void changeVoteToDislike(int id, String prethodniGlas) {
        if (vestRepository.findById(id) == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        if ("DISLIKE".equals(prethodniGlas)) {
            throw new RuntimeException("Već ste dislajkovali ovu vest.");
        }

        if ("LIKE".equals(prethodniGlas)) {
            vestRepository.decrementLikes(id);
        }

        vestRepository.incrementDislikes(id);
    }

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

        List<String> tagoviReči = tagRepository.getTagsForVest(v.getId()).stream()
                .map(t -> t.getKljucnaRec())
                .collect(Collectors.toList());
        res.setTagovi(tagoviReči);

        return res;
    }

    public List<VestResponse> getTopByReactions() {
        List<Vest> vesti = vestRepository.findTopByReactions(3);
        List<VestResponse> response = new ArrayList<>();

        for (Vest vest : vesti) {
            response.add(mapToResponse(vest));
        }

        return response;
    }

    public List<VestResponse> getRelatedByTags(int vestId) {
        if (vestRepository.findById(vestId) == null) {
            throw new RuntimeException("Vest ne postoji.");
        }

        List<Vest> vesti = vestRepository.findRelatedByTags(vestId, 3);
        List<VestResponse> response = new ArrayList<>();

        for (Vest vest : vesti) {
            response.add(mapToResponse(vest));
        }

        return response;
    }
}
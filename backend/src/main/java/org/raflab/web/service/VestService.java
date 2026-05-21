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

    public List<VestResponse> getAll(int page) {
        int offset = (page - 1) * 10;
        return vestRepository.findAllPaginated(offset, 10).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<VestResponse> search(String query, int page) {
        int offset = (page - 1) * 10;
        return vestRepository.search(query, offset, 10).stream()
                .map(this::mapToResponse).collect(Collectors.toList());
    }

    public VestResponse getById(int id) {
        Vest v = vestRepository.findById(id);
        if (v == null) return null;
        vestRepository.incrementVisitCount(id);
        return mapToResponse(v);
    }

    public void create(VestRequest req, int autorId) {
        validate(req);
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

    private void validate(VestRequest req) {
        if (req.getNaslov() == null || req.getNaslov().trim().isEmpty()) throw new RuntimeException("Naslov je obavezan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst je obavezan!");
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
        res.setTagovi(tagRepository.getTagsForVest(v.getId()).stream()
                .map(t -> t.getKljucnaRec()).collect(Collectors.toList()));
        return res;
    }

    public void likeVest(int id) { vestRepository.incrementLikes(id); }
    public void dislikeVest(int id) { vestRepository.incrementDislikes(id); }
}
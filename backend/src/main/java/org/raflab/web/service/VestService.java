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
        if (page < 1) page = 1;
        int limit = 10;
        int offset = (page - 1) * limit;
        return vestRepository.findAllPaginated(offset, limit).stream()
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
        // VALIDACIJA
        if (req.getNaslov() == null || req.getNaslov().trim().isEmpty()) throw new RuntimeException("Naslov je obavezan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst vesti je obavezan!");
        if (req.getKategorijaId() <= 0) throw new RuntimeException("Morate izabrati kategoriju!");
        if (autorId <= 0) throw new RuntimeException("Autor vesti nije prepoznat!");

        Vest v = new Vest(0, req.getNaslov(), req.getTekst(), null, 0, 0, 0, autorId, req.getKategorijaId());
        vestRepository.create(v, req.getTagovi());
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

        List<String> tagoviReči = tagRepository.getTagsForVest(v.getId()).stream()
                .map(t -> t.getKljucnaRec())
                .collect(Collectors.toList());
        res.setTagovi(tagoviReči);

        return res;
    }

    public void likeVest(int id) {
        vestRepository.incrementLikes(id);
    }

    public void dislikeVest(int id) {
        vestRepository.incrementDislikes(id);
    }
}
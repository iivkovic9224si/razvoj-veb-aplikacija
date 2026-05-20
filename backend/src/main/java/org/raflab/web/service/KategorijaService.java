package org.raflab.web.service;

import org.raflab.web.dto.request.KategorijaRequest;
import org.raflab.web.dto.response.KategorijaResponse;
import org.raflab.web.model.Kategorija;
import org.raflab.web.repository.KategorijaRepository;
import java.util.List;
import java.util.stream.Collectors;

public class KategorijaService {
    private final KategorijaRepository kategorijaRepository = new KategorijaRepository();

    public List<KategorijaResponse> getAll(int page) {
        int limit = 10;
        int offset = (page - 1) * limit;
        return kategorijaRepository.findAllPaginated(offset, limit).stream()
                .map(k -> new KategorijaResponse(k.getId(), k.getIme(), k.getOpis()))
                .collect(Collectors.toList());
    }

    public void create(KategorijaRequest request) {
        kategorijaRepository.create(new Kategorija(0, request.getIme(), request.getOpis()));
    }

    public void update(int id, KategorijaRequest request) {
        kategorijaRepository.update(new Kategorija(id, request.getIme(), request.getOpis()));
    }

    public void delete(int id) {
        kategorijaRepository.delete(id);
    }
}
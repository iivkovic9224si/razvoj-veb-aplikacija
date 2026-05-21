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
        if (page < 1) page = 1;
        int limit = 10;
        int offset = (page - 1) * limit;
        return kategorijaRepository.findAllPaginated(offset, limit).stream()
                .map(k -> new KategorijaResponse(k.getId(), k.getIme(), k.getOpis()))
                .collect(Collectors.toList());
    }

    public void create(KategorijaRequest request) {
        if (request.getIme() == null || request.getIme().trim().isEmpty()) {
            throw new RuntimeException("Ime kategorije je obavezno!");
        }
        kategorijaRepository.create(new Kategorija(0, request.getIme(), request.getOpis()));
    }

    public void update(int id, KategorijaRequest request) {
        if (id <= 0) throw new RuntimeException("ID kategorije nije validan!");
        if (request.getIme() == null || request.getIme().trim().isEmpty()) {
            throw new RuntimeException("Ime kategorije ne može biti prazno!");
        }
        kategorijaRepository.update(new Kategorija(id, request.getIme(), request.getOpis()));
    }

    public void delete(int id) {
        if (id <= 0) throw new RuntimeException("ID nije validan!");

        // Specifikacija: Provera da li kategorija ima vesti
        if (kategorijaRepository.hasNews(id)) {
            throw new RuntimeException("Nije moguće obrisati kategoriju koja sadrži vesti!");
        }

        kategorijaRepository.delete(id);
    }
}
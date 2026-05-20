package org.raflab.web.service;

import org.raflab.web.dto.request.KorisnikRequest;
import org.raflab.web.dto.response.KorisnikResponse;
import org.raflab.web.model.Korisnik;
import org.raflab.web.repository.KorisnikRepository;
import org.raflab.web.utils.HashUtils;
import java.util.List;
import java.util.stream.Collectors;

public class KorisnikService {
    private final KorisnikRepository korisnikRepository = new KorisnikRepository();

    public KorisnikResponse login(KorisnikRequest request) {
        if (request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new RuntimeException("Email nije u dobrom formatu!");
        }
        if (request.getLozinka() == null || request.getLozinka().length() < 4) {
            throw new RuntimeException("Lozinka mora imati bar 4 karaktera!");
        }
        String hashedLozinka = HashUtils.sha256(request.getLozinka());
        Korisnik k = korisnikRepository.findKorisnik(request.getEmail(), hashedLozinka);
        return (k != null) ? mapToResponse(k) : null;
    }

    public List<KorisnikResponse> findAll(int page) {
        if (page < 1) page = 1;
        int limit = 10;
        int offset = (page - 1) * limit;
        return korisnikRepository.findAllPaginated(offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public int getTotalPages() {
        int total = korisnikRepository.countAll();
        return (int) Math.ceil((double) total / 10);
    }

    private KorisnikResponse mapToResponse(Korisnik k) {
        return new KorisnikResponse(k.getId(), k.getEmail(), k.getIme(),
                k.getPrezime(), k.getTip(), k.getStatus(), null);
    }
}
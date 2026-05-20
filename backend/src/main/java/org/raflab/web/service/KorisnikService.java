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
        String hashedLozinka = HashUtils.sha256(request.getLozinka());
        Korisnik k = korisnikRepository.findKorisnik(request.getEmail(), hashedLozinka);
        return (k != null) ? mapToResponse(k) : null;
    }

    public List<KorisnikResponse> findAll(int page) {
        int limit = 10;
        int offset = (page - 1) * limit;
        return korisnikRepository.findAllPaginated(offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) korisnikRepository.countAll() / 10);
    }

    private KorisnikResponse mapToResponse(Korisnik k) {
        return new KorisnikResponse(k.getId(), k.getEmail(), k.getIme(),
                k.getPrezime(), k.getTip(), k.getStatus(), null);
    }
}
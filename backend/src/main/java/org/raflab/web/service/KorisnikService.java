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
        String hashedLozinka = HashUtils.sha256(request.getLozinka());
        Korisnik k = korisnikRepository.findKorisnik(request.getEmail(), hashedLozinka);
        return (k != null) ? mapToResponse(k) : null;
    }

    public void addKorisnik(KorisnikRequest req) {
        if (req.getEmail() == null || req.getEmail().isEmpty()) throw new RuntimeException("Email je obavezan!");
        if (korisnikRepository.findByEmail(req.getEmail()) != null) throw new RuntimeException("Email već postoji!");
        if (!req.getLozinka().equals(req.getLozinkaPotvrda())) throw new RuntimeException("Lozinke se ne podudaraju!");

        String hash = HashUtils.sha256(req.getLozinka());
        Korisnik k = new Korisnik(0, req.getEmail(), req.getIme(), req.getPrezime(), req.getTip(), "AKTIVAN", hash);
        korisnikRepository.create(k);
    }

    public void updateKorisnik(int id, KorisnikRequest req) {
        Korisnik postojeci = korisnikRepository.findById(id);
        if (postojeci == null) throw new RuntimeException("Korisnik nije pronađen.");

        postojeci.setEmail(req.getEmail());
        postojeci.setIme(req.getIme());
        postojeci.setPrezime(req.getPrezime());
        postojeci.setTip(req.getTip());

        korisnikRepository.update(postojeci);
    }

    public void toggleStatus(int id) {
        Korisnik k = korisnikRepository.findById(id);
        if (k == null) throw new RuntimeException("Korisnik nije pronađen.");
        if (k.getTip().equals("ADMINISTRATOR")) throw new RuntimeException("Adminu se ne može menjati status.");

        String noviStatus = k.getStatus().equals("AKTIVAN") ? "NEAKTIVAN" : "AKTIVAN";
        korisnikRepository.updateStatus(id, noviStatus);
    }

    public KorisnikResponse getById(int id) {
        Korisnik k = korisnikRepository.findById(id);
        return (k != null) ? mapToResponse(k) : null;
    }

    public List<KorisnikResponse> findAll(int page) {
        int limit = 10;
        int offset = (page - 1) * limit;
        return korisnikRepository.findAllPaginated(offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private KorisnikResponse mapToResponse(Korisnik k) {
        return new KorisnikResponse(k.getId(), k.getEmail(), k.getIme(),
                k.getPrezime(), k.getTip(), k.getStatus(), null);
    }
}
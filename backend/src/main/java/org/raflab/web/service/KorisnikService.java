package org.raflab.web.service;

import org.raflab.web.dto.request.KorisnikRequest;
import org.raflab.web.dto.response.KorisnikResponse;
import org.raflab.web.dto.response.PagedResponse;
import org.raflab.web.model.Korisnik;
import org.raflab.web.repository.KorisnikRepository;
import org.raflab.web.utils.HashUtils;
import java.util.List;
import java.util.stream.Collectors;

public class KorisnikService {
    private final KorisnikRepository korisnikRepository = new KorisnikRepository();

    // Metoda za LOGIN (koju Controller traži)
    public KorisnikResponse login(KorisnikRequest req) {
        Korisnik k = korisnikRepository.findByEmail(req.getEmail());
        if (k == null || !k.getLozinka().equals(HashUtils.sha256(req.getLozinka()))) {
            throw new RuntimeException("Neispravan email ili lozinka!");
        }
        return new KorisnikResponse(k.getId(), k.getEmail(), k.getIme(), k.getPrezime(), k.getTip(), k.getStatus(), null);
    }

    // Metoda za GET BY ID
    public KorisnikResponse getById(int id) {
        Korisnik k = korisnikRepository.findById(id);
        if (k == null) return null;
        return new KorisnikResponse(k.getId(), k.getEmail(), k.getIme(), k.getPrezime(), k.getTip(), k.getStatus(), null);
    }

    // Metoda za TOGGLE STATUS (Aktivacija/Deaktivacija)
    public void toggleStatus(int id) {
        Korisnik k = korisnikRepository.findById(id);
        if (k == null) throw new RuntimeException("Korisnik ne postoji");

        String noviStatus = k.getStatus().equals("AKTIVAN") ? "DEAKTIVIRAN" : "AKTIVAN";
        korisnikRepository.updateStatus(id, noviStatus);
    }

    public void addKorisnik(KorisnikRequest req) {
        if (korisnikRepository.findByEmail(req.getEmail()) != null) {
            throw new RuntimeException("Korisnik sa tim emailom već postoji!");
        }
        if (req.getLozinka() == null || !req.getLozinka().equals(req.getLozinkaPotvrda())) {
            throw new RuntimeException("Lozinke se ne podudaraju!");
        }

        String hash = HashUtils.sha256(req.getLozinka());
        Korisnik k = new Korisnik(0, req.getEmail(), req.getIme(), req.getPrezime(), req.getTip(), "AKTIVAN", hash);
        korisnikRepository.create(k);
    }

    public void updateKorisnik(int id, KorisnikRequest req) {
        Korisnik postojeci = korisnikRepository.findById(id);
        if (postojeci == null) throw new RuntimeException("Korisnik nije pronađen.");

        Korisnik saIstimEmailom = korisnikRepository.findByEmail(req.getEmail());
        if (saIstimEmailom != null && saIstimEmailom.getId() != id) {
            throw new RuntimeException("Email je već zauzet!");
        }

        postojeci.setEmail(req.getEmail());
        postojeci.setIme(req.getIme());
        postojeci.setPrezime(req.getPrezime());
        postojeci.setTip(req.getTip());

        if (req.getLozinka() != null && !req.getLozinka().isEmpty()) {
            if (!req.getLozinka().equals(req.getLozinkaPotvrda())) {
                throw new RuntimeException("Lozinke se ne podudaraju!");
            }
            postojeci.setLozinka(HashUtils.sha256(req.getLozinka()));
        }

        korisnikRepository.update(postojeci);
    }

    public PagedResponse<KorisnikResponse> findAll(int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;
        List<KorisnikResponse> podaci = korisnikRepository.findAllPaginated(offset, limit).stream()
                .map(k -> new KorisnikResponse(k.getId(), k.getEmail(), k.getIme(), k.getPrezime(), k.getTip(), k.getStatus(), null))
                .collect(Collectors.toList());
        int total = korisnikRepository.countAll();
        return new PagedResponse<>(podaci, (int) Math.ceil((double) total / limit));
    }
}
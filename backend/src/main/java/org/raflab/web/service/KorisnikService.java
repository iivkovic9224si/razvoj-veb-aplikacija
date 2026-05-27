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

    public KorisnikResponse login(KorisnikRequest req) {
        Korisnik k = korisnikRepository.findByEmail(req.getEmail());

        if (k == null || !k.getLozinka().equals(HashUtils.sha256(req.getLozinka()))) {
            throw new RuntimeException("Neispravan email ili lozinka!");
        }

        return new KorisnikResponse(
                k.getId(),
                k.getEmail(),
                k.getIme(),
                k.getPrezime(),
                k.getTip(),
                k.getStatus(),
                null
        );
    }

    public KorisnikResponse getById(int id) {
        Korisnik k = korisnikRepository.findById(id);

        if (k == null) {
            return null;
        }

        return new KorisnikResponse(
                k.getId(),
                k.getEmail(),
                k.getIme(),
                k.getPrezime(),
                k.getTip(),
                k.getStatus(),
                null
        );
    }

    public void toggleStatus(int id) {
        Korisnik k = korisnikRepository.findById(id);

        if (k == null) {
            throw new RuntimeException("Korisnik ne postoji");
        }

        String noviStatus = k.getStatus().equals("AKTIVAN") ? "NEAKTIVAN" : "AKTIVAN";
        korisnikRepository.updateStatus(id, noviStatus);
    }

    public void addKorisnik(KorisnikRequest req) {
        validirajOsnovnePodatke(req);
        validirajLozinku(req.getLozinka(), req.getLozinkaPotvrda(), true);

        if (korisnikRepository.findByEmail(req.getEmail().trim()) != null) {
            throw new RuntimeException("Korisnik sa tim emailom već postoji!");
        }

        String hash = HashUtils.sha256(req.getLozinka());

        Korisnik k = new Korisnik(
                0,
                req.getEmail().trim(),
                req.getIme().trim(),
                req.getPrezime().trim(),
                req.getTip(),
                "AKTIVAN",
                hash
        );

        korisnikRepository.create(k);
    }

    public void updateKorisnik(int id, KorisnikRequest req) {
        Korisnik postojeci = korisnikRepository.findById(id);

        if (postojeci == null) {
            throw new RuntimeException("Korisnik nije pronađen.");
        }

        validirajOsnovnePodatke(req);

        Korisnik saIstimEmailom = korisnikRepository.findByEmail(req.getEmail().trim());

        if (saIstimEmailom != null && saIstimEmailom.getId() != id) {
            throw new RuntimeException("Email je već zauzet!");
        }

        postojeci.setEmail(req.getEmail().trim());
        postojeci.setIme(req.getIme().trim());
        postojeci.setPrezime(req.getPrezime().trim());
        postojeci.setTip(req.getTip());

        if (req.getLozinka() != null && !req.getLozinka().isBlank()) {
            validirajLozinku(req.getLozinka(), req.getLozinkaPotvrda(), false);
            postojeci.setLozinka(HashUtils.sha256(req.getLozinka()));
        }

        korisnikRepository.update(postojeci);
    }

    private void validirajOsnovnePodatke(KorisnikRequest req) {
        if (req == null) {
            throw new RuntimeException("Podaci korisnika nisu poslati.");
        }

        if (isBlank(req.getIme())) {
            throw new RuntimeException("Ime je obavezno.");
        }

        if (isBlank(req.getPrezime())) {
            throw new RuntimeException("Prezime je obavezno.");
        }

        if (isBlank(req.getEmail())) {
            throw new RuntimeException("Email je obavezan.");
        }

        if (!req.getEmail().trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new RuntimeException("Email nije u ispravnom formatu.");
        }

        if (isBlank(req.getTip())) {
            throw new RuntimeException("Tip korisnika je obavezan.");
        }
    }

    private void validirajLozinku(String lozinka, String lozinkaPotvrda, boolean obavezna) {
        if (obavezna && isBlank(lozinka)) {
            throw new RuntimeException("Lozinka je obavezna.");
        }

        if (obavezna && isBlank(lozinkaPotvrda)) {
            throw new RuntimeException("Potvrda lozinke je obavezna.");
        }

        if (!obavezna && isBlank(lozinka)) {
            return;
        }

        if (lozinka.length() < 8) {
            throw new RuntimeException("Lozinka mora imati barem 8 karaktera.");
        }

        if (isBlank(lozinkaPotvrda)) {
            throw new RuntimeException("Potvrda lozinke je obavezna.");
        }

        if (!lozinka.equals(lozinkaPotvrda)) {
            throw new RuntimeException("Lozinke se ne podudaraju!");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public PagedResponse<KorisnikResponse> findAll(int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;

        List<KorisnikResponse> podaci = korisnikRepository.findAllPaginated(offset, limit)
                .stream()
                .map(k -> new KorisnikResponse(
                        k.getId(),
                        k.getEmail(),
                        k.getIme(),
                        k.getPrezime(),
                        k.getTip(),
                        k.getStatus(),
                        null
                ))
                .collect(Collectors.toList());

        int total = korisnikRepository.countAll();

        return new PagedResponse<>(podaci, (int) Math.ceil((double) total / limit));
    }
}
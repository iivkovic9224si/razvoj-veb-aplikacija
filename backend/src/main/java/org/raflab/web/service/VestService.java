package org.raflab.web.service;

import org.raflab.web.dto.request.VestRequest;
import org.raflab.web.dto.response.VestResponse;
import org.raflab.web.dto.response.PagedResponse;
import org.raflab.web.model.Vest;
import org.raflab.web.repository.VestRepository;
import org.raflab.web.repository.TagRepository;
import java.util.List;
import java.util.stream.Collectors;

public class VestService {
    private final VestRepository vestRepository = new VestRepository();
    private final TagRepository tagRepository = new TagRepository();

    // UNIVERZALNA RUTA ZA LISTANJE, PRETRAGU I FILTERE (Zadržan i spojen kod iz backend-smallStuffFixing)
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

    // NAJČITANIJE VESTI - Prilagođeno paginaciji iz kontrolera i zahtevu od 30 dana
    public PagedResponse<VestResponse> getMostRead(int page) {
        int limit = 10;
        int offset = (Math.max(1, page) - 1) * limit;

        // Koristi se findMostReadPaginated sa upitom koji filtrira poslednjih 30 dana
        List<VestResponse> podaci = vestRepository.findMostReadPaginated(offset, limit).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        // Izvlačimo ukupan broj najčitanijih vesti u poslednjih 30 dana radi tačne paginacije
        int total = vestRepository.countMostReadLast30Days(); 
        int totalPages = (int) Math.ceil((double) total / limit);

        return new PagedResponse<>(podaci, totalPages);
    }

    // POJEDINAČNA VEST - Izmenjena tako da prihvata odluku iz kontrolera/sesije da li treba uvećati brojač
    public VestResponse getByIdWithVisitLogic(int id, boolean vecPosetio) {
        if (id <= 0) throw new RuntimeException("ID vesti nije validan!");
        Vest v = vestRepository.findById(id);
        if (v == null) return null;
        
        // Broj poseta se uvećava samo ako korisnik u ovoj sesiji već nije otvorio tu vest
        if (!vecPosetio) {
            vestRepository.incrementVisitCount(id);
            v.setBrojPoseta(v.getBrojPoseta() + 1); // Ažuriramo objekat u memoriji da odmah vrati tačan broj
        }
        return mapToResponse(v);
    }

    // Zadržana metoda getById radi kompatibilnosti ako se poziva na drugim mestima na bekendu
    public VestResponse getById(int id) {
        return getByIdWithVisitLogic(id, true);
    }

    public void create(VestRequest req, int autorId) {
        validate(req); // Iskorišćena kolegina privatna metoda za validaciju
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
        vestRepository.incrementLikes(id); 
    }
    
    public void dislikeVest(int id) { 
        vestRepository.incrementDislikes(id); 
    }

    private void validate(VestRequest req) {
        if (req.getNaslov() == null || req.getNaslov().trim().isEmpty()) throw new RuntimeException("Naslov je obavezan!");
        if (req.getTekst() == null || req.getTekst().trim().isEmpty()) throw new RuntimeException("Tekst je obavezan!");
        if (req.getKategorijaId() <= 0) throw new RuntimeException("Kategorija je obavezna!");
    }

    // ISPRAVLJENO MAPIRANJE: Sada popunjava tekstualna imena koja se traže na platformi
    private VestResponse mapToResponse(Vest v) {
        VestResponse res = new VestResponse();
        res.setId(v.getId());
        res.setNaslov(v.getNaslov());
        res.setTekst(v.getTekst());
        res.setVremeKreiranja(v.getVremeKreiranja());
        res.setBrojPoseta(v.getBrojPoseta());
        res.setBrojLajkova(v.getBrojLajkova());
        res.setBrojDislajkova(v.getBrojDislajkova());

        // Pozivanje metoda iz repozitorijuma kako bi se izbegao null na frontendu
        res.setKategorijaIme(vestRepository.getKategorijaIme(v.getKategorijaId()));
        res.setAutorIme(vestRepository.getAutorImePrezime(v.getAutorId()));

        List<String> tagoviReči = tagRepository.getTagsForVest(v.getId()).stream()
                .map(t -> t.getKljucnaRec())
                .collect(Collectors.toList());
        res.setTagovi(tagoviReči);

        return res;
    }
}
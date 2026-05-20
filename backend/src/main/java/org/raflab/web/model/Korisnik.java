package org.raflab.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Korisnik {
    private int id;
    private String email;
    private String ime;
    private String prezime;
    private String tip;    // U bazi: 'ADMINISTRATOR' ili 'STVARALAC_SADRZAJA'
    private String status; // U bazi: 'AKTIVAN' ili 'NEAKTIVAN'
    private String lozinka;
}
package org.raflab.web.dto.request;

import lombok.Data;

@Data
public class KorisnikRequest {
    private String email;
    private String lozinka;
    private String lozinkaPotvrda; // Za potvrdu lozinke pri kreiranju
    private String ime;
    private String prezime;
    private String tip;
    private String status;
}
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
    private String tip; // "admin" ili "autor"
    private int status; // 1 za aktivan, 0 za neaktivan
    private String lozinka;
}
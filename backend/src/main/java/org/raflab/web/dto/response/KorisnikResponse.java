package org.raflab.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KorisnikResponse {
    private int id;
    private String email;
    private String ime;
    private String prezime;
    private String tip;
    private String status;
    private String jwt; // Token koji kolega dodaje
}
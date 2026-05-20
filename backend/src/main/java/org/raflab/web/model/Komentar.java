package org.raflab.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Komentar {
    private int id;
    private String autor;
    private String tekst;
    private Date vremeKreiranja;
    private int brojLajkova;     // Dodato prema SQL-u
    private int brojDislajkova;  // Dodato prema SQL-u
    private int vestId;
}
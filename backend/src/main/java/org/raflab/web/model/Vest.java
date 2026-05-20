package org.raflab.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vest {
    private int id;
    private String naslov;
    private String tekst;
    private Date vremeKreiranja;
    private int brojPoseta;
    private int autorId;
    private int kategorijaId;
}
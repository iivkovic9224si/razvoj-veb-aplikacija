package org.raflab.web.dto.response;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class VestResponse {
    private int id;
    private String naslov;
    private String tekst;
    private Date vremeKreiranja;
    private int brojPoseta;
    private int brojLajkova;
    private int brojDislajkova;
    private String autorIme;
    private String kategorijaIme;
    private List<String> tagovi;
}
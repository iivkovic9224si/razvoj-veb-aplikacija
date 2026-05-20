package org.raflab.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Date;

@Data
@AllArgsConstructor
public class KomentarResponse {
    private int id;
    private String autor;
    private String tekst;
    private Date vremeKreiranja;
    private int brojLajkova;
    private int brojDislajkova;
}
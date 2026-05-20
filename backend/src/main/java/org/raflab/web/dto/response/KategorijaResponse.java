package org.raflab.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KategorijaResponse {
    private int id;
    private String ime;
    private String opis;
}
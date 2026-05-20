package org.raflab.web.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class VestRequest {
    private String naslov;
    private String tekst;
    private int kategorijaId;
    private List<String> tagovi;
}
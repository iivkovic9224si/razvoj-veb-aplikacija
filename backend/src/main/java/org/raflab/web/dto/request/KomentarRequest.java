package org.raflab.web.dto.request;

import lombok.Data;

@Data
public class KomentarRequest {
    private int vestId;
    private String autor;
    private String tekst;
}
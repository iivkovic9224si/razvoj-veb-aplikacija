package org.raflab.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Kategorija {
    private int id;
    private String ime;
    private String opis;
}
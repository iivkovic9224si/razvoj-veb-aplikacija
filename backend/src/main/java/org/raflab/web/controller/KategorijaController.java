package org.raflab.web.controller;

import com.google.gson.Gson;
import org.raflab.web.dto.request.KategorijaRequest;
import org.raflab.web.service.KategorijaService;
import static spark.Spark.*;

public class KategorijaController {
    private final KategorijaService kategorijaService = new KategorijaService();
    private final Gson gson = new Gson();

    public void registerRoutes() {
        get("/api/kategorije", (req, res) -> {
            int page = req.queryParams("page") != null ? Integer.parseInt(req.queryParams("page")) : 1;
            return gson.toJson(kategorijaService.getAll(page));
        });

        post("/api/kategorije", (req, res) -> {
            KategorijaRequest reqDto = gson.fromJson(req.body(), KategorijaRequest.class);
            kategorijaService.create(reqDto);
            res.status(201);
            return gson.toJson("Kategorija dodata");
        });

        put("/api/kategorije/:id", (req, res) -> {
            int id = Integer.parseInt(req.params(":id"));
            KategorijaRequest reqDto = gson.fromJson(req.body(), KategorijaRequest.class);
            kategorijaService.update(id, reqDto);
            return gson.toJson("Kategorija izmenjena");
        });

        delete("/api/kategorije/:id", (req, res) -> {
            try {
                kategorijaService.delete(Integer.parseInt(req.params(":id")));
                return gson.toJson("Kategorija obrisana");
            } catch (RuntimeException e) {
                res.status(400); // Bad Request jer ne dozvoljavamo brisanje
                return gson.toJson(e.getMessage());
            }
        });
    }
}
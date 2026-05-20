package org.raflab.web;

import static spark.Spark.*;

public class MainApplication {
    public static void main(String[] args) {
        port(4567);
        get("/test", (req, res) -> "Backend je konacno spreman!");
        System.out.println("Server radi na http://localhost:4567/test");
    }
}
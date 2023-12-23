package com.shopme.admin.export;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class Exporter<T> {
    private ExporterStrategy<T> exporterStrategy;

    public Exporter(ExporterStrategy<T> exporterStrategy) {
        this.exporterStrategy = exporterStrategy;
    }

    public void export(List<T> objects, HttpServletResponse response) throws IOException {
        exporterStrategy.export(objects, response);
    }

}

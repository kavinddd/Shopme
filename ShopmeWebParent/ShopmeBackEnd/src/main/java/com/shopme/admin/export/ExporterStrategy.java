package com.shopme.admin.export;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ExporterStrategy<T> {

    void setResponseHeader(HttpServletResponse response,
                                  String contentType,
                                  String extension,
                                String objectName);
    void export(List<T> objects, HttpServletResponse response) throws IOException;

}

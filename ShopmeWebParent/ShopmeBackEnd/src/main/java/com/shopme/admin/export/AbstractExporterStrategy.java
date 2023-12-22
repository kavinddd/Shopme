package com.shopme.admin.export;

import jakarta.servlet.http.HttpServletResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractExporterStrategy<T> implements ExporterStrategy<T> {
    @Override
    public void setResponseHeader(HttpServletResponse response,
                                  String contentType,
                                  String extension,
                                  String objectName) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormatter.format(new Date());
        String fileName = objectName + "_" + timeStamp + extension;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename= " + fileName;
        response.setHeader(headerKey, headerValue);
    }
}

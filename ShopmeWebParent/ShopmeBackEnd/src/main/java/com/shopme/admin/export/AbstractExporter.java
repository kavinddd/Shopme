package com.shopme.admin.export;

import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.boot.model.relational.Exportable;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public abstract class AbstractExporter {
    public void setResponseHeader(HttpServletResponse response,
                                  String contentType,
                                  String extension) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timeStamp = dateFormatter.format(new Date());
        String fileName = "users_" + timeStamp + extension;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename= " + fileName;
        response.setHeader(headerKey, headerValue);
    }
    abstract void export(List<User> users, HttpServletResponse response) throws IOException;
}

package com.shopme.admin.export;

import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCsvExporterStrategy extends AbstractExporterStrategy<User> {
    public void export(List<User> users, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "users");
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};
        String[] fieldMapping = {"id", "email", "firstName", "lastName", "roles", "enabled"};
        csvWriter.writeHeader(csvHeader);
        users.forEach(user -> {
            try {
                csvWriter.write(user, fieldMapping);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        csvWriter.close();
    }
}

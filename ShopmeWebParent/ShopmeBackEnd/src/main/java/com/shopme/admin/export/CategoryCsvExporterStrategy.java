package com.shopme.admin.export;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvExporterStrategy extends AbstractExporterStrategy<Category> {
    public void export(List<Category> categories, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "text/csv", ".csv", "categories");
        ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Category ID", "Name", "Alias", "Enabled"};
        String[] fieldMapping = {"id", "name", "alias", "enabled"};
        csvWriter.writeHeader(csvHeader);
        categories.forEach(category -> {
            try {
                csvWriter.write(category, fieldMapping);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        csvWriter.close();
    }
}

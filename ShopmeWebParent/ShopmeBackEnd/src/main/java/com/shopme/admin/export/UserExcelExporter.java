package com.shopme.admin.export;

import com.shopme.common.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.util.List;

public class UserExcelExporter extends AbstractExporter {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    private static String[] HEADER = {"User ID", "E-mail", "First Name", "Last Name", "Roles", "Enabled"};

    private static int dataLength = HEADER.length;

    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }

    public void createSheet() {
        sheet = workbook.createSheet();
    }

    public void export(List<User> users, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response, "application/octet-stream", ".xlsx");
        createSheet();
        writeDataInSheet(sheet, users);
        autosizeColumns();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();

    }

    private void writeDataInSheet(XSSFSheet sheet, List<User> users) {
        // write header
        writeHeader(sheet);
        // write data
        for (int i = 0; i < users.size(); i++) {
            XSSFRow row = sheet.createRow(i + 1);
            writeDataInRow(row, users.get(i));
        }
    }

    private void writeHeader(XSSFSheet sheet) {
        XSSFRow headerRow = sheet.createRow(0);
        XSSFFont font = workbook.createFont(); font.setFontHeight(16); font.setBold(true);
        XSSFCellStyle cellStyle = workbook.createCellStyle(); cellStyle.setFont(font);

        for (int colIndex = 0; colIndex < dataLength; colIndex++ ) {
            XSSFCell cell = headerRow.createCell(colIndex);
            sheet.autoSizeColumn(colIndex);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(HEADER[colIndex]);
        }
    }

    private void writeDataInRow(XSSFRow row, User user) {
        int cellIndex = 0;
        createCell(row, cellIndex++).setCellValue(user.getId());
        createCell(row, cellIndex++).setCellValue(user.getEmail());
        createCell(row, cellIndex++).setCellValue(user.getFirstName());
        createCell(row, cellIndex++).setCellValue(user.getLastName());
        createCell(row, cellIndex++).setCellValue(user.getRoles().toString());
        createCell(row, cellIndex).setCellValue(user.isEnabled());
    }
    private XSSFCell createCell(XSSFRow row, int cellIndex) {
        XSSFCell cell = row.createCell(cellIndex);
        return cell;
    }

    private void autosizeColumns() {
        for (int col = 0; col < dataLength; col++){
            sheet.autoSizeColumn(col);
        }
    }
}

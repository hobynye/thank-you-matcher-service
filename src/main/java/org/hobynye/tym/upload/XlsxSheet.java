package org.hobynye.tym.upload;

import org.apache.poi.ss.usermodel.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class XlsxSheet implements Closeable {

    private final Workbook workbook;
    private final Map<String, Integer> headerIndex;
    private final List<Row> dataRows;

    private XlsxSheet(Workbook workbook, Map<String, Integer> headerIndex, List<Row> dataRows) {
        this.workbook = workbook;
        this.headerIndex = headerIndex;
        this.dataRows = dataRows;
    }

    static XlsxSheet from(InputStream in) throws IOException {
        Workbook wb = WorkbookFactory.create(in);
        Sheet sheet = wb.getSheetAt(0);
        Row headerRow = sheet.getRow(0);
        Map<String, Integer> idx = new LinkedHashMap<>();
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue().trim().toLowerCase();
            if (!header.isEmpty()) {
                idx.put(header, cell.getColumnIndex());
            }
        }
        List<Row> rows = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) rows.add(row);
        }
        return new XlsxSheet(wb, idx, rows);
    }

    List<RowView> rows() {
        return dataRows.stream()
                .map(r -> new RowView(r, headerIndex))
                .toList();
    }

    @Override
    public void close() throws IOException {
        workbook.close();
    }

    static class RowView {
        private final Row row;
        private final Map<String, Integer> idx;
        private final DataFormatter formatter = new DataFormatter();

        RowView(Row row, Map<String, Integer> idx) {
            this.row = row;
            this.idx = idx;
        }

        String getString(String header) {
            Integer col = idx.get(header.toLowerCase());
            if (col == null) return null;
            Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell == null) return null;
            String val = formatter.formatCellValue(cell).trim();
            return val.isEmpty() ? null : val;
        }

        int getIntOrDefault(String header, int defaultValue) {
            String s = getString(header);
            if (s == null) return defaultValue;
            try {
                return (int) Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        boolean isYes(String header) {
            return "yes".equalsIgnoreCase(getString(header));
        }
    }
}
package org.hobynye.tym.upload;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class XlsxSheetTest {

    @Test
    void readsStringByHeaderName() throws IOException {
        byte[] xlsx = workbook(wb -> {
            Sheet s = wb.createSheet();
            Row header = s.createRow(0);
            header.createCell(0).setCellValue("First Name");
            header.createCell(1).setCellValue("Last Name");
            Row data = s.createRow(1);
            data.createCell(0).setCellValue("Jane");
            data.createCell(1).setCellValue("Doe");
        });

        try (XlsxSheet sheet = XlsxSheet.from(new ByteArrayInputStream(xlsx))) {
            List<XlsxSheet.RowView> rows = sheet.rows();
            assertThat(rows, hasSize(1));
            assertThat(rows.get(0).getString("First Name"), is("Jane"));
            assertThat(rows.get(0).getString("LAST NAME"), is("Doe"));
            assertThat(rows.get(0).getString("missing"), nullValue());
        }
    }

    @Test
    void returnsNullForBlankCell() throws IOException {
        byte[] xlsx = workbook(wb -> {
            Sheet s = wb.createSheet();
            Row header = s.createRow(0);
            header.createCell(0).setCellValue("Name");
            s.createRow(1).createCell(0).setCellValue("");
        });

        try (XlsxSheet sheet = XlsxSheet.from(new ByteArrayInputStream(xlsx))) {
            assertThat(sheet.rows().get(0).getString("name"), nullValue());
        }
    }

    @Test
    void parsesIntOrDefault() throws IOException {
        byte[] xlsx = workbook(wb -> {
            Sheet s = wb.createSheet();
            Row header = s.createRow(0);
            header.createCell(0).setCellValue("Count");
            header.createCell(1).setCellValue("Empty");
            Row data = s.createRow(1);
            data.createCell(0).setCellValue(3);
        });

        try (XlsxSheet sheet = XlsxSheet.from(new ByteArrayInputStream(xlsx))) {
            XlsxSheet.RowView row = sheet.rows().get(0);
            assertThat(row.getIntOrDefault("count", 1), is(3));
            assertThat(row.getIntOrDefault("empty", 1), is(1));
            assertThat(row.getIntOrDefault("missing", 5), is(5));
        }
    }

    @Test
    void isYesReturnsTrueOnlyForYes() throws IOException {
        byte[] xlsx = workbook(wb -> {
            Sheet s = wb.createSheet();
            Row header = s.createRow(0);
            header.createCell(0).setCellValue("Earmarked?");
            header.createCell(1).setCellValue("Other");
            Row data = s.createRow(1);
            data.createCell(0).setCellValue("Yes");
            data.createCell(1).setCellValue("No");
        });

        try (XlsxSheet sheet = XlsxSheet.from(new ByteArrayInputStream(xlsx))) {
            XlsxSheet.RowView row = sheet.rows().get(0);
            assertThat(row.isYes("earmarked?"), is(true));
            assertThat(row.isYes("other"), is(false));
        }
    }

    @Test
    void skipsNullRows() throws IOException {
        byte[] xlsx = workbook(wb -> {
            Sheet s = wb.createSheet();
            Row header = s.createRow(0);
            header.createCell(0).setCellValue("Name");
            s.createRow(1).createCell(0).setCellValue("Alice");
            // row 2 intentionally left null (no createRow call)
            s.createRow(3).createCell(0).setCellValue("Bob");
        });

        try (XlsxSheet sheet = XlsxSheet.from(new ByteArrayInputStream(xlsx))) {
            assertThat(sheet.rows(), hasSize(2));
        }
    }

    @Test
    void emptyWorkbookReturnsNoRows() throws IOException {
        byte[] xlsx = workbook(wb -> wb.createSheet());

        try (XlsxSheet sheet = XlsxSheet.from(new ByteArrayInputStream(xlsx))) {
            assertThat(sheet.rows(), empty());
        }
    }

    private byte[] workbook(WorkbookBuilder builder) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            builder.build(wb);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    @FunctionalInterface
    interface WorkbookBuilder {
        void build(Workbook wb) throws IOException;
    }
}

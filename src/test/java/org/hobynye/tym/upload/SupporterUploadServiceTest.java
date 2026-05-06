package org.hobynye.tym.upload;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.seminar.SeminarRepository;
import org.hobynye.tym.supporter.Supporter;
import org.hobynye.tym.supporter.SupporterRepository;
import org.hobynye.tym.supporter.SupporterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class SupporterUploadServiceTest {

    @Autowired SupporterUploadService service;
    @Autowired SeminarRepository seminarRepository;
    @Autowired SupporterRepository supporterRepository;

    Seminar seminar;

    @BeforeEach
    void setUp() {
        seminar = new Seminar();
        seminar.setName("Spring 2026");
        seminar.setSeminarDate(LocalDate.of(2026, 4, 10));
        seminar.setCreatedBy("admin@hobynye.org");
        seminar = seminarRepository.save(seminar);
    }

    @Test
    void parsesAllDonorFields() throws IOException {
        byte[] xlsx = buildSheet(wb -> {
            Sheet s = wb.createSheet();
            Row h = s.createRow(0);
            h.createCell(0).setCellValue("Organization");
            h.createCell(1).setCellValue("Contact Name");
            h.createCell(2).setCellValue("Donor Type");
            h.createCell(3).setCellValue("Club");
            h.createCell(4).setCellValue("Address 1");
            h.createCell(5).setCellValue("Address 2");
            h.createCell(6).setCellValue("City");
            h.createCell(7).setCellValue("State");
            h.createCell(8).setCellValue("Zip");
            h.createCell(9).setCellValue("Email");
            h.createCell(10).setCellValue("Phone");
            h.createCell(11).setCellValue("Letter Count");
            h.createCell(12).setCellValue("Earmarked Donation?");
            h.createCell(13).setCellValue("Sponsored School");
            h.createCell(14).setCellValue("Sponsored County");
            h.createCell(15).setCellValue("Sponsored JStaff");
            h.createCell(16).setCellValue("Sponsored Ambassador");

            Row r = s.createRow(1);
            r.createCell(0).setCellValue("Greenwich BPO Elks #2223");
            r.createCell(1).setCellValue("Jane Smith");
            r.createCell(2).setCellValue("Civic Organization");
            r.createCell(3).setCellValue("Elks");
            r.createCell(4).setCellValue("130 Bulson Rd");
            r.createCell(5).setCellValue("Suite 4");
            r.createCell(6).setCellValue("Greenwich");
            r.createCell(7).setCellValue("NY");
            r.createCell(8).setCellValue("12834");
            r.createCell(9).setCellValue("jane@elks.org");
            r.createCell(10).setCellValue("5181234567");
            r.createCell(11).setCellValue(2);
            r.createCell(12).setCellValue("Yes");
            r.createCell(13).setCellValue("Greenwich Central School");
            r.createCell(14).setCellValue("Washington");
            r.createCell(15).setCellValue("Brooke Battiato");
            r.createCell(16).setCellValue("Ronan Corr");
        });

        int count = service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(count, is(1));
        List<Supporter> saved = supporterRepository.findBySeminarId(seminar.getId());
        assertThat(saved, hasSize(1));
        Supporter s = saved.get(0);
        assertThat(s.getSupporterType(), is(SupporterType.DONOR));
        assertThat(s.getName(), is("Greenwich BPO Elks #2223"));
        assertThat(s.getContactName(), is("Jane Smith"));
        assertThat(s.getCategory(), is("Civic Organization"));
        assertThat(s.getClub(), is("Elks"));
        assertThat(s.getStreet(), is("130 Bulson Rd"));
        assertThat(s.getStreet2(), is("Suite 4"));
        assertThat(s.getCity(), is("Greenwich"));
        assertThat(s.getState(), is("NY"));
        assertThat(s.getZip(), is("12834"));
        assertThat(s.getEmail(), is("jane@elks.org"));
        assertThat(s.getPhone(), is("5181234567"));
        assertThat(s.getLetterCount(), is(2));
        assertThat(s.getSponsoredSchool(), is("Greenwich Central School"));
        assertThat(s.getSponsoredCounty(), is("Washington"));
        assertThat(s.getSponsoredJStaff(), is("Brooke Battiato"));
        assertThat(s.getSponsoredAmbassador(), is("Ronan Corr"));
    }

    @Test
    void doesNotStoreSponsoredSchoolOrCountyWhenNotEarmarked() throws IOException {
        byte[] xlsx = buildSheet(wb -> {
            Sheet s = wb.createSheet();
            Row h = s.createRow(0);
            h.createCell(0).setCellValue("Organization");
            h.createCell(1).setCellValue("Earmarked Donation?");
            h.createCell(2).setCellValue("Sponsored School");
            h.createCell(3).setCellValue("Sponsored County");

            Row r = s.createRow(1);
            r.createCell(0).setCellValue("Cornwall Lions");
            r.createCell(1).setCellValue("No");
            r.createCell(2).setCellValue("Cornwall Central");
            r.createCell(3).setCellValue("Orange");
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        Supporter saved = supporterRepository.findBySeminarId(seminar.getId()).get(0);
        assertThat(saved.getSponsoredSchool(), nullValue());
        assertThat(saved.getSponsoredCounty(), nullValue());
    }

    @Test
    void defaultsLetterCountToOneWhenBlank() throws IOException {
        byte[] xlsx = buildSheet(wb -> {
            Sheet s = wb.createSheet();
            Row h = s.createRow(0);
            h.createCell(0).setCellValue("Organization");
            h.createCell(1).setCellValue("Letter Count");
            s.createRow(1).createCell(0).setCellValue("Acme Corp");
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(supporterRepository.findBySeminarId(seminar.getId()).get(0).getLetterCount(), is(1));
    }

    @Test
    void skipsRowsWithNoOrganization() throws IOException {
        byte[] xlsx = buildSheet(wb -> {
            Sheet s = wb.createSheet();
            Row h = s.createRow(0);
            h.createCell(0).setCellValue("Organization");
            s.createRow(1).createCell(0).setCellValue("Acme Corp");
            s.createRow(2); // no Organization cell
        });

        int count = service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(count, is(1));
    }

    @Test
    void replacesExistingDonorsOnReupload() throws IOException {
        byte[] first = buildSheet(wb -> {
            Sheet s = wb.createSheet();
            s.createRow(0).createCell(0).setCellValue("Organization");
            s.createRow(1).createCell(0).setCellValue("Old Donor");
        });
        byte[] second = buildSheet(wb -> {
            Sheet s = wb.createSheet();
            s.createRow(0).createCell(0).setCellValue("Organization");
            s.createRow(1).createCell(0).setCellValue("New Donor");
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(first));
        service.upload(seminar.getId(), new ByteArrayInputStream(second));

        List<Supporter> all = supporterRepository.findBySeminarId(seminar.getId());
        assertThat(all, hasSize(1));
        assertThat(all.get(0).getName(), is("New Donor"));
    }

    @Test
    void throws404ForUnknownSeminar() {
        assertThrows(ResponseStatusException.class,
                () -> service.upload(java.util.UUID.randomUUID(), new ByteArrayInputStream(new byte[0])));
    }

    private byte[] buildSheet(SheetBuilder builder) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            builder.build(wb);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }

    @FunctionalInterface
    interface SheetBuilder {
        void build(Workbook wb) throws IOException;
    }
}

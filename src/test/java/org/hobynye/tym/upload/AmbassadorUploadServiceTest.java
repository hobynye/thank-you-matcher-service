package org.hobynye.tym.upload;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hobynye.tym.ambassador.Ambassador;
import org.hobynye.tym.ambassador.AmbassadorRepository;
import org.hobynye.tym.school.School;
import org.hobynye.tym.school.SchoolRepository;
import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.seminar.SeminarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class AmbassadorUploadServiceTest {

    @Autowired AmbassadorUploadService service;
    @Autowired SeminarRepository seminarRepository;
    @Autowired AmbassadorRepository ambassadorRepository;
    @Autowired SchoolRepository schoolRepository;

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
    void parsesAllAmbassadorFields() throws IOException {
        School school = new School();
        school.setName("Cornwall Central High School");
        school.setCounty("Orange");
        school.setState("NY");
        schoolRepository.save(school);

        byte[] xlsx = ambassadorSheet(new String[][]{
                {"First Name", "Last Name", "School Name", "Color", "Group"},
                {"Jane", "Doe", "Cornwall Central High School", "Red", "A"}
        });

        int count = service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(count, is(1));
        List<Ambassador> saved = ambassadorRepository.findBySeminarId(seminar.getId());
        assertThat(saved, hasSize(1));
        Ambassador a = saved.get(0);
        assertThat(a.getFirstName(), is("Jane"));
        assertThat(a.getLastName(), is("Doe"));
        assertThat(a.getSchoolName(), is("Cornwall Central High School"));
        assertThat(a.getColor(), is("Red"));
        assertThat(a.getGroupCode(), is("A"));
        assertThat(a.getCounty(), is("Orange"));
    }

    @Test
    void countyIsNullWhenSchoolNotInTable() throws IOException {
        byte[] xlsx = ambassadorSheet(new String[][]{
                {"First Name", "Last Name", "School Name"},
                {"John", "Smith", "Unknown School"}
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(ambassadorRepository.findBySeminarId(seminar.getId()).get(0).getCounty(), nullValue());
    }

    @Test
    void replacesExistingAmbassadorsOnReupload() throws IOException {
        byte[] first = ambassadorSheet(new String[][]{
                {"First Name", "Last Name", "School Name"},
                {"Old", "Student", "Some School"}
        });
        byte[] second = ambassadorSheet(new String[][]{
                {"First Name", "Last Name", "School Name"},
                {"New", "Student", "Some School"}
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(first));
        service.upload(seminar.getId(), new ByteArrayInputStream(second));

        List<Ambassador> all = ambassadorRepository.findBySeminarId(seminar.getId());
        assertThat(all, hasSize(1));
        assertThat(all.get(0).getFirstName(), is("New"));
    }

    private byte[] ambassadorSheet(String[][] data) throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet s = wb.createSheet();
            for (int i = 0; i < data.length; i++) {
                Row row = s.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    row.createCell(j).setCellValue(data[i][j]);
                }
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            return out.toByteArray();
        }
    }
}

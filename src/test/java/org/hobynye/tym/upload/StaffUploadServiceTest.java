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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class StaffUploadServiceTest {

    @Autowired StaffUploadService service;
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
    void parsesJStaffRow() throws IOException {
        byte[] xlsx = staffSheet(new String[][]{
                {"First Name", "Last Name", "Color", "Group", "Role", "Street", "City", "State", "Zip", "Letter Count"},
                {"Brooke", "Battiato", "Red", "A", "J Staff", "600 Craigville Road", "Chester", "NY", "10918", "2"}
        });

        int count = service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(count, is(1));
        Supporter s = supporterRepository.findBySeminarId(seminar.getId()).get(0);
        assertThat(s.getSupporterType(), is(SupporterType.STAFF));
        assertThat(s.getName(), is("Brooke Battiato"));
        assertThat(s.getRole(), is("J Staff"));
        assertThat(s.getColor(), is("Red"));
        assertThat(s.getGroupCode(), is("A"));
        assertThat(s.getStreet(), is("600 Craigville Road"));
        assertThat(s.getCity(), is("Chester"));
        assertThat(s.getState(), is("NY"));
        assertThat(s.getZip(), is("10918"));
        assertThat(s.getLetterCount(), is(2));
    }

    @Test
    void mapsSpeakerRoleToSpeakerType() throws IOException {
        byte[] xlsx = staffSheet(new String[][]{
                {"First Name", "Last Name", "Role"},
                {"Mike", "Christakis", "Speaker"}
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(supporterRepository.findBySeminarId(seminar.getId()).get(0).getSupporterType(),
                is(SupporterType.SPEAKER));
    }

    @Test
    void mapsPanelistRoleToPanelistType() throws IOException {
        byte[] xlsx = staffSheet(new String[][]{
                {"First Name", "Last Name", "Role"},
                {"Gabrielle", "Fisher", "Panelist- Personal Leadership Panel"}
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(supporterRepository.findBySeminarId(seminar.getId()).get(0).getSupporterType(),
                is(SupporterType.PANELIST));
    }

    @Test
    void defaultsLetterCountToOne() throws IOException {
        byte[] xlsx = staffSheet(new String[][]{
                {"First Name", "Last Name", "Role"},
                {"Brad", "Cech", "Facilitator"}
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        assertThat(supporterRepository.findBySeminarId(seminar.getId()).get(0).getLetterCount(), is(1));
    }

    @Test
    void replacesExistingStaffOnReupload() throws IOException {
        byte[] first = staffSheet(new String[][]{
                {"First Name", "Last Name", "Role"},
                {"Old", "Staff", "J Staff"}
        });
        byte[] second = staffSheet(new String[][]{
                {"First Name", "Last Name", "Role"},
                {"New", "Staff", "Facilitator"}
        });

        service.upload(seminar.getId(), new ByteArrayInputStream(first));
        service.upload(seminar.getId(), new ByteArrayInputStream(second));

        List<Supporter> all = supporterRepository.findBySeminarId(seminar.getId());
        assertThat(all, hasSize(1));
        assertThat(all.get(0).getName(), is("New Staff"));
    }

    @Test
    void doesNotDeleteDonorsOnStaffReupload() throws IOException {
        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setName("Donor Corp");
        supporterRepository.save(donor);

        byte[] xlsx = staffSheet(new String[][]{
                {"First Name", "Last Name", "Role"},
                {"Jane", "Doe", "J Staff"}
        });
        service.upload(seminar.getId(), new ByteArrayInputStream(xlsx));

        List<Supporter> donors = supporterRepository.findBySeminarIdAndSupporterType(seminar.getId(), SupporterType.DONOR);
        assertThat(donors, hasSize(1));
    }

    private byte[] staffSheet(String[][] data) throws IOException {
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
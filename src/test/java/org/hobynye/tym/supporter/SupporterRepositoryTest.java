package org.hobynye.tym.supporter;

import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.seminar.SeminarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class SupporterRepositoryTest {

    @Autowired SupporterRepository supporterRepository;
    @Autowired SeminarRepository seminarRepository;

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
    void savesDonorWithMatchingFields() {
        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setLetterCount(1);
        donor.setFullName("Acme Corp");
        donor.setOrganization("Acme Corp");
        donor.setBeneficiaryFirst("Jane");
        donor.setBeneficiaryLast("Doe");
        donor.setStreet("123 Main St");
        donor.setCity("Albany");
        donor.setState("NY");
        donor.setZip("12201");

        Supporter saved = supporterRepository.save(donor);

        assertThat(saved.getId(), notNullValue());
        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSupporterType(), is(SupporterType.DONOR));
        assertThat(found.getBeneficiaryFirst(), is("Jane"));
        assertThat(found.getOrganization(), is("Acme Corp"));
    }

    @Test
    void savesStaffWithGroupFields() {
        Supporter staff = new Supporter();
        staff.setSeminar(seminar);
        staff.setSupporterType(SupporterType.STAFF);
        staff.setLetterCount(2);
        staff.setFirstName("Brad");
        staff.setLastName("Cech");
        staff.setRole("Facilitator");
        staff.setColor("Red");
        staff.setGroupCode("A");

        Supporter saved = supporterRepository.save(staff);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getColor(), is("Red"));
        assertThat(found.getGroupCode(), is("A"));
        assertThat(found.getRole(), is("Facilitator"));
    }

    @Test
    void findsBySeminarIdAndType() {
        Supporter speaker = new Supporter();
        speaker.setSeminar(seminar);
        speaker.setSupporterType(SupporterType.SPEAKER);
        speaker.setLetterCount(5);
        speaker.setFirstName("Mike");
        speaker.setLastName("Christakis");
        supporterRepository.save(speaker);

        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setFullName("Jane Smith");
        supporterRepository.save(donor);

        List<Supporter> speakers = supporterRepository.findBySeminarIdAndSupporterType(seminar.getId(), SupporterType.SPEAKER);
        assertThat(speakers, hasSize(1));
        assertThat(speakers.get(0).getFirstName(), is("Mike"));

        List<Supporter> all = supporterRepository.findBySeminarId(seminar.getId());
        assertThat(all, hasSize(2));
    }

    @Test
    void deletesBySeminarId() {
        Supporter supporter = new Supporter();
        supporter.setSeminar(seminar);
        supporter.setSupporterType(SupporterType.STAFF);
        supporter.setFirstName("Test");
        supporter.setLastName("User");
        supporterRepository.save(supporter);

        supporterRepository.deleteBySeminarId(seminar.getId());

        assertThat(supporterRepository.findBySeminarId(seminar.getId()), empty());
    }
}

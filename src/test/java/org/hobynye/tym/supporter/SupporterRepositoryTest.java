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
    void savesDonorWithAllFields() {
        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setLetterCount(2);
        donor.setName("Greenwich BPO Elks #2223");
        donor.setContactName("Jane Smith");
        donor.setCategory("Civic Organization");
        donor.setClub("Elks");
        donor.setStreet("130 Bulson Rd");
        donor.setStreet2("Suite 4");
        donor.setCity("Greenwich");
        donor.setState("NY");
        donor.setZip("12834");
        donor.setEmail("secretarygreenwichelks2223@yahoo.com");
        donor.setPhone("5181234567");
        donor.setSponsoredSchool("Greenwich Central School");
        donor.setSponsoredCounty("Washington");
        donor.setSponsoredJStaff("Brooke Battiato");
        donor.setSponsoredAmbassador("Ronan Corr");

        Supporter saved = supporterRepository.save(donor);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSupporterType(), is(SupporterType.DONOR));
        assertThat(found.getLetterCount(), is(2));
        assertThat(found.getName(), is("Greenwich BPO Elks #2223"));
        assertThat(found.getContactName(), is("Jane Smith"));
        assertThat(found.getCategory(), is("Civic Organization"));
        assertThat(found.getClub(), is("Elks"));
        assertThat(found.getStreet(), is("130 Bulson Rd"));
        assertThat(found.getStreet2(), is("Suite 4"));
        assertThat(found.getCity(), is("Greenwich"));
        assertThat(found.getState(), is("NY"));
        assertThat(found.getZip(), is("12834"));
        assertThat(found.getEmail(), is("secretarygreenwichelks2223@yahoo.com"));
        assertThat(found.getPhone(), is("5181234567"));
        assertThat(found.getSponsoredSchool(), is("Greenwich Central School"));
        assertThat(found.getSponsoredCounty(), is("Washington"));
        assertThat(found.getSponsoredJStaff(), is("Brooke Battiato"));
        assertThat(found.getSponsoredAmbassador(), is("Ronan Corr"));
        assertThat(found.getSeminar().getId(), is(seminar.getId()));
    }

    @Test
    void savesOrganizationDonorWithCountyMatch() {
        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setName("Cornwall Lions");
        donor.setCategory("Civic Organization");
        donor.setClub("Lions");
        donor.setSponsoredCounty("Orange");

        Supporter saved = supporterRepository.save(donor);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName(), is("Cornwall Lions"));
        assertThat(found.getSponsoredCounty(), is("Orange"));
    }

    @Test
    void savesSpeakerWithTitleAndRole() {
        Supporter speaker = new Supporter();
        speaker.setSeminar(seminar);
        speaker.setSupporterType(SupporterType.SPEAKER);
        speaker.setLetterCount(5);
        speaker.setName("Mike Christakis");
        speaker.setTitle("VP of Service Delivery");
        speaker.setRole("Speaker");
        speaker.setStreet("1400 Washington Ave");
        speaker.setCity("Albany");
        speaker.setState("NY");
        speaker.setZip("12222");

        Supporter saved = supporterRepository.save(speaker);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName(), is("Mike Christakis"));
        assertThat(found.getTitle(), is("VP of Service Delivery"));
        assertThat(found.getRole(), is("Speaker"));
        assertThat(found.getLetterCount(), is(5));
        assertThat(found.getStreet(), is("1400 Washington Ave"));
        assertThat(found.getCity(), is("Albany"));
        assertThat(found.getState(), is("NY"));
        assertThat(found.getZip(), is("12222"));
    }

    @Test
    void savesPanelistWithRole() {
        Supporter panelist = new Supporter();
        panelist.setSeminar(seminar);
        panelist.setSupporterType(SupporterType.PANELIST);
        panelist.setLetterCount(5);
        panelist.setName("Gabrielle Fisher");
        panelist.setRole("Panelist- Personal Leadership Panel");

        Supporter saved = supporterRepository.save(panelist);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSupporterType(), is(SupporterType.PANELIST));
        assertThat(found.getName(), is("Gabrielle Fisher"));
        assertThat(found.getRole(), is("Panelist- Personal Leadership Panel"));
    }

    @Test
    void savesStaffWithGroupFields() {
        Supporter staff = new Supporter();
        staff.setSeminar(seminar);
        staff.setSupporterType(SupporterType.STAFF);
        staff.setLetterCount(2);
        staff.setName("Brad Cech");
        staff.setRole("Facilitator");
        staff.setColor("Red");
        staff.setGroupCode("A");

        Supporter saved = supporterRepository.save(staff);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName(), is("Brad Cech"));
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
        speaker.setName("Mike Christakis");
        supporterRepository.save(speaker);

        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setName("Jane Smith");
        supporterRepository.save(donor);

        List<Supporter> speakers = supporterRepository.findBySeminarIdAndSupporterType(seminar.getId(), SupporterType.SPEAKER);
        assertThat(speakers, hasSize(1));
        assertThat(speakers.get(0).getName(), is("Mike Christakis"));

        List<Supporter> all = supporterRepository.findBySeminarId(seminar.getId());
        assertThat(all, hasSize(2));
    }

    @Test
    void deletesBySeminarId() {
        Supporter supporter = new Supporter();
        supporter.setSeminar(seminar);
        supporter.setSupporterType(SupporterType.STAFF);
        supporter.setName("Test User");
        supporterRepository.save(supporter);

        supporterRepository.deleteBySeminarId(seminar.getId());

        assertThat(supporterRepository.findBySeminarId(seminar.getId()), empty());
    }
}
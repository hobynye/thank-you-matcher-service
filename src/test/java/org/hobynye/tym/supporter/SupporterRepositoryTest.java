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
        donor.setFullName("Kate Hartley");
        donor.setFirstName("Kate");
        donor.setLastName("Hartley");
        donor.setOrganization(null);
        donor.setDonationInfo("15 cash");
        donor.setDonorType("Individual");
        donor.setBeneficiaryFirst("Ronan");
        donor.setBeneficiaryLast("Corr");
        donor.setSponsoredSchool("SCH001");
        donor.setSponsorCounty("Essex");
        donor.setStreet("276 Main St");
        donor.setCity("North Creek");
        donor.setState("NY");
        donor.setZip("12853");

        Supporter saved = supporterRepository.save(donor);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSupporterType(), is(SupporterType.DONOR));
        assertThat(found.getLetterCount(), is(2));
        assertThat(found.getFullName(), is("Kate Hartley"));
        assertThat(found.getFirstName(), is("Kate"));
        assertThat(found.getLastName(), is("Hartley"));
        assertThat(found.getDonationInfo(), is("15 cash"));
        assertThat(found.getDonorType(), is("Individual"));
        assertThat(found.getBeneficiaryFirst(), is("Ronan"));
        assertThat(found.getBeneficiaryLast(), is("Corr"));
        assertThat(found.getSponsoredSchool(), is("SCH001"));
        assertThat(found.getSponsorCounty(), is("Essex"));
        assertThat(found.getStreet(), is("276 Main St"));
        assertThat(found.getCity(), is("North Creek"));
        assertThat(found.getState(), is("NY"));
        assertThat(found.getZip(), is("12853"));
        assertThat(found.getSeminar().getId(), is(seminar.getId()));
    }

    @Test
    void savesOrganizationDonorWithCountyMatch() {
        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setFullName("Cornwall Lions");
        donor.setOrganization("Cornwall Lions");
        donor.setSponsorCounty("Orange");

        Supporter saved = supporterRepository.save(donor);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getOrganization(), is("Cornwall Lions"));
        assertThat(found.getSponsorCounty(), is("Orange"));
    }

    @Test
    void savesSpeakerWithTitleAndRole() {
        Supporter speaker = new Supporter();
        speaker.setSeminar(seminar);
        speaker.setSupporterType(SupporterType.SPEAKER);
        speaker.setLetterCount(5);
        speaker.setTitle("VP of Service Delivery");
        speaker.setFirstName("Mike");
        speaker.setLastName("Christakis");
        speaker.setRole("Speaker");
        speaker.setStreet("1400 Washington Ave");
        speaker.setCity("Albany");
        speaker.setState("NY");
        speaker.setZip("12222");

        Supporter saved = supporterRepository.save(speaker);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
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
        panelist.setFirstName("Gabrielle");
        panelist.setLastName("Fisher");
        panelist.setRole("Panelist- Personal Leadership Panel");

        Supporter saved = supporterRepository.save(panelist);

        Supporter found = supporterRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getSupporterType(), is(SupporterType.PANELIST));
        assertThat(found.getRole(), is("Panelist- Personal Leadership Panel"));
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
        assertThat(found.getFirstName(), is("Brad"));
        assertThat(found.getLastName(), is("Cech"));
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

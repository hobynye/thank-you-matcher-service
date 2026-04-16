package org.hobynye.tym;

import org.hobynye.tym.ambassador.Ambassador;
import org.hobynye.tym.ambassador.AmbassadorRepository;
import org.hobynye.tym.match.Match;
import org.hobynye.tym.match.MatchRepository;
import org.hobynye.tym.school.School;
import org.hobynye.tym.school.SchoolRepository;
import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.seminar.SeminarRepository;
import org.hobynye.tym.supporter.Supporter;
import org.hobynye.tym.supporter.SupporterRepository;
import org.hobynye.tym.supporter.SupporterType;
import org.hobynye.tym.user.AppUser;
import org.hobynye.tym.user.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@Testcontainers
@ActiveProfiles("it")
@Transactional
class DataModelIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("tym_it")
            .withUsername("tym")
            .withPassword("tym");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
    }

    @Autowired SeminarRepository seminarRepository;
    @Autowired AmbassadorRepository ambassadorRepository;
    @Autowired SupporterRepository supporterRepository;
    @Autowired SchoolRepository schoolRepository;
    @Autowired AppUserRepository appUserRepository;
    @Autowired MatchRepository matchRepository;

    @Test
    void seminarCrudRoundTrip() {
        Seminar seminar = new Seminar();
        seminar.setName("Spring 2026");
        seminar.setSeminarDate(LocalDate.of(2026, 4, 10));
        seminar.setCreatedBy("admin@hobynye.org");

        Seminar saved = seminarRepository.save(seminar);

        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getCreatedAt(), notNullValue());

        Seminar found = seminarRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName(), is("Spring 2026"));
        assertThat(found.getSeminarDate(), is(LocalDate.of(2026, 4, 10)));
        assertThat(found.getCreatedBy(), is("admin@hobynye.org"));
    }

    @Test
    void schoolCrudRoundTrip() {
        School school = new School();
        school.setName("Cornwall Central High School");
        school.setCounty("Orange");
        school.setState("NY");

        School saved = schoolRepository.save(school);

        assertThat(saved.getId(), notNullValue());
        School found = schoolRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getName(), is("Cornwall Central High School"));
        assertThat(found.getCounty(), is("Orange"));
        assertThat(found.getState(), is("NY"));

        assertThat(schoolRepository.findByNameIgnoreCase("cornwall central high school").isPresent(), is(true));
    }

    @Test
    void appUserCrudRoundTrip() {
        AppUser admin = new AppUser();
        admin.setEmail("admin@hobynye.org");
        admin.setAdmin(true);

        AppUser saved = appUserRepository.save(admin);

        assertThat(saved.getId(), notNullValue());
        AppUser found = appUserRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getEmail(), is("admin@hobynye.org"));
        assertThat(found.isAdmin(), is(true));

        assertThat(appUserRepository.findByEmailIgnoreCase("ADMIN@HOBYNYE.ORG").isPresent(), is(true));
    }

    @Test
    void ambassadorCrudAndQueryBySeminar() {
        Seminar seminar = savedSeminar("Ambassador IT Seminar");

        Ambassador a = new Ambassador();
        a.setSeminar(seminar);
        a.setFirstName("Jane");
        a.setLastName("Doe");
        a.setSchoolName("Cornwall Central High School");
        a.setColor("Red");
        a.setGroupCode("A");
        a.setCounty("Orange");
        ambassadorRepository.save(a);

        List<Ambassador> found = ambassadorRepository.findBySeminarId(seminar.getId());
        assertThat(found, hasSize(1));
        Ambassador ambassador = found.get(0);
        assertThat(ambassador.getFirstName(), is("Jane"));
        assertThat(ambassador.getLastName(), is("Doe"));
        assertThat(ambassador.getSchoolName(), is("Cornwall Central High School"));
        assertThat(ambassador.getColor(), is("Red"));
        assertThat(ambassador.getGroupCode(), is("A"));
        assertThat(ambassador.getCounty(), is("Orange"));
        assertThat(ambassador.getSeminar().getId(), is(seminar.getId()));

        ambassadorRepository.deleteBySeminarId(seminar.getId());
        assertThat(ambassadorRepository.findBySeminarId(seminar.getId()), empty());
    }

    @Test
    void supporterAllTypesAndQueryBySeminar() {
        Seminar seminar = savedSeminar("Supporter IT Seminar");

        Supporter donor = new Supporter();
        donor.setSeminar(seminar);
        donor.setSupporterType(SupporterType.DONOR);
        donor.setLetterCount(1);
        donor.setFullName("Kate Hartley");
        donor.setFirstName("Kate");
        donor.setLastName("Hartley");
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
        supporterRepository.save(donor);

        Supporter speaker = new Supporter();
        speaker.setSeminar(seminar);
        speaker.setSupporterType(SupporterType.SPEAKER);
        speaker.setLetterCount(5);
        speaker.setTitle("Dr.");
        speaker.setFirstName("Mike");
        speaker.setLastName("Christakis");
        speaker.setRole("Speaker");
        supporterRepository.save(speaker);

        Supporter panelist = new Supporter();
        panelist.setSeminar(seminar);
        panelist.setSupporterType(SupporterType.PANELIST);
        panelist.setLetterCount(5);
        panelist.setFirstName("Gabrielle");
        panelist.setLastName("Fisher");
        panelist.setRole("Panelist- Personal Leadership Panel");
        supporterRepository.save(panelist);

        Supporter staff = new Supporter();
        staff.setSeminar(seminar);
        staff.setSupporterType(SupporterType.STAFF);
        staff.setLetterCount(2);
        staff.setFirstName("Brad");
        staff.setLastName("Cech");
        staff.setRole("Facilitator");
        staff.setColor("Red");
        staff.setGroupCode("A");
        supporterRepository.save(staff);

        List<Supporter> all = supporterRepository.findBySeminarId(seminar.getId());
        assertThat(all, hasSize(4));

        List<Supporter> donors = supporterRepository.findBySeminarIdAndSupporterType(seminar.getId(), SupporterType.DONOR);
        assertThat(donors, hasSize(1));
        Supporter foundDonor = donors.get(0);
        assertThat(foundDonor.getFullName(), is("Kate Hartley"));
        assertThat(foundDonor.getBeneficiaryFirst(), is("Ronan"));
        assertThat(foundDonor.getBeneficiaryLast(), is("Corr"));
        assertThat(foundDonor.getSponsoredSchool(), is("SCH001"));
        assertThat(foundDonor.getSponsorCounty(), is("Essex"));
        assertThat(foundDonor.getOrganization(), nullValue());
        assertThat(foundDonor.getDonationInfo(), is("15 cash"));
        assertThat(foundDonor.getDonorType(), is("Individual"));
        assertThat(foundDonor.getStreet(), is("276 Main St"));
        assertThat(foundDonor.getCity(), is("North Creek"));
        assertThat(foundDonor.getState(), is("NY"));
        assertThat(foundDonor.getZip(), is("12853"));
        assertThat(foundDonor.getLetterCount(), is(1));
        assertThat(foundDonor.getSeminar().getId(), is(seminar.getId()));

        List<Supporter> staffList = supporterRepository.findBySeminarIdAndSupporterType(seminar.getId(), SupporterType.STAFF);
        assertThat(staffList, hasSize(1));
        assertThat(staffList.get(0).getColor(), is("Red"));
        assertThat(staffList.get(0).getGroupCode(), is("A"));

        supporterRepository.deleteBySeminarId(seminar.getId());
        assertThat(supporterRepository.findBySeminarId(seminar.getId()), empty());
    }

    @Test
    void matchCrudAndQueryBySeminar() {
        Seminar seminar = savedSeminar("Match IT Seminar");

        Ambassador ambassador = new Ambassador();
        ambassador.setSeminar(seminar);
        ambassador.setFirstName("Jane");
        ambassador.setLastName("Doe");
        ambassador = ambassadorRepository.save(ambassador);

        Supporter supporter = new Supporter();
        supporter.setSeminar(seminar);
        supporter.setSupporterType(SupporterType.DONOR);
        supporter.setFullName("Acme Corp");
        supporter = supporterRepository.save(supporter);

        Match match = new Match();
        match.setSeminar(seminar);
        match.setAmbassador(ambassador);
        match.setSupporter(supporter);
        match.setMandatory(true);
        Match saved = matchRepository.save(match);

        assertThat(saved.getId(), notNullValue());
        Match found = matchRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.isMandatory(), is(true));
        assertThat(found.getAmbassador().getId(), is(ambassador.getId()));
        assertThat(found.getSupporter().getId(), is(supporter.getId()));
        assertThat(found.getSeminar().getId(), is(seminar.getId()));

        assertThat(matchRepository.findBySeminarId(seminar.getId()), hasSize(1));

        matchRepository.deleteBySeminarId(seminar.getId());
        assertThat(matchRepository.findBySeminarId(seminar.getId()), empty());
    }

    private Seminar savedSeminar(String name) {
        Seminar seminar = new Seminar();
        seminar.setName(name);
        seminar.setSeminarDate(LocalDate.of(2026, 4, 10));
        seminar.setCreatedBy("admin@hobynye.org");
        return seminarRepository.save(seminar);
    }
}

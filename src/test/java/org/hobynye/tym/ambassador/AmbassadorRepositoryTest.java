package org.hobynye.tym.ambassador;

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
class AmbassadorRepositoryTest {

    @Autowired AmbassadorRepository ambassadorRepository;
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
    void savesAndFindsById() {
        Ambassador ambassador = new Ambassador();
        ambassador.setSeminar(seminar);
        ambassador.setFirstName("Jane");
        ambassador.setLastName("Doe");
        ambassador.setSchoolName("Cornwall Central High School");
        ambassador.setColor("Red");
        ambassador.setGroupCode("A");
        ambassador.setCounty("Orange");

        Ambassador saved = ambassadorRepository.save(ambassador);

        assertThat(saved.getId(), notNullValue());
        Ambassador found = ambassadorRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getFirstName(), is("Jane"));
        assertThat(found.getLastName(), is("Doe"));
        assertThat(found.getCounty(), is("Orange"));
    }

    @Test
    void findsBySeminarId() {
        Ambassador a1 = new Ambassador();
        a1.setSeminar(seminar);
        a1.setFirstName("Jane");
        a1.setLastName("Doe");
        ambassadorRepository.save(a1);

        Ambassador a2 = new Ambassador();
        a2.setSeminar(seminar);
        a2.setFirstName("John");
        a2.setLastName("Smith");
        ambassadorRepository.save(a2);

        List<Ambassador> found = ambassadorRepository.findBySeminarId(seminar.getId());
        assertThat(found, hasSize(2));
    }

    @Test
    void deletesBySeminarId() {
        Ambassador ambassador = new Ambassador();
        ambassador.setSeminar(seminar);
        ambassador.setFirstName("Test");
        ambassador.setLastName("User");
        ambassadorRepository.save(ambassador);

        ambassadorRepository.deleteBySeminarId(seminar.getId());

        assertThat(ambassadorRepository.findBySeminarId(seminar.getId()), empty());
    }
}

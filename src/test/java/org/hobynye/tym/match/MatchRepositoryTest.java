package org.hobynye.tym.match;

import org.hobynye.tym.ambassador.Ambassador;
import org.hobynye.tym.ambassador.AmbassadorRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class MatchRepositoryTest {

    @Autowired MatchRepository matchRepository;
    @Autowired SeminarRepository seminarRepository;
    @Autowired AmbassadorRepository ambassadorRepository;
    @Autowired SupporterRepository supporterRepository;

    Seminar seminar;
    Ambassador ambassador;
    Supporter supporter;

    @BeforeEach
    void setUp() {
        seminar = new Seminar();
        seminar.setName("Spring 2026");
        seminar.setSeminarDate(LocalDate.of(2026, 4, 10));
        seminar.setCreatedBy("admin@hobynye.org");
        seminar = seminarRepository.save(seminar);

        ambassador = new Ambassador();
        ambassador.setSeminar(seminar);
        ambassador.setFirstName("Jane");
        ambassador.setLastName("Doe");
        ambassador = ambassadorRepository.save(ambassador);

        supporter = new Supporter();
        supporter.setSeminar(seminar);
        supporter.setSupporterType(SupporterType.DONOR);
        supporter.setName("Acme Corp");
        supporter = supporterRepository.save(supporter);
    }

    @Test
    void savesAndFindsAllFields() {
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
    }

    @Test
    void findsBySeminarId() {
        Match match = new Match();
        match.setSeminar(seminar);
        match.setAmbassador(ambassador);
        match.setSupporter(supporter);
        match.setMandatory(false);
        matchRepository.save(match);

        List<Match> found = matchRepository.findBySeminarId(seminar.getId());
        assertThat(found, hasSize(1));
    }

    @Test
    void deletesBySeminarId() {
        Match match = new Match();
        match.setSeminar(seminar);
        match.setAmbassador(ambassador);
        match.setSupporter(supporter);
        match.setMandatory(false);
        matchRepository.save(match);

        matchRepository.deleteBySeminarId(seminar.getId());

        assertThat(matchRepository.findBySeminarId(seminar.getId()), empty());
    }
}

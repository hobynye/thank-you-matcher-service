package org.hobynye.tym.seminar;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class SeminarRepositoryTest {

    @Autowired
    SeminarRepository repository;

    @Test
    void savesAndFinds() {
        Seminar seminar = new Seminar();
        seminar.setName("Spring 2026");
        seminar.setSeminarDate(LocalDate.of(2026, 4, 10));
        seminar.setCreatedBy("admin@hobynye.org");

        Seminar saved = repository.save(seminar);

        assertThat(saved.getId(), notNullValue());
        assertThat(saved.getCreatedAt(), notNullValue());

        Optional<Seminar> found = repository.findById(saved.getId());
        assertThat(found.isPresent(), is(true));
        assertThat(found.get().getName(), is("Spring 2026"));
        assertThat(found.get().getSeminarDate(), is(LocalDate.of(2026, 4, 10)));
        assertThat(found.get().getCreatedBy(), is("admin@hobynye.org"));
    }

    @Test
    void deletesById() {
        Seminar seminar = new Seminar();
        seminar.setName("Fall 2026");
        seminar.setSeminarDate(LocalDate.of(2026, 10, 1));
        seminar.setCreatedBy("admin@hobynye.org");

        Seminar saved = repository.save(seminar);
        repository.deleteById(saved.getId());

        assertThat(repository.findById(saved.getId()).isPresent(), is(false));
    }
}

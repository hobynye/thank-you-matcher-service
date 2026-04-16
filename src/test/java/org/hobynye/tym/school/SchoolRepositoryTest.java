package org.hobynye.tym.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class SchoolRepositoryTest {

    @Autowired
    SchoolRepository repository;

    @Test
    void savesAndFindsById() {
        School school = new School();
        school.setName("Cornwall Central High School");
        school.setCounty("Orange");
        school.setState("NY");

        School saved = repository.save(school);

        assertThat(saved.getId(), notNullValue());
        assertThat(repository.findById(saved.getId()).orElse(null), notNullValue());
    }

    @Test
    void findsByNameIgnoreCase() {
        School school = new School();
        school.setName("Warrensburg Central School");
        school.setCounty("Warren");
        school.setState("NY");
        repository.save(school);

        assertThat(repository.findByNameIgnoreCase("warrensburg central school").isPresent(), is(true));
        assertThat(repository.findByNameIgnoreCase("WARRENSBURG CENTRAL SCHOOL").isPresent(), is(true));
        assertThat(repository.findByNameIgnoreCase("Unknown School").isPresent(), is(false));
    }
}

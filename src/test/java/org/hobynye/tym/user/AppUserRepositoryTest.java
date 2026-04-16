package org.hobynye.tym.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Transactional
class AppUserRepositoryTest {

    @Autowired
    AppUserRepository repository;

    @Test
    void savesAndFindsAllFields() {
        AppUser user = new AppUser();
        user.setEmail("admin@hobynye.org");
        user.setAdmin(true);

        AppUser saved = repository.save(user);

        assertThat(saved.getId(), notNullValue());
        AppUser found = repository.findById(saved.getId()).orElseThrow();
        assertThat(found.getEmail(), is("admin@hobynye.org"));
        assertThat(found.isAdmin(), is(true));
    }

    @Test
    void findsByEmailIgnoreCase() {
        AppUser user = new AppUser();
        user.setEmail("User@hobynye.org");
        user.setAdmin(false);
        repository.save(user);

        assertThat(repository.findByEmailIgnoreCase("user@hobynye.org").isPresent(), is(true));
        assertThat(repository.findByEmailIgnoreCase("USER@HOBYNYE.ORG").isPresent(), is(true));
        assertThat(repository.findByEmailIgnoreCase("other@hobynye.org").isPresent(), is(false));
    }

    @Test
    void distinguishesAdminFlag() {
        AppUser admin = new AppUser();
        admin.setEmail("admin2@hobynye.org");
        admin.setAdmin(true);

        AppUser regular = new AppUser();
        regular.setEmail("member@hobynye.org");
        regular.setAdmin(false);

        repository.save(admin);
        repository.save(regular);

        assertThat(repository.findByEmailIgnoreCase("admin2@hobynye.org").get().isAdmin(), is(true));
        assertThat(repository.findByEmailIgnoreCase("member@hobynye.org").get().isAdmin(), is(false));
    }
}

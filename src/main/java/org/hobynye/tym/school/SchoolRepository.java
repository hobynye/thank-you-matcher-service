package org.hobynye.tym.school;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SchoolRepository extends JpaRepository<School, UUID> {
    Optional<School> findByNameIgnoreCase(String name);
}

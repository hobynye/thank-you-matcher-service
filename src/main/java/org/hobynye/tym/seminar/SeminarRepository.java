package org.hobynye.tym.seminar;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SeminarRepository extends JpaRepository<Seminar, UUID> {
}

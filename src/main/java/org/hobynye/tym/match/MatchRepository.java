package org.hobynye.tym.match;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findBySeminarId(UUID seminarId);
    void deleteBySeminarId(UUID seminarId);
}

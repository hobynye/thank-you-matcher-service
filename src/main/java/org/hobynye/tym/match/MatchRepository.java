package org.hobynye.tym.match;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findBySeminarId(UUID seminarId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Match m WHERE m.seminar.id = :seminarId")
    void deleteBySeminarId(@Param("seminarId") UUID seminarId);
}

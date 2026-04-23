package org.hobynye.tym.ambassador;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface AmbassadorRepository extends JpaRepository<Ambassador, UUID> {
    List<Ambassador> findBySeminarId(UUID seminarId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Ambassador a WHERE a.seminar.id = :seminarId")
    void deleteBySeminarId(@Param("seminarId") UUID seminarId);
}

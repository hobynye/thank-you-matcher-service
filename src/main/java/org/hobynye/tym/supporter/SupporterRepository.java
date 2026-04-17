package org.hobynye.tym.supporter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SupporterRepository extends JpaRepository<Supporter, UUID> {
    List<Supporter> findBySeminarId(UUID seminarId);
    List<Supporter> findBySeminarIdAndSupporterType(UUID seminarId, SupporterType supporterType);

    @Modifying
    @Query("DELETE FROM Supporter s WHERE s.seminar.id = :seminarId")
    void deleteBySeminarId(@Param("seminarId") UUID seminarId);
}

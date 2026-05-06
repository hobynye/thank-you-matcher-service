package org.hobynye.tym.supporter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface SupporterRepository extends JpaRepository<Supporter, UUID> {
    List<Supporter> findBySeminarId(UUID seminarId);
    List<Supporter> findBySeminarIdAndSupporterType(UUID seminarId, SupporterType supporterType);

    @Transactional
    @Modifying
    @Query("DELETE FROM Supporter s WHERE s.seminar.id = :seminarId")
    void deleteBySeminarId(@Param("seminarId") UUID seminarId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Supporter s WHERE s.seminar.id = :seminarId AND s.supporterType = :type")
    void deleteBySeminarIdAndSupporterType(@Param("seminarId") UUID seminarId, @Param("type") SupporterType type);

    @Transactional
    @Modifying
    @Query("DELETE FROM Supporter s WHERE s.seminar.id = :seminarId AND s.supporterType IN :types")
    void deleteBySeminarIdAndSupporterTypeIn(@Param("seminarId") UUID seminarId, @Param("types") Collection<SupporterType> types);
}

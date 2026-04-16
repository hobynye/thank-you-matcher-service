package org.hobynye.tym.supporter;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SupporterRepository extends JpaRepository<Supporter, UUID> {
    List<Supporter> findBySeminarId(UUID seminarId);
    List<Supporter> findBySeminarIdAndSupporterType(UUID seminarId, SupporterType supporterType);
    void deleteBySeminarId(UUID seminarId);
}

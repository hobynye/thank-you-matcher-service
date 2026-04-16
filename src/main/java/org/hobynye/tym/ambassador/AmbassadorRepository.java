package org.hobynye.tym.ambassador;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AmbassadorRepository extends JpaRepository<Ambassador, UUID> {
    List<Ambassador> findBySeminarId(UUID seminarId);
    void deleteBySeminarId(UUID seminarId);
}

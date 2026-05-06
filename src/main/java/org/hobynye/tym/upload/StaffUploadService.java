package org.hobynye.tym.upload;

import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.seminar.SeminarRepository;
import org.hobynye.tym.supporter.Supporter;
import org.hobynye.tym.supporter.SupporterRepository;
import org.hobynye.tym.supporter.SupporterType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class StaffUploadService {

    private static final Set<SupporterType> STAFF_TYPES =
            Set.of(SupporterType.STAFF, SupporterType.SPEAKER, SupporterType.PANELIST);

    private final SeminarRepository seminarRepository;
    private final SupporterRepository supporterRepository;

    public StaffUploadService(SeminarRepository seminarRepository, SupporterRepository supporterRepository) {
        this.seminarRepository = seminarRepository;
        this.supporterRepository = supporterRepository;
    }

    @Transactional
    public int upload(UUID seminarId, InputStream in) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seminar not found: " + seminarId));

        try (XlsxSheet sheet = XlsxSheet.from(in)) {
            supporterRepository.deleteBySeminarIdAndSupporterTypeIn(seminarId, STAFF_TYPES);

            List<Supporter> supporters = new ArrayList<>();
            for (XlsxSheet.RowView row : sheet.rows()) {
                String firstName = row.getString("first name");
                String lastName = row.getString("last name");
                if (firstName == null && lastName == null) continue;

                String role = row.getString("role");

                Supporter s = new Supporter();
                s.setSeminar(seminar);
                s.setSupporterType(typeFromRole(role));
                s.setName(joinName(firstName, lastName));
                s.setRole(role);
                s.setColor(row.getString("color"));
                s.setGroupCode(row.getString("group"));
                s.setStreet(row.getString("street"));
                s.setCity(row.getString("city"));
                s.setState(row.getString("state"));
                s.setZip(row.getString("zip"));
                s.setLetterCount(row.getIntOrDefault("letter count", 1));
                // DonorInfo.xlsx has no Title column; Supporter.title is intentionally left null for staff-sheet uploads

                supporters.add(s);
            }
            supporterRepository.saveAll(supporters);
            return supporters.size();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse file: " + e.getMessage());
        }
    }

    private static SupporterType typeFromRole(String role) {
        if (role == null) return SupporterType.STAFF;
        String lower = role.toLowerCase();
        if (lower.contains("speaker")) return SupporterType.SPEAKER;
        if (lower.contains("panelist")) return SupporterType.PANELIST;
        return SupporterType.STAFF;
    }

    private static String joinName(String firstName, String lastName) {
        if (firstName == null) return lastName;
        if (lastName == null) return firstName;
        return firstName + " " + lastName;
    }
}

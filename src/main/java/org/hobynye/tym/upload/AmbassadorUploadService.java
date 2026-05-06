package org.hobynye.tym.upload;

import org.hobynye.tym.ambassador.Ambassador;
import org.hobynye.tym.ambassador.AmbassadorRepository;
import org.hobynye.tym.school.School;
import org.hobynye.tym.school.SchoolRepository;
import org.hobynye.tym.seminar.Seminar;
import org.hobynye.tym.seminar.SeminarRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AmbassadorUploadService {

    private final SeminarRepository seminarRepository;
    private final AmbassadorRepository ambassadorRepository;
    private final SchoolRepository schoolRepository;

    public AmbassadorUploadService(SeminarRepository seminarRepository,
                                   AmbassadorRepository ambassadorRepository,
                                   SchoolRepository schoolRepository) {
        this.seminarRepository = seminarRepository;
        this.ambassadorRepository = ambassadorRepository;
        this.schoolRepository = schoolRepository;
    }

    @Transactional
    public int upload(UUID seminarId, InputStream in) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seminar not found: " + seminarId));

        try (XlsxSheet sheet = XlsxSheet.from(in)) {
            ambassadorRepository.deleteBySeminarId(seminarId);

            List<Ambassador> ambassadors = new ArrayList<>();
            for (XlsxSheet.RowView row : sheet.rows()) {
                String firstName = row.getString("first name");
                String lastName = row.getString("last name");
                if (firstName == null && lastName == null) continue;

                String schoolName = row.getString("school name");
                String county = resolveCounty(schoolName);

                Ambassador a = new Ambassador();
                a.setSeminar(seminar);
                a.setFirstName(firstName);
                a.setLastName(lastName);
                a.setSchoolName(schoolName);
                a.setColor(row.getString("color"));
                a.setGroupCode(row.getString("group"));
                a.setCounty(county);

                ambassadors.add(a);
            }
            ambassadorRepository.saveAll(ambassadors);
            return ambassadors.size();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse file: " + e.getMessage());
        }
    }

    private String resolveCounty(String schoolName) {
        if (schoolName == null) return null;
        return schoolRepository.findByNameIgnoreCase(schoolName)
                .map(School::getCounty)
                .orElse(null);
    }
}
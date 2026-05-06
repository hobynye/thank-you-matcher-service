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
import java.util.UUID;

@Service
public class SupporterUploadService {

    private final SeminarRepository seminarRepository;
    private final SupporterRepository supporterRepository;

    public SupporterUploadService(SeminarRepository seminarRepository, SupporterRepository supporterRepository) {
        this.seminarRepository = seminarRepository;
        this.supporterRepository = supporterRepository;
    }

    @Transactional
    public int upload(UUID seminarId, InputStream in) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Seminar not found: " + seminarId));

        try (XlsxSheet sheet = XlsxSheet.from(in)) {
            supporterRepository.deleteBySeminarIdAndSupporterType(seminarId, SupporterType.DONOR);

            List<Supporter> supporters = new ArrayList<>();
            for (XlsxSheet.RowView row : sheet.rows()) {
                String name = row.getString("organization");
                if (name == null) continue;

                Supporter s = new Supporter();
                s.setSeminar(seminar);
                s.setSupporterType(SupporterType.DONOR);
                s.setName(name);
                s.setContactName(row.getString("contact name"));
                s.setCategory(row.getString("donor type"));
                s.setClub(row.getString("club"));
                s.setStreet(row.getString("address 1"));
                s.setStreet2(row.getString("address 2"));
                s.setCity(row.getString("city"));
                s.setState(row.getString("state"));
                s.setZip(row.getString("zip"));
                s.setEmail(row.getString("email"));
                s.setPhone(row.getString("phone"));
                s.setLetterCount(row.getIntOrDefault("letter count", 1));
                s.setSponsoredJStaff(row.getString("sponsored jstaff"));
                s.setSponsoredAmbassador(row.getString("sponsored ambassador"));

                // School and county mandatory matching only applies when explicitly earmarked
                if (row.isYes("earmarked donation?")) {
                    s.setSponsoredSchool(row.getString("sponsored school"));
                    s.setSponsoredCounty(row.getString("sponsored county"));
                }

                supporters.add(s);
            }
            supporterRepository.saveAll(supporters);
            return supporters.size();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to parse file: " + e.getMessage());
        }
    }
}
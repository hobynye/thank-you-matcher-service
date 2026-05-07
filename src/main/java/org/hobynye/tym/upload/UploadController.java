package org.hobynye.tym.upload;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/seminars/{seminarId}/upload")
class UploadController {

    private final SupporterUploadService supporterUploadService;
    private final StaffUploadService staffUploadService;
    private final AmbassadorUploadService ambassadorUploadService;

    UploadController(SupporterUploadService supporterUploadService,
                     StaffUploadService staffUploadService,
                     AmbassadorUploadService ambassadorUploadService) {
        this.supporterUploadService = supporterUploadService;
        this.staffUploadService = staffUploadService;
        this.ambassadorUploadService = ambassadorUploadService;
    }

    @PostMapping("/supporters")
    UploadResult uploadSupporters(@PathVariable UUID seminarId, @RequestParam MultipartFile file) {
        return new UploadResult(supporterUploadService.upload(seminarId, stream(file)));
    }

    @PostMapping("/staff")
    UploadResult uploadStaff(@PathVariable UUID seminarId, @RequestParam MultipartFile file) {
        return new UploadResult(staffUploadService.upload(seminarId, stream(file)));
    }

    @PostMapping("/ambassadors")
    UploadResult uploadAmbassadors(@PathVariable UUID seminarId, @RequestParam MultipartFile file) {
        return new UploadResult(ambassadorUploadService.upload(seminarId, stream(file)));
    }

    private static java.io.InputStream stream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not read uploaded file");
        }
    }
}

package org.hobynye.tym.upload;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class UploadControllerTest {

    @Autowired WebApplicationContext context;

    @MockitoBean SupporterUploadService supporterUploadService;
    @MockitoBean StaffUploadService staffUploadService;
    @MockitoBean AmbassadorUploadService ambassadorUploadService;

    MockMvc mockMvc;

    private static final UUID SEMINAR_ID = UUID.randomUUID();
    private static final MockMultipartFile FILE =
            new MockMultipartFile("file", "test.xlsx", "application/octet-stream", new byte[]{1, 2, 3});

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    @WithMockUser
    void uploadSupportersReturnsCount() throws Exception {
        when(supporterUploadService.upload(any(), any())).thenReturn(42);

        mockMvc.perform(multipart("/api/seminars/{id}/upload/supporters", SEMINAR_ID).file(FILE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(42));
    }

    @Test
    @WithMockUser
    void uploadStaffReturnsCount() throws Exception {
        when(staffUploadService.upload(any(), any())).thenReturn(10);

        mockMvc.perform(multipart("/api/seminars/{id}/upload/staff", SEMINAR_ID).file(FILE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(10));
    }

    @Test
    @WithMockUser
    void uploadAmbassadorsReturnsCount() throws Exception {
        when(ambassadorUploadService.upload(any(), any())).thenReturn(25);

        mockMvc.perform(multipart("/api/seminars/{id}/upload/ambassadors", SEMINAR_ID).file(FILE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(25));
    }

    @Test
    @WithMockUser
    void returns404WhenSeminarNotFound() throws Exception {
        when(supporterUploadService.upload(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Seminar not found"));

        mockMvc.perform(multipart("/api/seminars/{id}/upload/supporters", SEMINAR_ID).file(FILE))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns401WhenUnauthenticated() throws Exception {
        mockMvc.perform(multipart("/api/seminars/{id}/upload/supporters", SEMINAR_ID).file(FILE))
                .andExpect(status().isUnauthorized());
    }
}
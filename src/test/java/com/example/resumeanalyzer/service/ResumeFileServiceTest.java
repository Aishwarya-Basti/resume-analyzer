package com.example.resumeanalyzer.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResumeFileServiceTest {

    private final ResumeFileService service = new ResumeFileService();

    @Test
    void extractTextShouldReadTextFiles() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "resume",
                "sample.txt",
                "text/plain",
                "Java backend developer with Spring Boot and REST APIs".getBytes(StandardCharsets.UTF_8)
        );

        String text = service.extractText(file);

        assertEquals("Java backend developer with Spring Boot and REST APIs", text);
    }
}

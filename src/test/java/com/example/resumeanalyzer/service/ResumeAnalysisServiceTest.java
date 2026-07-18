package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.ResumeAnalysisRequest;
import com.example.resumeanalyzer.dto.ResumeAnalysisResult;
import com.example.resumeanalyzer.repository.ResumeAnalysisRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ResumeAnalysisServiceTest {

    private final ResumeAnalysisRepository repository = mock(ResumeAnalysisRepository.class);
    private final ResumeAnalysisService service = new ResumeAnalysisService(repository);

    @Test
    void analyzeResumeShouldProduceStrongAtsAndMatchScores() {
        ResumeAnalysisRequest request = new ResumeAnalysisRequest();
        request.setJobTitle("Java Backend Developer");
        request.setResumeText("Experienced Java backend developer with 4 years building Spring Boot applications, REST APIs, microservices, SQL, Hibernate, Maven, and Docker. Led migration projects and improved system reliability.");
        request.setExperienceYears(4);

        ResumeAnalysisResult result = service.analyzeResume(request);

        assertTrue(result.getAtsScore() >= 70);
        assertTrue(result.getMatchScore() >= 70);
        assertTrue(result.getStrengths().contains("Java"));
        assertTrue(!result.getSuggestedJobs().isEmpty());
    }

    @Test
    void analyzeResumeShouldChangeScoresBasedOnTargetRole() {
        ResumeAnalysisRequest backendRequest = new ResumeAnalysisRequest();
        backendRequest.setJobTitle("Java Backend Developer");
        backendRequest.setResumeText("Java backend developer with Spring Boot, REST APIs, SQL, Docker, and microservices");
        backendRequest.setExperienceYears(4);

        ResumeAnalysisRequest frontendRequest = new ResumeAnalysisRequest();
        frontendRequest.setJobTitle("Frontend Developer");
        frontendRequest.setResumeText("Java backend developer with Spring Boot, REST APIs, SQL, Docker, and microservices");
        frontendRequest.setExperienceYears(4);

        ResumeAnalysisResult backendResult = service.analyzeResume(backendRequest);
        ResumeAnalysisResult frontendResult = service.analyzeResume(frontendRequest);

        assertNotEquals(backendResult.getMatchScore(), frontendResult.getMatchScore());
        assertNotEquals(backendResult.getAtsScore(), frontendResult.getAtsScore());
    }
}
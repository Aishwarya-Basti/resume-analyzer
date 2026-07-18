package com.example.resumeanalyzer.service;

import com.example.resumeanalyzer.dto.ResumeAnalysisRequest;
import com.example.resumeanalyzer.dto.ResumeAnalysisResult;
import com.example.resumeanalyzer.entity.ResumeAnalysis;
import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.repository.ResumeAnalysisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.Locale;

@Service
public class ResumeAnalysisService {

    
    private final ResumeAnalysisRepository resumeAnalysisRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final List<String> JAVA_BACKEND_SKILLS = Arrays.asList(
    "java",
    "python",
    "c",
    "html",
    "css",
    "javascript",
    "spring",
    "spring boot",
    "hibernate",
    "jpa",
    "flask",
    "rest",
    "api",
    "mysql",
    "sql",
    "sqlite",
    "docker",
    "microservices",
    "maven",
    "git",
    "github",
    "oop",
    "object-oriented programming",
    "dbms",
    "data structures",
    "algorithms"
);
private static final List<String> REQUIRED_JAVA_SKILLS = Arrays.asList(
        "java",
        "spring boot",
        "sql",
        "mysql",
        "rest",
        "git",
        "maven"
);
    public ResumeAnalysisService(ResumeAnalysisRepository resumeAnalysisRepository) {
    this.resumeAnalysisRepository = resumeAnalysisRepository;
}

   

    /**
     * Extract text from PDF file
     */
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Extract text from DOCX file
     */
    public String extractTextFromDocx(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             XWPFDocument document = new XWPFDocument(inputStream)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    /**
     * Extract text from file based on mime type
     */
    public String extractTextFromFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return "";
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) return "";

        String lowerName = fileName.toLowerCase(Locale.ROOT);
        byte[] bytes = file.getBytes();

        if (lowerName.endsWith(".txt") || lowerName.endsWith(".md")) {
            return new String(bytes, StandardCharsets.UTF_8);
        } else if (lowerName.endsWith(".pdf")) {
            return extractTextFromPdf(file);
        } else if (lowerName.endsWith(".docx")) {
            return extractTextFromDocx(file);
        } else if (lowerName.endsWith(".doc")) {
            // For .doc files, attempt DOCX parsing (older format may not work perfectly)
            return extractTextFromDocx(file);
        } else {
            // Plain text fallback
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * Save analysis result to database
     */
    public ResumeAnalysis saveAnalysis(User user, ResumeAnalysisRequest request, ResumeAnalysisResult result) throws IOException {
        ResumeAnalysis analysis = new ResumeAnalysis();
        analysis.setUser(user);
        analysis.setJobTitle(request.getJobTitle());
        analysis.setResumeText(request.getResumeText());
        analysis.setExperienceYears(request.getExperienceYears());
        analysis.setAtsScore(result.getAtsScore());
        analysis.setMatchScore(result.getMatchScore());
        analysis.setStrengths(objectMapper.writeValueAsString(result.getStrengths()));
        analysis.setGaps(objectMapper.writeValueAsString(result.getGaps()));
        analysis.setSuggestedJobs(objectMapper.writeValueAsString(result.getSuggestedJobs()));
        analysis.setUpdatedAt(LocalDateTime.now());

        return resumeAnalysisRepository.save(analysis);
    }

    /**
     * Get all analyses for a user
     */
    public List<ResumeAnalysis> getUserAnalyses(User user) {
        return resumeAnalysisRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get a specific analysis for a user
     */
    public Optional<ResumeAnalysis> getUserAnalysis(Long analysisId, User user) {
        return resumeAnalysisRepository.findByIdAndUser(analysisId, user);
    }

    public ResumeAnalysisResult analyzeResume(ResumeAnalysisRequest request) {
        String text = request.getResumeText() == null ? "" : request.getResumeText().toLowerCase(Locale.ROOT);
        String jobDescription = request.getJobDescription() == null
        ? ""
        : request.getJobDescription().toLowerCase();
        String title = request.getJobTitle() == null ? "" : request.getJobTitle().toLowerCase(Locale.ROOT);

        List<String> strengths = new ArrayList<>();
        List<String> gaps = new ArrayList<>();
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

       for (String skill : JAVA_BACKEND_SKILLS) {
    if (text.contains(skill)) {
       switch (skill) {
    case "api":
        strengths.add("API");
        break;
    case "sql":
        strengths.add("SQL");
        break;
    case "mysql":
        strengths.add("MySQL");
        break;
    case "spring boot":
        strengths.add("Spring Boot");
        break;
    case "github":
        strengths.add("GitHub");
        break;
    case "oop":
        strengths.add("OOP");
        break;
    default:
        strengths.add(skill.substring(0, 1).toUpperCase() + skill.substring(1));
}
    }
}

if (request.getExperienceYears() >= 3) {
    strengths.add("Experience Depth");
}
for (String skill : REQUIRED_JAVA_SKILLS) {

    boolean required =
            jobDescription.isEmpty() || jobDescription.contains(skill);

    if (!required) {
        continue;
    }

    if (text.contains(skill)) {
        matchedSkills.add(skill);
    } else {
        missingSkills.add(skill);
    }
}
        if (title.contains("backend") || title.contains("java")) {
            if (!text.contains("backend")) gaps.add("Backend keywords");
            if (!text.contains("java")) gaps.add("Java keywords");
        }
        if (title.contains("frontend") || title.contains("ui") || title.contains("react")) {
            if (!text.contains("react") && !text.contains("frontend") && !text.contains("ui")) gaps.add("Frontend keywords");
            if (!text.contains("javascript") && !text.contains("typescript")) gaps.add("JavaScript/TypeScript keywords");
        }
        if (title.contains("data") || title.contains("analyst")) {
            if (!text.contains("sql") && !text.contains("database") && !text.contains("analytics")) gaps.add("Data analysis keywords");
        }
        if (!text.contains("aws") && !text.contains("cloud")) gaps.add("Cloud exposure");
        if (!text.contains("testing") && !text.contains("unit")) gaps.add("Testing practices");

        int atsScore = 50;
        int matchScore = 50;

        // Add 5 points for every required skill found
        atsScore += matchedSkills.size() * 5;

        // Add experience points
        atsScore += request.getExperienceYears() * 2;

        // Match score depends on matched skills and experience
        matchScore += matchedSkills.size() * 5;
        matchScore += request.getExperienceYears() * 3;

        if (title.contains("backend") || title.contains("java")) {
            if (text.contains("java")) {
                atsScore += 8;
                matchScore += 10;
            }
            if (text.contains("spring") || text.contains("spring boot")) {
                atsScore += 4;
                matchScore += 8;
            }
            if (text.contains("api") || text.contains("rest")) {
                atsScore += 4;
                matchScore += 6;
            }
            if (!text.contains("backend")) {
                atsScore -= 6;
                matchScore -= 4;
            }
        } else if (title.contains("frontend") || title.contains("ui") || title.contains("react")) {
            if (text.contains("react") || text.contains("frontend") || text.contains("ui")) {
                atsScore += 10;
                matchScore += 12;
            }
            if (text.contains("javascript") || text.contains("typescript")) {
                atsScore += 6;
                matchScore += 8;
            }
            if (!text.contains("react") && !text.contains("frontend") && !text.contains("ui")) {
                atsScore -= 8;
                matchScore -= 6;
            }
        } else if (title.contains("data") || title.contains("analyst")) {
            if (text.contains("sql") || text.contains("data") || text.contains("analytics")) {
                atsScore += 10;
                matchScore += 12;
            }
        }

        if (text.contains("aws") || text.contains("cloud")) {
            atsScore += 2;
            matchScore += 4;
        }
        if (text.contains("testing") || text.contains("unit")) {
            atsScore += 2;
            matchScore += 3;
        }

        atsScore = Math.min(95, Math.max(40, atsScore));
        matchScore = Math.min(95, Math.max(40, matchScore));

        List<String> suggestedJobs = new ArrayList<>();
        if (title.contains("java") || title.contains("backend")) {
            suggestedJobs.addAll(Arrays.asList(
                    "Java Backend Developer",
                    "Senior Backend Engineer",
                    "Spring Boot Engineer"
            ));
        } else {
            suggestedJobs.addAll(Arrays.asList(
                    "Software Engineer",
                    "Product Engineer",
                    "Full Stack Developer"
            ));
        }

        String roleLabel = title.isBlank() ? "the selected role" : title;
        String verdict = matchScore >= 80 ? "strongly matches" : matchScore >= 65 ? "partially matches" : "needs improvement for";
        String strengthList = String.join(", ", strengths.isEmpty() ? List.of("core engineering") : strengths.subList(0, Math.min(3, strengths.size())));
        gaps.addAll(missingSkills);
        String gapList = gaps.isEmpty() ? "a few polish points" : String.join(", ", gaps.subList(0, Math.min(3, gaps.size())));

        String summary = String.format(
                "Your resume %s %s. It shows strong evidence for %s, but you should improve %s to better align with the role.",
                verdict,
                roleLabel,
                strengthList,
                gapList
        );

       return new ResumeAnalysisResult(
        atsScore,
        matchScore,
        strengths,
        gaps,
        matchedSkills,
        missingSkills,
        suggestedJobs,
        summary
);
    }
}

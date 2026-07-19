package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.dto.ResumeAnalysisRequest;
import com.example.resumeanalyzer.dto.ResumeAnalysisResult;
import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.repository.UserRepository;
import com.example.resumeanalyzer.service.PdfService;
import com.example.resumeanalyzer.service.ResumeAnalysisService;
import com.example.resumeanalyzer.service.ResumeFileService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;

@Controller
public class AnalysisController {

    private final PdfService pdfService;
    private final ResumeAnalysisService resumeAnalysisService;
    private final ResumeFileService resumeFileService;
    private final UserRepository userRepository;

    public AnalysisController(
            ResumeAnalysisService resumeAnalysisService,
            ResumeFileService resumeFileService,
            UserRepository userRepository,
            PdfService pdfService) {

        this.resumeAnalysisService = resumeAnalysisService;
        this.resumeFileService = resumeFileService;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
    }

    private String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication != null ? authentication.getName() : null;
    }

    private User getCurrentUser() {

        String email = getCurrentUserEmail();

        if (email == null)
            return null;

        Optional<User> user = userRepository.findByEmail(email);

        return user.orElse(null);
    }

    @GetMapping("/analyze")
    public String showAnalysisForm(Model model) {

        model.addAttribute("request", new ResumeAnalysisRequest());

        User currentUser = getCurrentUser();

        if (currentUser != null) {

            model.addAttribute(
                    "previousAnalyses",
                    resumeAnalysisService.getUserAnalyses(currentUser));
        }

        return "analyze";
    }

    @PostMapping("/analyze")
    public String analyzeResume(
            @ModelAttribute("request") ResumeAnalysisRequest request,
            Model model,
            HttpSession session) {

        User currentUser = getCurrentUser();

        if (request.getResumeFile() != null &&
                !request.getResumeFile().isEmpty()) {

            try {

                String extracted =
                        resumeAnalysisService.extractTextFromFile(
                                request.getResumeFile());

                request.setResumeText(extracted);

            } catch (Exception ex) {

                model.addAttribute(
                        "uploadError",
                        "Unable to read uploaded file.");

                model.addAttribute("request", request);

                return "analyze";
            }
        }

        ResumeAnalysisResult result =
                resumeAnalysisService.analyzeResume(request);

        // Store latest analysis in session
        session.setAttribute("latestResult", result);

        if (currentUser != null) {

            try {

                resumeAnalysisService.saveAnalysis(
                        currentUser,
                        request,
                        result);

            } catch (Exception ignored) {
            }

            model.addAttribute(
                    "previousAnalyses",
                    resumeAnalysisService.getUserAnalyses(currentUser));
        }

        model.addAttribute("result", result);
        model.addAttribute("request", request);

        return "analyze";
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadPdf(HttpSession session) {

        ResumeAnalysisResult result =
                (ResumeAnalysisResult) session.getAttribute("latestResult");

        if (result == null) {

            byte[] pdf = pdfService.generateResumeReport(
                    "N/A",
                    "N/A",
                    "Please analyze a resume first.",
                    "",
                    "",
                    "");

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=Resume_Report.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        }

        byte[] pdf = pdfService.generateResumeReport(

                result.getAtsScore() + "%",

                result.getMatchScore() + "%",

                result.getSummary(),

                String.join(", ", result.getStrengths()),

                String.join(", ", result.getGaps()),

                String.join(", ", result.getSuggestedJobs())

        );

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Resume_Report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
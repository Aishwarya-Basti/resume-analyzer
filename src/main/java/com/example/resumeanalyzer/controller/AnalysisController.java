package com.example.resumeanalyzer.controller;

import com.example.resumeanalyzer.dto.ResumeAnalysisRequest;
import com.example.resumeanalyzer.dto.ResumeAnalysisResult;
import com.example.resumeanalyzer.entity.User;
import com.example.resumeanalyzer.repository.UserRepository;
import com.example.resumeanalyzer.service.ResumeAnalysisService;
import com.example.resumeanalyzer.service.ResumeFileService;
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

    private final ResumeAnalysisService resumeAnalysisService;
    private final ResumeFileService resumeFileService;
    private final UserRepository userRepository;

    public AnalysisController(ResumeAnalysisService resumeAnalysisService,
                              ResumeFileService resumeFileService,
                              UserRepository userRepository) {
        this.resumeAnalysisService = resumeAnalysisService;
        this.resumeFileService = resumeFileService;
        this.userRepository = userRepository;
    }

    /**
     * Get the current authenticated user email
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    /**
     * Get the current authenticated User entity
     */
    private User getCurrentUser() {
        String email = getCurrentUserEmail();
        if (email == null) {
            return null;
        }
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    @GetMapping("/analyze")
    public String showAnalysisForm(Model model) {

        model.addAttribute("request", new ResumeAnalysisRequest());

        User currentUser = getCurrentUser();
        if (currentUser != null) {
            model.addAttribute("previousAnalyses",
                    resumeAnalysisService.getUserAnalyses(currentUser));
        }

        return "analyze";
    }

    @PostMapping("/analyze")
    public String analyzeResume(@ModelAttribute("request") ResumeAnalysisRequest request,
                                Model model) {

        User currentUser = getCurrentUser();

        if (request.getResumeFile() != null && !request.getResumeFile().isEmpty()) {
            try {
                String extractedText = resumeAnalysisService.extractTextFromFile(request.getResumeFile());
                request.setResumeText(extractedText);
            } catch (Exception ex) {
                model.addAttribute("uploadError",
                        "Unable to read the uploaded resume file: " + ex.getMessage());

                if (currentUser != null) {
                    model.addAttribute("previousAnalyses",
                            resumeAnalysisService.getUserAnalyses(currentUser));
                }

                model.addAttribute("request", request);
                return "analyze";
            }
        }

        ResumeAnalysisResult result = resumeAnalysisService.analyzeResume(request);

        if (currentUser != null) {
            try {
                resumeAnalysisService.saveAnalysis(currentUser, request, result);
            } catch (Exception ex) {
                model.addAttribute("saveError",
                        "Analysis completed but could not be saved.");
            }

            model.addAttribute("previousAnalyses",
                    resumeAnalysisService.getUserAnalyses(currentUser));
        }

        model.addAttribute("result", result);
        model.addAttribute("request", request);

        return "analyze";
    }
}
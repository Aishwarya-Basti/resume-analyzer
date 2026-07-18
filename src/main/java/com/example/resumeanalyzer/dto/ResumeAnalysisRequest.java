package com.example.resumeanalyzer.dto;

import org.springframework.web.multipart.MultipartFile;

public class ResumeAnalysisRequest {
    private String jobTitle;
    private String jobDescription;
    private String resumeText;
    private int experienceYears;
    private MultipartFile resumeFile;
    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    public String getJobDescription() {
    return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
    this.jobDescription = jobDescription;
    }

    public String getResumeText() {
        return resumeText;
    }

    public void setResumeText(String resumeText) {
        this.resumeText = resumeText;
    }

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public MultipartFile getResumeFile() {
        return resumeFile;
    }

    public void setResumeFile(MultipartFile resumeFile) {
        this.resumeFile = resumeFile;
    }
}

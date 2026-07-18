package com.example.resumeanalyzer.dto;

import org.springframework.web.multipart.MultipartFile;

public class ResumeUploadRequest {
    private String jobTitle;
    private int experienceYears;
    private MultipartFile resumeFile;

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
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

package com.example.resumeanalyzer.dto;

import java.util.List;

public class ResumeAnalysisResult {
    private int atsScore;
    private int matchScore;
    private List<String> strengths;
    private List<String> gaps;
    private List<String> matchedSkills;
    private List<String> missingSkills;
    private List<String> suggestedJobs;
    private String summary;

    public ResumeAnalysisResult() {
    }

    public ResumeAnalysisResult(
        int atsScore,
        int matchScore,
        List<String> strengths,
        List<String> gaps,
        List<String> matchedSkills,
        List<String> missingSkills,
        List<String> suggestedJobs,
        String summary) {

    this.atsScore = atsScore;
    this.matchScore = matchScore;
    this.strengths = strengths;
    this.gaps = gaps;
    this.matchedSkills = matchedSkills;
    this.missingSkills = missingSkills;
    this.suggestedJobs = suggestedJobs;
    this.summary = summary;
}

    public int getAtsScore() {
        return atsScore;
    }

    public void setAtsScore(int atsScore) {
        this.atsScore = atsScore;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public List<String> getStrengths() {
        return strengths;
    }

    public void setStrengths(List<String> strengths) {
        this.strengths = strengths;
    }

    public List<String> getGaps() {
        return gaps;
    }

    public void setGaps(List<String> gaps) {
        this.gaps = gaps;
    }
    public List<String> getMatchedSkills() {
    return matchedSkills;
}

public void setMatchedSkills(List<String> matchedSkills) {
    this.matchedSkills = matchedSkills;
}

public List<String> getMissingSkills() {
    return missingSkills;
}

public void setMissingSkills(List<String> missingSkills) {
    this.missingSkills = missingSkills;
}

    public List<String> getSuggestedJobs() {
        return suggestedJobs;
    }

    public void setSuggestedJobs(List<String> suggestedJobs) {
        this.suggestedJobs = suggestedJobs;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}

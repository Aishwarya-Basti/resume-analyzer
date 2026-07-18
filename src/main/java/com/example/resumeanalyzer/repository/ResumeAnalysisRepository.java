package com.example.resumeanalyzer.repository;

import com.example.resumeanalyzer.entity.ResumeAnalysis;
import com.example.resumeanalyzer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {

    List<ResumeAnalysis> findByUserOrderByCreatedAtDesc(User user);

    Optional<ResumeAnalysis> findByIdAndUser(Long id, User user);

}

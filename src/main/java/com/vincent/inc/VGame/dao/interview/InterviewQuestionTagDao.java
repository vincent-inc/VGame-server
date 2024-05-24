package com.vincent.inc.VGame.dao.interview;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vincent.inc.VGame.model.interview.InterviewQuestionTag;

public interface InterviewQuestionTagDao extends JpaRepository<InterviewQuestionTag, Integer> {
    
}

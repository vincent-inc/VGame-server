package com.vincent.inc.VGame.service;

import org.springframework.stereotype.Service;

import com.vincent.inc.VGame.dao.interview.InterviewQuestionTagDao;
import com.vincent.inc.VGame.model.interview.InterviewQuestionTag;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseCall;

@Service
public class InterviewQuestionTagService extends ViesService<InterviewQuestionTag, Integer, InterviewQuestionTagDao> {

    public InterviewQuestionTagService(DatabaseCall<InterviewQuestionTag, Integer> databaseCall,
            InterviewQuestionTagDao repositoryDao) {
        super(databaseCall, repositoryDao);
    }

    @Override
    protected InterviewQuestionTag newEmptyObject() {
        return new InterviewQuestionTag();
    }
    
}

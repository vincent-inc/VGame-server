package com.vincent.inc.VGame.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.interview.InterviewQuestionTag;
import com.vincent.inc.VGame.service.InterviewQuestionTagService;
import com.vincent.inc.viesspringutils.controller.ViesController;

@RestController
@RequestMapping("/interview_question_tags")
public class InterviewQuestionTagController extends ViesController<InterviewQuestionTag, Integer, InterviewQuestionTagService> {

    public InterviewQuestionTagController(InterviewQuestionTagService service) {
        super(service);
    }
    
}
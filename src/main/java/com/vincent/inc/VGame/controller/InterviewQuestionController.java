package com.vincent.inc.VGame.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.interview.InterviewQuestion;
import com.vincent.inc.VGame.service.InterviewQuestionService;
import com.vincent.inc.viesspringutils.controller.ViesController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/interview_questions")
public class InterviewQuestionController extends ViesController<InterviewQuestion, Integer, InterviewQuestionService> {

    public InterviewQuestionController(InterviewQuestionService service) {
        super(service);
    }
    
    @GetMapping("sync")
    public List<InterviewQuestion> getMSIQuestions() {
        return this.service.syncMsiQuestions();
    }
}

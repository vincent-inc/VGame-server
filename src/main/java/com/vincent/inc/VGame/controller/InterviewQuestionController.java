package com.vincent.inc.VGame.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.interview.InterviewQuestion;
import com.vincent.inc.VGame.service.InterviewQuestionService;
import com.vincent.inc.viesspringutils.controller.ViesController;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/interview_questions")
public class InterviewQuestionController extends ViesController<InterviewQuestion, Integer, InterviewQuestionService> {

    private final ExecutorService threadPool = Executors.newCachedThreadPool();

    public InterviewQuestionController(InterviewQuestionService service) {
        super(service);
    }
    
    @GetMapping("sync")
    public List<InterviewQuestion> syncMSIQuestions() {
        return this.service.syncMsiQuestions();
    }

    @GetMapping("async")
    public Map<String, String> asyncMSIQuestions() {
        this.threadPool.submit(this.service::syncMsiQuestions);
        return Map.of("status", "ok");
    }
}

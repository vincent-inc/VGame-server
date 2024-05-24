package com.vincent.inc.VGame.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.vincent.inc.VGame.service.InterviewQuestionService;


@Configuration
@EnableScheduling
public class SyncInterviewQuestionSchedule {

    @Autowired
    private InterviewQuestionService interviewQuestionService;

    @Scheduled(cron = "0 0 0 * * 0,2,4")
    public void init() {
        this.interviewQuestionService.syncMsiQuestions();
    }
}

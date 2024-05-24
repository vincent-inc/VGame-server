package com.vincent.inc.VGame.model.interview;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSIQuestionResponse {
    private boolean success;
    private List<Question> questions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        private String id;
        private String title;
        private String content;
        private String answer;
        private List<String> tags;
    }
}


package com.vincent.inc.VGame.model.interview;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "interview_question")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewQuestion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String sub;

    @Column(columnDefinition = "BLOB")
    private String title;
    
    @Column(columnDefinition = "BLOB")
    private String content;
    
    @Column(columnDefinition = "BLOB")
    private String answer;
    
    @Column
    private List<String> tags;

    public static InterviewQuestion of(MSIQuestionResponse.Question question) {
        String id = question.getId();
        String title = question.getTitle();
        String content = question.getContent();
        String answer = question.getAnswer();
        List<String> tags = question.getTags();
        return InterviewQuestion.builder().id(0).sub(id + "").title(title).content(content).answer(answer).tags(tags).build();
    }
}

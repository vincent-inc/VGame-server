package com.vincent.inc.VGame.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.vincent.inc.VGame.dao.interview.InterviewQuestionDao;
import com.vincent.inc.VGame.model.interview.InterviewQuestion;
import com.vincent.inc.VGame.model.interview.MSILoginRequest;
import com.vincent.inc.VGame.model.interview.MSIQuestionResponse;
import com.vincent.inc.VGame.openfiegn.MSIClient;
import com.vincent.inc.viesspringutils.model.GenericPropertyMatcherEnum;
import com.vincent.inc.viesspringutils.service.ViesService;
import com.vincent.inc.viesspringutils.util.DatabaseCall;

@Service
public class InterviewQuestionService extends ViesService<InterviewQuestion, Integer, InterviewQuestionDao> {

    @Value("${msi.backend.username}")
    private String msiLoginUsername;

    @Value("${msi.backend.password}")
    private String msiLoginPassword;

    @Autowired
    private InterviewQuestionTagService interviewQuestionTagService;

    @Autowired
    private MSIClient msiClient;

    public InterviewQuestionService(DatabaseCall<InterviewQuestion, Integer> databaseCall,
            InterviewQuestionDao repositoryDao) {
        super(databaseCall, repositoryDao);
    }

    @Override
    protected InterviewQuestion newEmptyObject() {
        return new InterviewQuestion();
    }

    /**
     * Synchronizes the MSI questions by fetching them from the MSI backend,
     * processing each question in parallel, and updating or creating them
     * in the local repository. Finally, returns all the interview questions.
     *
     * @return         	A list of all the interview questions after synchronization
     */
    public List<InterviewQuestion> syncMsiQuestions() {
        MSIQuestionResponse msiQuestions = fetchMsiQuestions();

        msiQuestions.getQuestions().stream().forEach(question -> {
            InterviewQuestion interviewQuestion = InterviewQuestion.of(question);
            var newTags = interviewQuestion.getTags().parallelStream().map(e -> this.interviewQuestionTagService.getOrPostIfMatchAny(e, GenericPropertyMatcherEnum.IGNORE_CASE)).collect(Collectors.toList());
            interviewQuestion.setTags(newTags);
            var foundInterviewQuestion = this.repositoryDao.findBySub(interviewQuestion.getSub());
            if(!ObjectUtils.isEmpty(foundInterviewQuestion))
                this.patch(foundInterviewQuestion.getId(), interviewQuestion);
            else
                this.post(interviewQuestion);
        });

        return this.getAll();
    }
    
    public MSIQuestionResponse fetchMsiQuestions() {
        return msiClient.getQuestions("Bearer " + getAccessToken());
    }
 
    private String getAccessToken() {
        return msiClient.getMSILoginToken(new MSILoginRequest(this.msiLoginUsername, this.msiLoginPassword, true)).getToken();
    }
}

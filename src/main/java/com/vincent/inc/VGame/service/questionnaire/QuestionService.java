package com.vincent.inc.VGame.service.questionnaire;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Example;
import org.springframework.data.redis.core.RedisTemplate;

import com.google.gson.Gson;
import com.vincent.inc.VGame.dao.questionnaire.QuestionDao;
import com.vincent.inc.VGame.model.questionnaire.Question;
import com.vincent.inc.VGame.util.ReflectionUtils;
import com.vincent.inc.VGame.util.splunk.Splunk;

@Service
public class QuestionService
{
    public static final String HASH_KEY = "com.vincent.inc.VGame.questions";

    // @Value("${spring.cache.redis.questionTTL}")
    private int questionTTL = 600;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private QuestionDao questionDao;

    public List<Question> getAll()
    {
        return this.questionDao.findAll();
    }

    public Question getById(int id)
    {
        //get from redis
        String key = String.format("%s.%s", HASH_KEY, id);
        try
        {
            String jsonQuestion = this.redisTemplate.opsForValue().get(key);
            if(jsonQuestion != null)
                return this.gson.fromJson(jsonQuestion, Question.class);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        //get from database
        Optional<Question> oQuestion = this.questionDao.findById(id);

        if(oQuestion.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Question ID not found");

        Question question = oQuestion.get();

        //save to redis
        try
        {
            this.redisTemplate.opsForValue().set(key, gson.toJson(question));
            this.redisTemplate.expire(key, questionTTL, TimeUnit.SECONDS);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return question;
    }

    public List<Question> getAllByMatchAll(Question question)
    {
        Example<Question> example = (Example<Question>) ReflectionUtils.getMatchAllMatcher(question);
        return this.questionDao.findAll(example);
    }

    public List<Question> getAllByMatchAny(Question question)
    {
        Example<Question> example = (Example<Question>) ReflectionUtils.getMatchAnyMatcher(question);
        return this.questionDao.findAll(example);
    }

    public Question createQuestion(Question question)
    {
        question = this.questionDao.save(question);
        return question;
    }

    public Question modifyQuestion(int id, Question question)
    {
        Question oldQuestion = this.getById(id);

		oldQuestion.setQuestion(question.getQuestion());
		oldQuestion.setCategory(question.getCategory());
		oldQuestion.setPossibleAnswer(question.getPossibleAnswer());
		oldQuestion.setOrderBy(question.getOrderBy());
		oldQuestion.setAnswer(question.getAnswer());


        oldQuestion = this.questionDao.save(oldQuestion);

        //remove from redis
        try
        {
            String key = String.format("%s.%s", HASH_KEY, id);
            this.redisTemplate.delete(key);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return oldQuestion;
    }

    public Question patchQuestion(int id, Question question)
    {
        Question oldQuestion = this.getById(id);

		oldQuestion.setQuestion(question.getQuestion() == null ? oldQuestion.getQuestion() : question.getQuestion());
		oldQuestion.setCategory(question.getCategory() == null ? oldQuestion.getCategory() : question.getCategory());
		oldQuestion.setPossibleAnswer(question.getPossibleAnswer() == null ? oldQuestion.getPossibleAnswer() : question.getPossibleAnswer());
		oldQuestion.setOrderBy(question.getOrderBy());
		oldQuestion.setAnswer(question.getAnswer() == null ? oldQuestion.getAnswer() : question.getAnswer());


        oldQuestion = this.questionDao.save(oldQuestion);

        //remove from redis
        try
        {
            String key = String.format("%s.%s", HASH_KEY, id);
            this.redisTemplate.delete(key);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }

        return oldQuestion;
    }

    public void deleteQuestion(int id)
    {
        this.questionDao.deleteById(id);

        //remove from redis
        try
        {
            String key = String.format("%s.%s", HASH_KEY, id);
            this.redisTemplate.delete(key);
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
        }
    }
}
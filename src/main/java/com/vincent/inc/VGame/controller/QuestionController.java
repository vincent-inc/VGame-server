package com.vincent.inc.VGame.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.ErrorResponseException;

import jakarta.ws.rs.QueryParam;
import io.swagger.v3.oas.annotations.Operation;

import com.vincent.inc.VGame.model.questionnaire.Question;
import com.vincent.inc.VGame.service.questionnaire.QuestionService;
import com.vincent.inc.VGame.util.splunk.Splunk;

@RestController
@RequestMapping("/questions")
class QuestionController
{
    @Autowired
    QuestionService questionService;

    @Operation(summary = "Get a list of all Question")
    @GetMapping
    public ResponseEntity<List<Question>> getAll()
    {
        try
        {
            List<Question> questions = questionService.getAll();

            if (questions.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get Question base on id in path variable")
    @GetMapping("{id}")
    public ResponseEntity<Question> getById(@PathVariable("id") int id)
    {
        try
        {
            Question question = questionService.getById(id);

            return new ResponseEntity<>(question, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get a list of all Question that match all information base on query parameter")
    @GetMapping("match_all")
    public ResponseEntity<List<Question>> matchAll(@QueryParam("question") Question question)
    {
        try
        {
            List<Question> questions = this.questionService.getAllByMatchAll(question);

            if (questions.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get a list of all Question that match any information base on query parameter")
    @GetMapping("match_any")
    public ResponseEntity<List<Question>> matchAny(@QueryParam("question") Question question)
    {
        try
        {
            List<Question> questions = this.questionService.getAllByMatchAny(question);

            if (questions.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Get a list of all Question that match any information base on query parameter")
    @GetMapping("match_any/hide_answer")
    public ResponseEntity<List<Question>> matchAnyHideAnswer(@QueryParam("question") Question question)
    {
        try
        {
            List<Question> questions = this.questionService.getAllByMatchAny(question);

            questions.forEach(q -> q.setAnswer(null));

            if (questions.isEmpty())
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);

            return new ResponseEntity<>(questions, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Create a new Question")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Question> create(@RequestBody Question question)
    {
        try
        {
            Question savedQuestion = questionService.createQuestion(question);
            return new ResponseEntity<>(savedQuestion, HttpStatus.CREATED);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @Operation(summary = "Modify an Question base on id in path variable")
    @PutMapping("{id}")
    public ResponseEntity<Question> update(@PathVariable("id") int id, @RequestBody Question question)
    {
        try
        {
            question = this.questionService.modifyQuestion(id, question);

            return new ResponseEntity<>(question, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Patch an Question base on id in path variable")
    @PatchMapping("{id}")
    public ResponseEntity<Question> patch(@PathVariable("id") int id, @RequestBody Question question)
    {
        try
        {
            question = this.questionService.patchQuestion(id, question);

            return new ResponseEntity<>(question, HttpStatus.OK);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch(Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Delete an Question base on id in path variable")
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id)
    {
        try
        {
            questionService.deleteQuestion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        catch(ErrorResponseException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            Splunk.logError(ex);
            return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
        }
    }
}
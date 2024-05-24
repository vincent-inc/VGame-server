package com.vincent.inc.VGame.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.interview.InterviewQuestion;
import com.vincent.inc.viesspringutils.exception.HttpResponseThrowers;
import com.vincent.inc.viesspringutils.model.GenericPropertyMatcherEnum;
import com.vincent.inc.viesspringutils.model.MatchByEnum;

import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("tests")
public class TestController {
    
    @PostMapping("bad_request")
    public void testBadRequest() {
        HttpResponseThrowers.throwBadRequest("Bad request test");
    }

    @GetMapping("pageable")
    public Object getMethodName(
        @RequestParam(value = "page", required = false) int page,
        @RequestParam(value = "size", required = false) int size, 
        @RequestHeader(value = "user_id",required = false) String user_id, 
        @ModelAttribute InterviewQuestion object,
        @RequestParam(required = false) GenericPropertyMatcherEnum.PropertyMatcherEnum matchCase, 
        @RequestParam(required = false) MatchByEnum matchBy) {
        return ObjectUtils.isEmpty(object);
    }
    
}

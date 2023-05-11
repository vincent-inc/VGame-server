package com.vincent.inc.VGame.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.util.Http.HttpResponseThrowers;


@RestController
@RequestMapping("tests")
public class TestController {
    
    @PostMapping("bad_request")
    public void testBadRequest() {
        HttpResponseThrowers.throwBadRequest("Bad request test");
    }
}

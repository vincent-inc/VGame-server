package com.vincent.inc.VGame.openfiegn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.vincent.inc.VGame.model.interview.MSILoginRequest;
import com.vincent.inc.VGame.model.interview.MSILoginResponse;
import com.vincent.inc.VGame.model.interview.MSIQuestionResponse;

import feign.Headers;

@FeignClient("${msi.backend.url}")
@Headers("Content-Type: application/json")
public interface MSIClient {
    
    /**
     * Sends a POST request to the server to retrieve the MSI login token.
     *
     * @param  body  the MSI login request body containing the necessary information
     * @return       the MSI login response containing the token and other relevant information
     */
    @PostMapping("login")
    public MSILoginResponse getMSILoginToken(@RequestBody MSILoginRequest body);

    /**
     * Retrieves the MSI question response by sending a GET request to the server with the provided token in the Authorization header.
     *
     * @param  token  the token to be included in the Authorization header
     * @return        the MSI question response received from the server
     */
    @GetMapping("questions")
    public MSIQuestionResponse getQuestions(@RequestHeader("Authorization") String token);
}

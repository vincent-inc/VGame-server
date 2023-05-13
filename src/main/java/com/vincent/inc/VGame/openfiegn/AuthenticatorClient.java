package com.vincent.inc.VGame.openfiegn;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.vincent.inc.VGame.model.authenticator.User;

import feign.Headers;

@FeignClient("AUTHENTICATOR-SERVICE")
@Headers("Content-Type: application/json")
public interface AuthenticatorClient {
    public final String USERS = "users";
    
    @GetMapping("/" + USERS + "/{id}")
    public User getById(@PathVariable("id") int id);
}

package com.vincent.inc.VGame.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.authenticator.User;
import com.vincent.inc.VGame.openfiegn.AuthenticatorClient;
import com.vincent.inc.VGame.service.LobbyService;

@RestController
@RequestMapping("lobbies")
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @Autowired
    private AuthenticatorClient authenticatorClient;

    @GetMapping()
    public User getLobbies(@RequestHeader("user_id") int userId) {
        return this.authenticatorClient.getById(userId);
    }
}

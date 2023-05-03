package com.vincent.inc.VGame.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.model.authenticator.User;
import com.vincent.inc.VGame.openfiegn.AuthenticatorClient;
import com.vincent.inc.VGame.service.LobbyService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("lobbies")
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @GetMapping()
    public List<Lobby> getLobbies(@RequestHeader("user_id") int userId) {
        return this.lobbyService.getAll();
    }

    @PostMapping()
    public Lobby createLobby(@RequestHeader("user_id") int userId, @RequestBody Lobby lobby) {
        return this.lobbyService.createLobby(lobby, userId);
    }
}

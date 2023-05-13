package com.vincent.inc.VGame.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.service.LobbyService;
import com.vincent.inc.VGame.util.Http.HttpResponseThrowers;

@RestController
@RequestMapping("lobbies")
public class LobbyController {

    @Autowired
    private LobbyService lobbyService;

    @GetMapping()
    public List<Lobby> getLobbies() {
        return this.lobbyService.getAll();
    }

    @GetMapping("{id}")
    public Lobby getLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId) {
        if(!this.lobbyService.isInLobby(lobbyId, userId))
            return (Lobby) HttpResponseThrowers.throwBadRequest("user does not belong to lobby");

        return this.lobbyService.getLobby(lobbyId, userId);
    }

    @PostMapping()
    public Lobby createLobby(@RequestHeader("user_id") int userId, @RequestBody Lobby lobby) {
        return this.lobbyService.createLobby(lobby, userId);
    }

    @PostMapping("join/{id}")
    public Lobby createLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId) {
        return this.lobbyService.joinLobby(lobbyId, userId);
    }

    @PostMapping("leave/{id}")
    public Lobby leaveLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId) {
        return this.lobbyService.leaveLobby(lobbyId, userId);
    }

    @DeleteMapping("{id}")
    public void deleteLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId) {
        this.lobbyService.deleteLobby(lobbyId, userId);
    }
}

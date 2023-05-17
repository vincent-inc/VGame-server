package com.vincent.inc.VGame.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.model.PasswordPojo;
import com.vincent.inc.VGame.model.chat.Message;
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
            return (Lobby) HttpResponseThrowers.throwBadRequest("User does not belong to lobby");

        return this.lobbyService.getLobby(lobbyId, userId);
    }

    @PostMapping()
    public Lobby createLobby(@RequestBody Lobby lobby) {
        return this.lobbyService.createLobby(lobby);
    }

    @PostMapping("join/{id}")
    public Lobby joinLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId, @RequestBody(required = false) PasswordPojo password) {
        String tempPassword = ObjectUtils.isEmpty(password) ? "" : password.getPassword();

        if(this.lobbyService.isCorrectPassword(lobbyId, tempPassword))
            return (Lobby) HttpResponseThrowers.throwBadRequest("Wrong lobby password");
        
        return this.lobbyService.joinLobby(lobbyId, userId);
    }

    @PostMapping("leave/{id}")
    public Lobby leaveLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId) {
        if(!this.lobbyService.isInLobby(lobbyId, userId))
            return (Lobby) HttpResponseThrowers.throwBadRequest("User does not belong to lobby");

        return this.lobbyService.leaveLobby(lobbyId, userId);
    }

    @PostMapping("chat/{id}")
    public Lobby chat(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId, @RequestBody Message message) {
        if(!this.lobbyService.isInLobby(lobbyId, userId))
            return (Lobby) HttpResponseThrowers.throwBadRequest("User does not belong to lobby");
        
        return this.lobbyService.sendMessage(lobbyId, userId, message);
    }

    @PatchMapping("{id}")
    public Lobby patchLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId, @RequestBody Lobby lobby) {
        return this.lobbyService.patchLobby(lobbyId, userId, lobby);
    }

    @DeleteMapping("{id}")
    public void deleteLobby(@RequestHeader("user_id") int userId, @PathVariable("id") String lobbyId) {
        this.lobbyService.deleteLobby(lobbyId, userId);
    }
}

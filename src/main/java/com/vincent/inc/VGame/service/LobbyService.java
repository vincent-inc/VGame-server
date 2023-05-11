package com.vincent.inc.VGame.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.model.authenticator.User;
import com.vincent.inc.VGame.openfiegn.AuthenticatorClient;
import com.vincent.inc.VGame.util.HttpResponseThrowers;
import com.vincent.inc.VGame.util.Sha256PasswordEncoder;

@Service
public class LobbyService {

    public static final String HASH_KEY = "com.vincent.inc.VGame.service.LobbyService";

    private int lobbyTTL = 3000;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthenticatorClient authenticatorClient;

    @Autowired
    private Sha256PasswordEncoder sha256PasswordEncoder;

    @Autowired
    private Gson gson;

    public List<Lobby> getAll() {
        List<Lobby> lobbies = new ArrayList<>();
        List<String> lobbyList = this.getLobbyIdList();

        for (int i = 0; i < lobbyList.size(); i++) {
            String key = String.format("%s.%s", HASH_KEY, lobbyList.get(i));
            String lobby = this.redisTemplate.opsForValue().get(key);

            if(lobby == null) {
                this.removeLobbyIndex(i);
                continue;
            }

            lobbies.add(this.gson.fromJson(lobby, Lobby.class));
        }

        return lobbies;
    }

    public Lobby getLobby(String lobbyId) {
        String key = String.format("%s.%s", HASH_KEY, lobbyId);
        String lobby = this.redisTemplate.opsForValue().getAndExpire(key, Duration.ofSeconds(lobbyTTL));

        if(lobby == null) {
            return (Lobby) HttpResponseThrowers.throwBadRequest("Lobby does not exist");
        }

        return this.gson.fromJson(lobby, Lobby.class);
    }

    public void setLobby(Lobby lobby) {
        String key = String.format("%s.%s", HASH_KEY, lobby.getId());
        this.redisTemplate.opsForValue().set(key, this.gson.toJson(lobby), Duration.ofSeconds(lobbyTTL));
    }

    public void removeLobby(String lobbyId) {
        String key = String.format("%s.%s", HASH_KEY, lobbyId);
        this.redisTemplate.delete(key);
    }

    public Lobby createLobby(Lobby lobby, int userId) {
        List<String> lobbyList = this.getLobbyIdList();
        User user = this.getUserWithMask(userId);
        if(user == null)
            HttpResponseThrowers.throwBadRequest("User not found"); 

        do {
            String uuid = UUID.randomUUID().toString();
            lobby.setId(uuid);
        }
        while(lobbyList.parallelStream().anyMatch(e -> e.equals(lobby.getId())));

        String key = String.format("%s.%s", HASH_KEY, lobby.getId());

        lobby.getLobbyGame().setHost(user);

        if(!ObjectUtils.isEmpty(lobby.getPassword()))
            lobby.setPassword(this.sha256PasswordEncoder.encode(lobby.getPassword()));
        
        this.redisTemplate.opsForValue().set(key, this.gson.toJson(lobby), Duration.ofSeconds(lobbyTTL));

        key = String.format("%s.%s", HASH_KEY, "lobbies");

        this.pushLobbyIdList(lobby);

        return lobby;
    }

    public Lobby joinLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);

        if(lobby.getCurrentNumberOfPlayer() > lobby.getMaxPlayer())
            HttpResponseThrowers.throwBadRequest("Max player reach");

        addToSpectatingList(lobby, user);
        this.setLobby(lobby);
        return lobby;
    }

    public Lobby leaveLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);

        lobby.getLobbyGame().getSpectatingList().remove(user);
        if(isHost(lobby, user)) {
            lobby.getLobbyGame().setHost(null);
            this.autoAssignHost(lobby);
        }

        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() - 1);
        
        this.setLobby(lobby);
        return lobby;
    }

    public void autoCountPlayer(Lobby lobby, User user) {

    }

    public void addToSpectatingList(Lobby lobby, User user) {
        if(lobby.getLobbyGame().getSpectatingList().parallelStream().anyMatch(u -> u.getId() == user.getId()))
            return;

        lobby.getLobbyGame().getSpectatingList().add(user);
        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() + 1);
    }

    public void autoAssignHost(Lobby lobby) {
        if(ObjectUtils.isEmpty(lobby.getLobbyGame().getHost())) {
            Optional<User> user = lobby.getLobbyGame().getSpectatingList().stream().findAny();
            if(user.isPresent())
                lobby.getLobbyGame().setHost(user.get());
        }
    }

    public boolean isHost(Lobby lobby, User user) {
        return lobby.getLobbyGame().getHost().getId() == user.getId();
    }

    public void deleteLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);
        if(isHost(lobby, user))
            this.removeLobby(lobbyId);
    }

    // extra function

    public List<String> getLobbyIdList() {
        String key = String.format("%s.%s", HASH_KEY, "lobbies");

        long size = this.redisTemplate.opsForList().size(key);

        List<String> list = this.redisTemplate.opsForList().range(key, 0, size);

        this.redisTemplate.expire(key, Duration.ofSeconds(this.lobbyTTL * 10));

        return list;
    }

    private void pushLobbyIdList(Lobby lobby) {
        String key = String.format("%s.%s", HASH_KEY, "lobbies");
        this.redisTemplate.opsForList().rightPush(key, lobby.getId());
    }

    private void removeLobbyById(String id) {
        String key = String.format("%s.%s", HASH_KEY, "lobbies");
        long index = this.redisTemplate.opsForList().indexOf(key, id);
        this.redisTemplate.opsForList().remove(key, index, id);
    }

    private void removeLobbyIndex(int index) {
        String key = String.format("%s.%s", HASH_KEY, "lobbies");
        String uuid = this.redisTemplate.opsForList().index(key, index);
        this.redisTemplate.opsForList().remove(key, index, uuid);
    }

    private User getUserWithMask(int userId) {
        User user = this.authenticatorClient.getById(userId);
        user.setPassword(null);
        user.getUserProfile().setAddress(null);
        user.getUserProfile().setCity(null);
        user.getUserProfile().setEmail(null);
        user.getUserProfile().setPhoneNumber(null);
        user.getUserProfile().setState(null);
        user.getUserProfile().setZip(null);

        return user;
    }


}

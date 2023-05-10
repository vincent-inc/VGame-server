package com.vincent.inc.VGame.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.model.authenticator.User;
import com.vincent.inc.VGame.openfiegn.AuthenticatorClient;
import com.vincent.inc.VGame.util.HttpResponseThrowers;

@Service
public class LobbyService {

    public static final String HASH_KEY = "com.vincent.inc.VGame.service.LobbyService";

    private int lobbyTTL = 3000;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private AuthenticatorClient authenticatorClient;

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
        
        this.redisTemplate.opsForValue().set(key, this.gson.toJson(lobby), Duration.ofSeconds(lobbyTTL));

        key = String.format("%s.%s", HASH_KEY, "lobbies");

        this.pushLobbyIdList(lobby);

        return lobby;
    }

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

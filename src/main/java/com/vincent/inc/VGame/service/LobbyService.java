package com.vincent.inc.VGame.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.google.gson.Gson;
import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.model.authenticator.User;
import com.vincent.inc.VGame.model.chat.Message;
import com.vincent.inc.VGame.openfiegn.AuthenticatorClient;
import com.vincent.inc.VGame.util.Sha256PasswordEncoder;
import com.vincent.inc.VGame.util.Time;
import com.vincent.inc.VGame.util.Http.HttpResponseThrowers;

@Service
public class LobbyService {

    public static final String HASH_KEY = "com.vincent.inc.VGame.service.LobbyService";

    private final int lobbyTTL = 3000; //3000s

    private final int checkInOffset = 5; // 5s

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

        Lobby lobbyO = this.gson.fromJson(lobby, Lobby.class);

        return lobbyO;
    }

    public Lobby getLobby(String lobbyId, int userId) {
        String key = String.format("%s.%s", HASH_KEY, lobbyId);
        String lobby = this.redisTemplate.opsForValue().getAndExpire(key, Duration.ofSeconds(lobbyTTL));

        if(lobby == null) 
            return (Lobby) HttpResponseThrowers.throwBadRequest("Lobby does not exist");

        Lobby lobbyO = this.gson.fromJson(lobby, Lobby.class);
        
        this.renewCheckIn(lobbyO, userId);

        lobbyO = this.leaveOverdueUser(lobbyO);

        this.saveLobby(lobbyO);

        return lobbyO;
    }

    public void saveLobby(Lobby lobby) {
        String key = String.format("%s.%s", HASH_KEY, lobby.getId());
        this.redisTemplate.opsForValue().set(key, this.gson.toJson(lobby), Duration.ofSeconds(lobbyTTL));
    }

    public void removeLobby(String lobbyId) {
        String key = String.format("%s.%s", HASH_KEY, lobbyId);
        this.redisTemplate.delete(key);
    }

    public Lobby createLobby(Lobby lobby) {
        List<String> lobbyList = this.getLobbyIdList();

        do {
            String uuid = UUID.randomUUID().toString();
            lobby.setId(uuid);
        }
        while(lobbyList.parallelStream().anyMatch(e -> e.equals(lobby.getId())));

        String key = String.format("%s.%s", HASH_KEY, lobby.getId());

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

        addToPlayerList(lobby, user);
        this.saveLobby(lobby);
        return lobby;
    }

    public Lobby leaveLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);

        lobby.getLobbyGame().setPlayerList(lobby.getLobbyGame().getPlayerList().stream().filter(u -> u.getId() != userId).collect(Collectors.toList()));
        
        if(isHost(lobby, user)) {
            lobby.getLobbyGame().setHost(null);
            this.autoAssignHost(lobby);
        }

        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() - 1);
        
        this.saveLobby(lobby);
        return lobby;
    }

    public void autoCountPlayer(Lobby lobby, User user) {
        
    }

    public boolean isInLobby(Lobby lobby, int userId) {
        return lobby.getLobbyGame().getPlayerList().parallelStream().anyMatch(u -> u.getId() == userId);
    }

    public boolean isInLobby(String lobbyId, int userId) {
        return isInLobby(this.getLobby(lobbyId), userId);
    }

    public void addToPlayerList(Lobby lobby, User user) {
        if(lobby.getLobbyGame().getPlayerList().parallelStream().anyMatch(u -> u.getId() == user.getId()))
            return;

        lobby.getLobbyGame().getPlayerList().add(user);
        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() + 1);
        this.autoAssignHost(lobby);
    }

    public void addToSpectatingList(Lobby lobby, User user) {
        if(lobby.getLobbyGame().getSpectatingList().parallelStream().anyMatch(u -> u.getId() == user.getId()))
            return;

        lobby.getLobbyGame().getSpectatingList().add(user);
        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() + 1);
    }

    public void autoAssignHost(Lobby lobby) {
        if(ObjectUtils.isEmpty(lobby.getLobbyGame().getHost())) {
            Optional<User> user = lobby.getLobbyGame().getPlayerList().stream().findAny();
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

    public Lobby renewCheckIn(Lobby lobby, int userId) {
        Time now = new Time();

        if(lobby.getLobbyGame().getHost().getId() == userId)
            lobby.getLobbyGame().getHost().setLastCheckInTime(now);

        lobby.getLobbyGame().getPlayerList().parallelStream().forEach(u -> {
            if(u.getId() == userId)
                u.setLastCheckInTime(now);
        });

        return lobby;
    }

    public Lobby leaveOverdueUser(Lobby lobby) {
        Time now = new Time();

        // User host = lobby.getLobbyGame().getHost();
        // if(ObjectUtils.isEmpty(host) && isPassCheckInTime(host, now)) {
        //     this.leaveLobby(lobby.getId(), host.getId());
        //     leaveOverdueUser(lobby);
        //     return;
        // }

        // lobby.getLobbyGame().setPlayerList(lobby.getLobbyGame().getPlayerList().stream().filter(u -> this.isPassCheckInTime(u, now)).collect(Collectors.toList()));
        
        List<User> players = lobby.getLobbyGame().getPlayerList();
        for(int count = players.size() - 1; count >= 0; count --) {
            User player = players.get(count);
            if(this.isPassCheckInTime(player, now))
                this.leaveLobby(lobby.getId(), player.getId());
        }

        return lobby;
    }

    public boolean isPassCheckInTime(User user, Time now) {
        return user.getLastCheckInTime().increaseSecond(checkInOffset).isBefore(now);
    }

    //Chatting

    public Lobby sendMessage(String lobbyId, int userId, Message message) {
        message.setTime(new Time());
        message.setSendBy(this.getUserAlias(this.getUserWithMask(userId)));
        Lobby lobby = this.getLobby(lobbyId);
        if(ObjectUtils.isEmpty(lobby.getMessages()))
            lobby.setMessages(new ArrayList<>());

        lobby.getMessages().add(message);
        this.saveLobby(lobby);
        return lobby;
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
        user.getUserProfile().setEmail(null);

        return user;
    }

    private String getUserAlias(User user) {
        if(!ObjectUtils.isEmpty(user.getUserProfile().getAlias()))
            return user.getUserProfile().getAlias();
        
        return user.getUsername();
    }
}

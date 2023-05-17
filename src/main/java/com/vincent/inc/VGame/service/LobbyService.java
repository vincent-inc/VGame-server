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
import com.vincent.inc.VGame.util.ReflectionUtils;
import com.vincent.inc.VGame.util.Sha256PasswordEncoder;
import com.vincent.inc.VGame.util.Time;
import com.vincent.inc.VGame.util.Http.HttpResponseThrowers;

@Service
public class LobbyService {

    public static final String HASH_KEY = "com.vincent.inc.VGame.service.LobbyService";

    private static final int MAX_MESSAGE = 50;

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

            lobbies.add(this.maskLobby(this.gson.fromJson(lobby, Lobby.class)));
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

    public Lobby saveLobby(Lobby lobby) {
        String key = String.format("%s.%s", HASH_KEY, lobby.getId());
        this.redisTemplate.opsForValue().set(key, this.gson.toJson(lobby), Duration.ofSeconds(lobbyTTL));
        return lobby;
    }

    public void removeLobby(String lobbyId) {
        String key = String.format("%s.%s", HASH_KEY, lobbyId);
        this.redisTemplate.delete(key);
    }

    public Lobby joinLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);

        if(lobby.getCurrentNumberOfPlayer() > lobby.getMaxPlayer())
            HttpResponseThrowers.throwBadRequest("Max player reach");

        addToPlayerList(lobby, user);

        this.autoCountPlayer(lobby);
        
        return this.saveLobby(lobby);
    }

    public Lobby leaveLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);

        lobby.getLobbyInfo().setPlayerList(lobby.getLobbyInfo().getPlayerList().stream().filter(u -> u.getId() != userId).collect(Collectors.toList()));
        
        if(isHost(lobby, user)) {
            lobby.getLobbyInfo().setHost(null);
            this.autoAssignHost(lobby);
        }

        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() - 1);

        this.autoCountPlayer(lobby);
        
        return this.saveLobby(lobby);
    }

    public void autoCountPlayer(Lobby lobby) {
        int numberOfPlayer = lobby.getLobbyInfo().getPlayerList().size();
        lobby.setCurrentNumberOfPlayer(numberOfPlayer);
    }

    public boolean isInLobby(Lobby lobby, int userId) {
        return lobby.getLobbyInfo().getPlayerList().parallelStream().anyMatch(u -> u.getId() == userId);
    }

    public boolean isInLobby(String lobbyId, int userId) {
        return isInLobby(this.getLobby(lobbyId), userId);
    }

    public void addToPlayerList(Lobby lobby, User user) {
        if(lobby.getLobbyInfo().getPlayerList().parallelStream().anyMatch(u -> u.getId() == user.getId()))
            return;

        lobby.getLobbyInfo().getPlayerList().add(user);
        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() + 1);
        this.autoAssignHost(lobby);
    }

    public void addToSpectatingList(Lobby lobby, User user) {
        if(lobby.getLobbyInfo().getSpectatingList().parallelStream().anyMatch(u -> u.getId() == user.getId()))
            return;

        lobby.getLobbyInfo().getSpectatingList().add(user);
        lobby.setCurrentNumberOfPlayer(lobby.getCurrentNumberOfPlayer() + 1);
    }

    public void autoAssignHost(Lobby lobby) {
        if(ObjectUtils.isEmpty(lobby.getLobbyInfo().getHost())) {
            Optional<User> user = lobby.getLobbyInfo().getPlayerList().stream().findAny();
            if(user.isPresent())
                lobby.getLobbyInfo().setHost(user.get());
        }
    }

    public boolean isHost(Lobby lobby, User user) {
        return lobby.getLobbyInfo().getHost().getId() == user.getId();
    }

    public boolean isCorrectPassword(String lobbyId, String password) {
        Lobby lobby = this.getLobby(lobbyId);
        if(!ObjectUtils.isEmpty(lobby.getPassword())) {
            return this.sha256PasswordEncoder.matches(password, lobby.getPassword());
        }
        
        return true;
    }

    public void deleteLobby(String lobbyId, int userId) {
        User user = this.getUserWithMask(userId);
        Lobby lobby = this.getLobby(lobbyId);
        if(isHost(lobby, user))
            this.removeLobby(lobbyId);
    }

    public Lobby renewCheckIn(Lobby lobby, int userId) {
        Time now = new Time();

        if(lobby.getLobbyInfo().getHost().getId() == userId)
            lobby.getLobbyInfo().getHost().setLastCheckInTime(now);

        lobby.getLobbyInfo().getPlayerList().parallelStream().forEach(u -> {
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
        
        List<User> players = lobby.getLobbyInfo().getPlayerList();
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

        while(lobby.getMessages().size() > MAX_MESSAGE)
            lobby.getMessages().remove(0);

        this.saveLobby(lobby);
        return lobby;
    }

    //usual operation

    public Lobby createLobby(Lobby lobby) {
        List<String> lobbyList = this.getLobbyIdList();

        do {
            String uuid = UUID.randomUUID().toString();
            lobby.setId(uuid);
        }
        while(lobbyList.parallelStream().anyMatch(e -> e.equals(lobby.getId())));

        String key = String.format("%s.%s", HASH_KEY, lobby.getId());

        lobby.setPassword(this.encodePassword(lobby.getPassword()));
        
        this.redisTemplate.opsForValue().set(key, this.gson.toJson(lobby), Duration.ofSeconds(lobbyTTL));

        key = String.format("%s.%s", HASH_KEY, "lobbies");

        this.pushLobbyIdList(lobby);

        return lobby;
    }

    public Lobby  patchLobby(String id, int userId, Lobby lobby) {
        User user = this.getUserWithMask(userId);
        Lobby oldLobby = this.getLobby(id);
        String oldPassword = oldLobby.getPassword();
        String newPassword = lobby.getPassword();
        if(oldLobby.getLobbyInfo().getHost().getId() != user.getId())
            return (Lobby) HttpResponseThrowers.throwBadRequest("User is not lobby host");

        ReflectionUtils.patchValue(oldLobby, lobby);
        oldLobby.setPassword(this.encodePassword(oldPassword, newPassword));

        return this.saveLobby(oldLobby);
    }

    // extra function

    public List<String> getLobbyIdList() {
        String key = String.format("%s.%s", HASH_KEY, "lobbies");

        long size = this.redisTemplate.opsForList().size(key);

        List<String> list = this.redisTemplate.opsForList().range(key, 0, size);

        this.redisTemplate.expire(key, Duration.ofSeconds(this.lobbyTTL * 10));

        return list;
    }

    public String encodePassword(String password) {
        if(!ObjectUtils.isEmpty(password))
            password = this.sha256PasswordEncoder.encode(password);
        else {
            password = "";
        }

        return password;
    }

    public String encodePassword(String oldPassword, String newPassword) {
        if(!ObjectUtils.isEmpty(newPassword) && !this.sha256PasswordEncoder.matches(newPassword, oldPassword))
            oldPassword = this.sha256PasswordEncoder.encode(newPassword);
        else
            oldPassword = "";

        return oldPassword;
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

    private Lobby maskLobby(Lobby lobby) {
        lobby.setMessages(null);
        return lobby;
    }

    private Lobby getLobbyWithMask(String lobbyId) {
        Lobby lobby = this.getLobby(lobbyId);
        return maskLobby(lobby);
    }

    private String getUserAlias(User user) {
        if(!ObjectUtils.isEmpty(user.getUserProfile().getAlias()))
            return user.getUserProfile().getAlias();
        
        return user.getUsername();
    }
}

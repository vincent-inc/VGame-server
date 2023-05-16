package com.vincent.inc.VGame.model;

import java.util.ArrayList;
import java.util.List;

import com.vincent.inc.VGame.model.authenticator.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class LobbyGame {
    private User host;
    private List<User> playerList;
    private List<User> spectatingList;

    public LobbyGame() {
        this.playerList = new ArrayList<>();
        this.spectatingList = new ArrayList<>();
    }
}

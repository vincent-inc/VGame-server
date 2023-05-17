package com.vincent.inc.VGame.model;

import java.util.ArrayList;
import java.util.List;

import com.vincent.inc.VGame.model.Battleship.BattleshipGame;
import com.vincent.inc.VGame.model.chat.Message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Lobby {
    private String id;

    private String name;

    private String description;
    
    private String currentGame = "N/A";

    private String password;

    private int currentNumberOfPlayer;

    private int maxPlayer = 2;

    private LobbyInfo lobbyInfo;

    private BattleshipGame battleshipGame;

    private List<Message> messages;
    
    public Lobby() {
        this.lobbyInfo = new LobbyInfo();
        this.battleshipGame = new BattleshipGame();
        messages = new ArrayList<>();
    }
}

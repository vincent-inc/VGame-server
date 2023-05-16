package com.vincent.inc.VGame.model;

import com.vincent.inc.VGame.model.Battleship.BattleshipGame;

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

    private LobbyGame lobbyGame;

    private BattleshipGame battleshipGame;
    
    public Lobby() {
        this.lobbyGame = new LobbyGame();
        this.battleshipGame = new BattleshipGame();
    }
}

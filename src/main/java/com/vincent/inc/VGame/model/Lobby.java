package com.vincent.inc.VGame.model;

import com.vincent.inc.VGame.model.Battleship.BattleshipGame;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lobby {
    private int id;

    private String name;

    private String description;
    
    @Builder.Default
    private String currentGame = "N/A";

    private String password;

    private int currentNumberOfPlayer;

    private int maxPlayer;

    private LobbyGame lobbyGame;

    private BattleshipGame battleshipGame;
}

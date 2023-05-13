package com.vincent.inc.VGame.model.Battleship;

import java.util.ArrayList;
import java.util.List;

import com.vincent.inc.VGame.model.authenticator.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Board {
    public static String EMPTY = "";
    public static String HIT = "X";
    public static String SHIP = "S";
    public static String MISS_HIT = "M";

    private User player;

    private int currentNumberOfShip;
    
    //row | column
    private List<List<String>> matrix;
    
    public Board() {
        this.resetBoard(0);
    }

    public Board(BattleshipGame battleshipGame) {
        this.resetBoard(battleshipGame.getGridSize());
    }

    public void resetBoard(int gridSize) {
        this.matrix = new ArrayList<>();

        for (int i = 0; i < gridSize; i++) {
            List<String> row = new ArrayList<>();
            for (int j = 0; j < gridSize; j++) {
                row.add(EMPTY);
            }
            this.matrix.add(row);
        }
    }
}

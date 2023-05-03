package com.vincent.inc.VGame.model.Battleship;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleshipGame {

    private int maxPlayer = 2;

    private int currentNumberOfPlayer = this.getBoards().size();

    private int gridSize = 7;

    private int maxNumberOfShip = 5;

    private List<Board> boards = new ArrayList<>();
}

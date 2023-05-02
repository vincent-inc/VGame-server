package com.vincent.inc.VGame.model.Battleship;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BattleshipGame {
    private List<Board> boards;
}

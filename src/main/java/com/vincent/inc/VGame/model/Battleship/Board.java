package com.vincent.inc.VGame.model.Battleship;

import com.vincent.inc.VGame.model.authenticator.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    private User player;
    
}

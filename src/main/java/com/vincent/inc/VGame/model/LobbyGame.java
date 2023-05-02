package com.vincent.inc.VGame.model;

import java.util.List;

import com.vincent.inc.VGame.model.authenticator.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LobbyGame {
    private User host;
    private List<User> playerList;
    private List<User> spectatingList;
    private List<String> conversation;
}

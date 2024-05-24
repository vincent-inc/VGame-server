package com.vincent.inc.VGame.model.authenticator;

import java.util.List;

import com.vincent.inc.viesspringutils.model.User.Role;
import com.vincent.inc.viesspringutils.model.User.UserProfile;
import com.vincent.inc.viesspringutils.util.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;

    private String username;

    private String password;

    private UserProfile userProfile;

    private List<Role> userRoles;

    private DateTime lastCheckInTime;

    public static User of(com.vincent.inc.viesspringutils.model.User.User user) {
        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .userProfile(user.getUserProfile())
                .userRoles(user.getUserRoles()).build();
    }
}

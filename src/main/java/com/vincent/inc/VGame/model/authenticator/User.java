package com.vincent.inc.VGame.model.authenticator;

import java.util.List;

import com.vincent.inc.VGame.util.Time;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User 
{

    private int id;

    private String username;

    private String password;

    private UserProfile userProfile;

    private List<Role> userRoles;

    private Time lastCheckInTime;
}

package com.vincent.inc.VGame.model.authenticator;

import java.util.List;

import com.vincent.inc.viesspringutils.model.User.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Route 
{
    private int id;
    
    private String path;

    private String method;

    private boolean secure = false;

    private List<Role> roles;
}

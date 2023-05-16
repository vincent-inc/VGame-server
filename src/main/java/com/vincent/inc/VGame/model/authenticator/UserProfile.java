package com.vincent.inc.VGame.model.authenticator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile 
{
    private int id;

    private String email;

    private String alias;
}

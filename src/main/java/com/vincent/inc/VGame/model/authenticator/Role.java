package com.vincent.inc.VGame.model.authenticator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role 
{

    private int id;

    private String name;

    private int level;
}

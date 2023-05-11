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

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private String email;

    private String address;

    private String city;

    private String state;

    private String zip;

    private String alias;
}

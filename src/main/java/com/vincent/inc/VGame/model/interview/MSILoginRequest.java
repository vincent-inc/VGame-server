package com.vincent.inc.VGame.model.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSILoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;
}

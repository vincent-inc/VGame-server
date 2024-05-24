package com.vincent.inc.VGame.model.interview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MSILoginResponse {
    private boolean success;
    private User user;
    private String token;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private String legalName;
        private String gender;
        private String trainingBatch;
        private String group;
        private String username;
        private boolean isActivated;
    }
}



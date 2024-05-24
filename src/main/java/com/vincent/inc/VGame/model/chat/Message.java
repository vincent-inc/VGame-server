package com.vincent.inc.VGame.model.chat;

import com.vincent.inc.viesspringutils.util.DateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String message;
    private DateTime time;
    private String sendBy;
}

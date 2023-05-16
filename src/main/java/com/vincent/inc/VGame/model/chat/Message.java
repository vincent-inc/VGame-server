package com.vincent.inc.VGame.model.chat;

import com.vincent.inc.VGame.util.Time;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private String message;
    private Time time;
    private String sendBy;
}

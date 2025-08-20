package org.Simulator.AI;

import org.Simulator.AI.chatbot;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class simulation {
    private String AI_KEY;
    private chatbot AI_1;
    private chatbot AI_2;

    public simulation(String KEY) {
        this.AI_KEY = KEY;
    }

    public Map<String, String> run(String[] Secnario, int length)  {

        this.AI_1 = new chatbot(this.AI_KEY, Secnario[0]);
        this.AI_2 = new chatbot(this.AI_KEY, Secnario[1]);

        Map<String, String>Conversation = new HashMap<>();

        String BOT_1 = "Hello what is your name!";
        String BOT_2 = "Hello what is your name!";

        for (int i = 0; i < length; i++) {

            BOT_1 = this.AI_1.chat(BOT_2);
            BOT_2 = this.AI_1.chat(BOT_1);

            Conversation.put("BOT_1 TURN " + i + ":", BOT_1);
            Conversation.put("BOT_2 TURN " + i + ":", BOT_2);

        }

        return Conversation;
    }
}

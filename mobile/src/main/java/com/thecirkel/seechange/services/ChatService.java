package com.thecirkel.seechange.services;

import com.thecirkel.seechangemodels.models.ChatMessage;
import android.os.Handler;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ChatService extends Observable {

    private static ChatService instance = null;
    private List<ChatMessage> messages = new ArrayList<>();

    private boolean firstSend = false;
    private boolean lastSend = false;

    protected ChatService() {
        this.messages.add(new ChatMessage("Deze stream is echt sick!", "Bart in 't Veld"));
    }

    public static ChatService getInstance() {
        if (instance == null) {
            instance = new ChatService();
        }
        return instance;
    }

    public void start() {

        if (!firstSend) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    messages.add(new ChatMessage(":FeelsGoodMan:", "Niels van Dam"));
                    setChanged();
                    notifyObservers();
                }
            };

            handler.postDelayed(r, 2000);
            this.firstSend = true;
        }


        if (!lastSend) {
            Handler handler2 = new Handler();
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    messages.add(new ChatMessage("Love it! <3", "Marc den Uil"));
                    setChanged();
                    notifyObservers();
                }
            };

            handler2.postDelayed(r2, 4000);
            lastSend = true;
        }
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }
}

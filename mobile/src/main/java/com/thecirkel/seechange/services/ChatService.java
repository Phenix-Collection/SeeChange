package com.thecirkel.seechange.services;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.thecirkel.seechangemodels.models.ChatMessage;
import com.thecirkel.seechangemodels.models.Static;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ChatService extends Observable {
    private static ChatService instance = null;
    private List<ChatMessage> messages = new ArrayList<>();

    private boolean firstSend = false;
    private boolean lastSend = false;

    private DataClient dataClient;

    protected ChatService(Context context) {
        if (context != null) {
            dataClient = Wearable.getDataClient(context);
        } else {
            dataClient = null;
        }

        //addMessage(new ChatMessage("Mooie stream!", "Bart in 't Veld"));
    }

    public static ChatService getInstance(Context context) {
        if (instance == null) {
            instance = new ChatService(context);
        }
        return instance;
    }

    public void start() {

        if (!firstSend) {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    //addMessage(new ChatMessage(":FeelsGoodMan:", "Niels van Dam"));
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
                    //addMessage(new ChatMessage("Love it! <3", "Marc den Uil"));
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

    private void addMessage(ChatMessage chatMessage) {
        this.messages.add(chatMessage);

        try {
            if (dataClient != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(chatMessage);

                byte[] bytes = byteArrayOutputStream.toByteArray();

                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(Static.CHATMAP);
                putDataMapRequest.getDataMap().putByteArray(Static.CHATKEY, bytes);

                PutDataRequest putDataRequest = putDataMapRequest.asPutDataRequest();
                putDataRequest.setUrgent();
                dataClient.putDataItem(putDataRequest);
            }
        } catch (Exception e) {

        }
    }
}

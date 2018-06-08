package com.thecirkel.seechange.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.thecirkel.seechange.R;
import com.thecirkel.seechange.adapters.ChatArrayAdapter;
import com.thecirkel.seechange.services.ChatService;
import com.thecirkel.seechangemodels.models.ChatMessage;

import java.util.*;

public class ChatActivity extends AppCompatActivity implements Observer{

    private ListView chatListView;

    private List<ChatMessage> chatList;
    private ChatArrayAdapter arrayAdapter;

    private ChatService chatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatService = ChatService.getInstance(this);

        initMessages();
        initListView();

        chatService.addObserver(this);
        chatService.start();
    }

    @Override
    public void update(Observable o, Object arg) {
        arrayAdapter.notifyDataSetChanged();
    }

    private void initMessages() {
        chatList = this.chatService.getMessages();
    }

    private void initListView() {
        chatListView = findViewById(R.id.ChatList);
        arrayAdapter = new ChatArrayAdapter(this, chatList);
        chatListView.setAdapter(arrayAdapter);
    }
}

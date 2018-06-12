package com.thecirkel.seechange.activities;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.thecirkel.seechange.R;
import com.thecirkel.seechange.adapters.ChatArrayAdapter;
import com.thecirkel.seechange.services.ChatService;
import com.thecirkel.seechangemodels.models.ChatMessage;

import java.util.*;

public class ChatActivity extends Fragment implements Observer{

    private ListView chatListView;

    private List<ChatMessage> chatList;
    private ChatArrayAdapter arrayAdapter;

    private ChatService chatService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatService = ChatService.getInstance(getContext());
        chatService.addObserver(this);
        chatService.start();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMessages();
        initListView();
    }

    @Override
    public void update(Observable o, Object arg) {
        arrayAdapter.notifyDataSetChanged();
    }

    private void initMessages() {
        chatList = this.chatService.getMessages();
    }

    private void initListView() {
        chatListView = getView().findViewById(R.id.ChatList);
        arrayAdapter = new ChatArrayAdapter(getContext(), chatList);
        chatListView.setAdapter(arrayAdapter);
    }
}

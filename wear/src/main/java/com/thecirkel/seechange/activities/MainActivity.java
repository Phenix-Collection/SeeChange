package com.thecirkel.seechange.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.wearable.activity.WearableActivity;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.thecirkel.seechange.R;
import com.thecirkel.seechange.adapters.ChatArrayAdapter;
import com.thecirkel.seechangemodels.models.ChatMessage;
import com.thecirkel.seechangemodels.models.Static;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends WearableActivity implements DataClient.OnDataChangedListener {

    private TextView username;
    private TextView message;

    private ListView chatListView;
    private List<ChatMessage> chatList;
    private ChatArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.chatUser);
        message = findViewById(R.id.chatMessage);
        chatList = new ArrayList<>();

        initListView();
        initFakeMessages();

        // Enables Always-on
        setAmbientEnabled();
    }

    private void initFakeMessages() {
        chatList.add(new ChatMessage("Zieke stream!" , "Bart in 't Veld"));
        chatList.add(new ChatMessage("Ja sws neef broer kan dit nog leuker!", "Rick Voermans"));
        chatList.add(new ChatMessage("Wow zo vet!", "Felix Boons"));
        chatListView.setSelection(arrayAdapter.getCount() - 1);
    }

    private void initListView() {
        chatListView = findViewById(R.id.chatList);
        arrayAdapter = new ChatArrayAdapter(this, chatList);
        chatListView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getDataClient(this).removeListener(this);
    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(Static.CHATMAP) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();

                    ChatMessage chatMessage = getChatMessageFromByteArray(dataMap.getByteArray(Static.CHATKEY));
                    updateMessageUI(chatMessage);
                }
            }
        }
    }

    private ChatMessage getChatMessageFromByteArray(byte[] bytes) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            ObjectInputStream is = new ObjectInputStream(in);
            return (ChatMessage) is.readObject();
        }
        catch (Exception e) {
            return null;
        }
    }

    private void updateMessageUI(ChatMessage chatMessage) {
        username.setText(chatMessage.getUsername());
        message.setText(chatMessage.getMessage());
    }
}

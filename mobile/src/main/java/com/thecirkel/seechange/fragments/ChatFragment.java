package com.thecirkel.seechange.fragments;

import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.thecirkel.seechange.R;
import com.thecirkel.seechange.adapters.ChatArrayAdapter;
import com.thecirkel.seechange.services.ChatApplication;
import com.thecirkel.seechange.services.ChatService;
import com.thecirkel.seechangemodels.models.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private ListView chatListView;

    private ArrayList<ChatMessage> chatList;
    private ChatArrayAdapter arrayAdapter;

    private ImageButton sendMessageBtn;
    private EditText messageText;
    private TextView followerCount;

    private ChatApplication chatApplication;
    private Socket mSocket;

    private Boolean isConnected = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            chatList = new ArrayList<>();

            chatApplication = new ChatApplication() ;
            mSocket = chatApplication.getSocket();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("chat_message", onNewMessage);
            mSocket.on("update_followers", onUpdateFollowers);

            mSocket.connect();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        messageText = view.findViewById(R.id.chatText);
        followerCount = view.findViewById(R.id.followerCount);

        sendMessageBtn = view.findViewById(R.id.sendMessageButton);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListView();
    }

    private void initListView() {
        chatListView = getView().findViewById(R.id.ChatList);
        arrayAdapter = new ChatArrayAdapter(getContext(), chatList);
        chatListView.setAdapter(arrayAdapter);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        Log.i(TAG, "connected");

                        JSONObject data = new JSONObject();
                        try {
                            data.put("room", "room 1");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        mSocket.emit("join_room", data);
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "Error connecting");
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "disconnected");
                    isConnected = false;
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message, username;
                    try {
                        message = data.getString("message");
                        username = data.getString("username");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    addMessage(message, username);
                }
            });
        }
    };

    private Emitter.Listener onUpdateFollowers = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Integer followers;
                    try {
                        followers = data.getInt("followers");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    updateFollowers(followers);
                }
            });
        }
    };

    private void addMessage(String message, String username) {
        ChatMessage messagetext = new ChatMessage(message, username);

        chatList.add(messagetext);
        arrayAdapter.notifyDataSetChanged();
    }

    private void updateFollowers(int followers) {
        followerCount.setText(Integer.toString(followers));
    }

    private void attemptSend() {
        if (!mSocket.connected()) return;

        String message = messageText.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            messageText.requestFocus();
            return;
        }

        Log.i(TAG, message);

        messageText.setText("");
        // perform the sending message attempt.

        JSONObject data = new JSONObject();
        try {
            data.put("message", message);
            data.put("username", "kayvon");
            data.put("room", "room 1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit("chat_message", data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
    }
}

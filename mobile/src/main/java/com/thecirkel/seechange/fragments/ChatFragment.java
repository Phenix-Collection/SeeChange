package com.thecirkel.seechange.fragments;

import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.thecirkel.seechange.R;
import com.thecirkel.seechange.adapters.ChatArrayAdapter;
import com.thecirkel.seechange.services.CertificateService;
import com.thecirkel.seechange.services.ChatApplication;
import com.thecirkel.seechangemodels.models.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private ArrayList<ChatMessage> chatList = new ArrayList<>();
    private ChatArrayAdapter arrayAdapter;
    private ChatApplication chatApplication;
    private CertificateService certificateService;

    private ImageButton sendMessageBtn;
    private EditText messageText;
    private TextView followerCount;
    private ListView chatListView;

    private Socket mSocket;

    private Boolean isConnected = false;

    private String streamerName = "";
    private String streamkey = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            chatApplication = new ChatApplication();
            certificateService = new CertificateService();
            streamerName = certificateService.getStreamerName();
            streamkey = certificateService.getStreamkey();

            mSocket = chatApplication.getSocket();
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on("chat_message", onNewMessage);
            mSocket.on("update_followers", onUpdateFollowers);

            mSocket.connect();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        arrayAdapter = new ChatArrayAdapter(getContext(), this.chatList);
        chatListView.setAdapter(arrayAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_chat, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        messageText = view.findViewById(R.id.chatText);
        followerCount = view.findViewById(R.id.followerCount);
        chatListView = view.findViewById(R.id.ChatList);

        chatListView.setStackFromBottom(true);

        sendMessageBtn = view.findViewById(R.id.sendMessageButton);
        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
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
                            data.put("room", streamkey);
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
                    Toast.makeText(getActivity().getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_LONG).show();
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
                    String message, username, timestamp;
                    try {
                        message = data.getString("message").trim();
                        username = data.getString("username");
                        timestamp = data.getString("timestamp");
                    } catch (JSONException e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }

                    addMessage(message, username, timestamp);
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

    private void addMessage(String message, String username, String timestamp) {
        ChatMessage chatMessage = new ChatMessage(message, username, timestamp);

        chatList.add(chatMessage);

        arrayAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                chatListView.smoothScrollToPosition(arrayAdapter.getCount() - 1);
            }
        });
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
            data.put("username", streamerName);
            data.put("room",  streamkey);
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
        mSocket.off(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("chat_message", onNewMessage);
        mSocket.off("update_followers", onUpdateFollowers);
    }
}

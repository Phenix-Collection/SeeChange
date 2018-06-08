package com.thecirkel.seechange.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.thecirkel.seechange.R;
import com.thecirkel.seechangemodels.models.ChatMessage;

import java.util.List;

public class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {
    private final List<ChatMessage> messages;
    private final Context context;

    public ChatArrayAdapter(Context context, List<ChatMessage> messages) {
        super(context, R.layout.chatitem, messages);

        this.messages = messages;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.chatitem, parent, false);

            TextView username = rowView.findViewById(R.id.chatUser);
            TextView message = rowView.findViewById(R.id.chatMessage);

            username.setText(messages.get(position).getUsername());
            message.setText(messages.get(position).getMessage());
        }

        return rowView;
    }
}

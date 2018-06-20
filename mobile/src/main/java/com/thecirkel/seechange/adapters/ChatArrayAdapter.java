package com.thecirkel.seechange.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.thecirkel.seechange.R;
import com.thecirkel.seechangemodels.models.ChatMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ChatArrayAdapter extends BaseAdapter {
    private Context context;
    private List<ChatMessage> messages;

    public ChatArrayAdapter(Context context, List<ChatMessage> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int i) {
        return messages.get(i);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(R.layout.chatitem, null);

            viewHolder = new ViewHolder();
            viewHolder.username = view.findViewById(R.id.chatUser);
            viewHolder.timestamp = view.findViewById(R.id.chatTimestamp);
            viewHolder.message = view.findViewById(R.id.chatMessage);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.username.setText(messages.get(position).getUsername());
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("H:mm:ss a");

        String timestamp = messages.get(position).getTimestamp();
        Date inputDate = null;

        try {
            inputDate = inputFormat.parse(timestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String outputDate = outputFormat.format(inputDate);

        viewHolder.timestamp.setText(outputDate);
        viewHolder.message.setText(messages.get(position).getMessage());

        return view;
    }

    private static class ViewHolder {
        TextView username;
        TextView timestamp;
        TextView message;
    }
}

package com.thecirkel.seechange;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<ChatMessage> messages;

    public ChatMessageAdapter(Context context, LayoutInflater layoutInflater, List<ChatMessage> messages) {
        this.context = context;
        this.layoutInflater = layoutInflater;
        this.messages = (ArrayList<ChatMessage>) messages;
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

        if(view == null) {
            view = layoutInflater.inflate(R.layout.message_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.message = view.findViewById(R.id.listItemText);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        ChatMessage message = messages.get(position);

        viewHolder.message.setText(message.getMessage());

        return view;
    }

    private static class ViewHolder {
        TextView message;
    }
}

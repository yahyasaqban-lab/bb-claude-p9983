package com.bbclaude.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends BaseAdapter {
    private final List<Message> items;
    private final LayoutInflater inflater;

    public MessageAdapter(Context context, List<Message> items) {
        this.items = items;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_message, parent, false);
        }

        Message msg = items.get(position);
        TextView roleView = (TextView) view.findViewById(R.id.text_role);
        TextView contentView = (TextView) view.findViewById(R.id.text_content);

        roleView.setText(msg.role.toUpperCase());
        contentView.setText(msg.content);

        if ("user".equals(msg.role)) {
            contentView.setBackgroundResource(R.drawable.bubble_user);
        } else {
            contentView.setBackgroundResource(R.drawable.bubble_assistant);
        }

        return view;
    }

    public void add(Message msg) {
        items.add(msg);
        notifyDataSetChanged();
    }

    public void setAll(List<Message> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }
}

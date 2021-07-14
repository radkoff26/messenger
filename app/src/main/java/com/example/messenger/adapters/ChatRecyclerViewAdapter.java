package com.example.messenger.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.models.Chat;
import com.example.messenger.models.DateConverter;
import com.example.messenger.models.UserLoggedIn;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TIME_PATTERN = "h:mm a";

    // TODO: 14.07.2021 Check removed chats and don't forget to attach user to chat
    private List<Chat> chats;
    private Context context;
    private LayoutInflater inflater;

    private class ChatViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout chatLayout;
        final ImageView avatar, status;
        final TextView userName, time, lastMessage;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.chatLayout);
            avatar = itemView.findViewById(R.id.avatar);
            status = itemView.findViewById(R.id.status);
            userName = itemView.findViewById(R.id.userName);
            time = itemView.findViewById(R.id.time);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }

    public ChatRecyclerViewAdapter(List<Chat> chats, Context context) {
        this.chats = chats;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(inflater.inflate(R.layout.chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        ChatViewHolder viewHolder = (ChatViewHolder) holder;
        if (!UserLoggedIn.getUser(context).getId().equals(chat.getLastMessage().getAuthorId())) {
            viewHolder.status.setImageResource(R.drawable.ic_baseline_circle_24);
        } else {
            if (chat.getLastMessage().getIsWatched()) {
                viewHolder.status.setImageResource(R.drawable.checked);
            } else {
                viewHolder.status.setImageResource(R.drawable.sent);
            }
        }
        viewHolder.userName.setText(chat.getChatterLocalNickname());
        viewHolder.lastMessage.setText(chat.getLastMessage().getText());
        String time = "-";
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
        try {
            time = sdf.format(DateConverter.getTime(
                    chat.getLastMessage().getTime()
            ));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    private String truncate(String s) {
        if (s.length() <= 37) {

        }
        return null;
    }
}

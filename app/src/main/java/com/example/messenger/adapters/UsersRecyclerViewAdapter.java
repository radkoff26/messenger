package com.example.messenger.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.fragments.ChatFragment;
import com.example.messenger.fragments.ChatsFragment;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.example.messenger.models.Constants.BUNDLE_CHAT_ID;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<User> users;
    private Context context;
    private LayoutInflater inflater;
    private WeakReference<MainActivity> reference;

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        final LinearLayout layout;
        final ImageView avatar;
        final TextView nickname, login;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.avatar);
            nickname = itemView.findViewById(R.id.nickname);
            login = itemView.findViewById(R.id.loginWithAt);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    public UsersRecyclerViewAdapter(List<User> users, Context context, MainActivity activity) {
        this.users = users;
        this.context = context;
        this.reference = new WeakReference<>(activity);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(inflater.inflate(R.layout.user_item, parent, false));
    }

    // TODO: 16.07.2021 fix self-call
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        User user = users.get(position);
        UsersViewHolder viewHolder = (UsersViewHolder) holder;
        viewHolder.login.setText("@" + user.getLogin());
        viewHolder.nickname.setText(user.getNickname());
        viewHolder.layout.setOnClickListener(v -> {
            String chat_id = UserLoggedIn.getUser(context).getId() + "_" + user.getId();
            ChatFragment fragment = new ChatFragment();
            Bundle arguments = new Bundle();
            arguments.putString(BUNDLE_CHAT_ID, chat_id);
            fragment.setArguments(arguments);
            reference.get().getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

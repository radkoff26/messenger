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
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.BUNDLE_CHAT_ID;

public class UsersRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Variables
    private final List<User> users;
    private final Context context;
    private final LayoutInflater inflater;
    private final WeakReference<MainActivity> reference;

    // ViewHolder
    public static final class UsersViewHolder extends RecyclerView.ViewHolder {

        // Views
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // User object
        User user = users.get(position);

        // ViewHolder object
        UsersViewHolder viewHolder = (UsersViewHolder) holder;

        // Setting data into the views
        viewHolder.login.setText(String.format("%s%s", "@", user.getLogin()));
        viewHolder.nickname.setText(user.getNickname());

        // Setting OnClickListener
        viewHolder.layout.setOnClickListener(v -> {
            User currentUser = UserLoggedIn.getUser(context);
            if (currentUser != null) {
                String chat_id = currentUser.getId() + "_" + user.getId();

                ChatFragment fragment = new ChatFragment();
                Bundle arguments = new Bundle();
                arguments.putString(BUNDLE_CHAT_ID, chat_id);
                fragment.setArguments(arguments);

                reference.get().getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment, fragment)
                        .commit();
            }
        });

        // Setting up avatar of user
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("null")) {
            Picasso.with(context)
                    .load(BASE_URL + "/getAvatar?url=" + user.getAvatarUrl())
                    .placeholder(R.drawable.user_round)
                    .into(viewHolder.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

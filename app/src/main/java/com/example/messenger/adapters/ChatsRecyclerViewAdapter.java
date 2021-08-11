package com.example.messenger.adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.fragments.ChatFragment;
import com.example.messenger.models.Chat;
import com.example.messenger.models.DateConverter;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.BUNDLE_CHAT_ID;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class ChatsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Time pattern
    public static final String TIME_PATTERN = "h:mm a";

    // Variables
    private final List<Chat> chats;
    private final Context context;
    private final LayoutInflater inflater;
    private final WeakReference<MainActivity> reference;

    // ViewHolder for RecyclerView
    private static final class ChatsViewHolder extends RecyclerView.ViewHolder {

        // Views
        final LinearLayout chatLayout;
        final ImageView avatar, status, userStatus;
        final TextView userName, time, lastMessage;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);
            chatLayout = itemView.findViewById(R.id.chatLayout);
            avatar = itemView.findViewById(R.id.avatar);
            status = itemView.findViewById(R.id.status);
            userName = itemView.findViewById(R.id.userName);
            time = itemView.findViewById(R.id.time);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            userStatus = itemView.findViewById(R.id.user_status);
        }
    }

    public ChatsRecyclerViewAdapter(List<Chat> chats, Context context, MainActivity activity) {
        this.chats = chats;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.reference = new WeakReference<>(activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatsViewHolder(inflater.inflate(R.layout.chat_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Chat object to receive necessary information
        Chat chat = chats.get(position);

        // Holder
        ChatsViewHolder viewHolder = (ChatsViewHolder) holder;

        // User's chatter id
        int chatterId = 0;

        // Getting user object
        User mUser = UserLoggedIn.getUser(context);

        // Setting up all data into view
        if (mUser != null && !mUser.getId().equals(chat.getLastMessage().getAuthorId())) {
            chatterId = chat.getLastMessage().getAuthorId();
            if (chat.getNotChecked() > 0) {
                viewHolder.status.setVisibility(View.VISIBLE);
                viewHolder.status.setImageResource(R.drawable.ic_baseline_circle_24);
            } else {
                viewHolder.status.setVisibility(View.GONE);
            }
        } else {
            if (mUser != null) {
                chatterId = chat.getChatter1().equals(mUser.getId()) ? chat.getChatter2() : chat.getChatter1();
                if (chat.getLastMessage().getIsWatched()) {
                    viewHolder.status.setImageResource(R.drawable.checked);
                } else {
                    viewHolder.status.setImageResource(R.drawable.sent);
                }
            }
        }

        // Rest
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        ClientAPI clientAPI = retrofit.create(ClientAPI.class);

        // Check if chatter is online
        clientAPI.isUserOnline(TOKEN_VALUE, chatterId)
                .enqueue(new Callback<Boolean>() {
                    @Override
                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                        Boolean f = response.body();
                        if (f != null && f) {
                            viewHolder.userStatus.setVisibility(View.VISIBLE);
                            return;
                        }
                        viewHolder.userStatus.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Call<Boolean> call, Throwable t) {

                    }
                });

        // Setting up data
        viewHolder.userName.setText(truncate(chat.getChatterLocalNickname()));
        viewHolder.lastMessage.setText(truncate(chat.getLastMessage().getText()));

        // Setting up time
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

        // Setting up OnClickListener to transact to ChatFragment
        viewHolder.chatLayout.setOnClickListener(v -> {
            ChatFragment fragment = new ChatFragment();
            Bundle arguments = new Bundle();
            arguments.putString(BUNDLE_CHAT_ID, chat.getId());
            fragment.setArguments(arguments);
            reference.get().getSupportFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .add(R.id.fragment, fragment)
                    .commit();
        });

        // Getting chatter's image
        clientAPI.getUser(TOKEN_VALUE, chatterId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();

                        if (user != null) {
                            if (user.getAvatarUrl() != null && !user.getAvatarUrl().equals("null")) {
                                Picasso.with(context)
                                        .load(BASE_URL + "/getAvatar?url=" + user.getAvatarUrl())
                                        .placeholder(R.drawable.user_round)
                                        .into(viewHolder.avatar);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    // Method to truncate string with the ending of '...' to 30 symbols
    private String truncate(String s) {
        if (s.length() <= 30) {
            return s;
        }
        return s.substring(0, 27) + "...";
    }
}

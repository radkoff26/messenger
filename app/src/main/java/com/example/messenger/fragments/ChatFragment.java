package com.example.messenger.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.messenger.MainActivity;
import com.example.messenger.R;
import com.example.messenger.adapters.ChatRecyclerViewAdapter;
import com.example.messenger.models.Message;
import com.example.messenger.models.MessageSending;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.BUNDLE_CHAT_ID;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class ChatFragment extends Fragment {

    private ChatRecyclerViewAdapter adapter;
    private RecyclerView chat;
    private Retrofit retrofit;
    private ClientAPI clientAPI;
    private String chat_id;
    private EditText textOfMessage;
    private ImageButton sendMessage;
    private int chatterId, userId;
    private Runnable updateMessages;
    private Handler handler;
    private ItemTouchHelper itemTouchHelper;
    private List<Message> messages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Bundle arguments = getArguments();

        chat_id = arguments.getString(BUNDLE_CHAT_ID);

        chat = view.findViewById(R.id.chat);
        textOfMessage = view.findViewById(R.id.textOfMessage);
        sendMessage = view.findViewById(R.id.sendMessage);

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();
        clientAPI = retrofit.create(ClientAPI.class);

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Message message = messages.get(viewHolder.getAdapterPosition());
                if (message.getAuthorId().equals(UserLoggedIn.getUser(getContext()).getId())) {
                    new MaterialAlertDialogBuilder(getContext())
                            .setTitle("Remove message")
                            .setMessage("Remove this message for everyone?")
                            .setNegativeButton("No", (dialog1, which) -> {
                                dialog1.cancel();
                            })
                            .setPositiveButton("Yes", (dialog12, which) -> {
                                String body = chat_id + " ";
                                body += message.getId();
                                clientAPI.removeMessage(TOKEN_VALUE, body)
                                        .enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                updateChat();
                                                adjustAdapter(messages);
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                                            }
                                        });
                                dialog12.cancel();
                            })
                            .show();
                } else {
                    adjustAdapter(messages);
                }
            }

        });

        int var1 = Integer.parseInt(chat_id.split("_")[0]);
        int var2 = Integer.parseInt(chat_id.split("_")[1]);

        userId = UserLoggedIn.getUser(getContext()).getId();

        chatterId = var1 == userId ? var2 : var1;

        List<Fragment> fragments = getActivity().getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments) {
            if (fragment.getClass() == ChatsFragment.class) {
                ((ChatsFragment) fragment).updateData();
            } else if (fragment.getClass() == MainFragment.class) {
                ((MainFragment) fragment).updateData();
            }
        }

        handler = new Handler();

        clientAPI.getUser(TOKEN_VALUE, chatterId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        ((MainActivity) getActivity()).setChatActionBar(response.body());
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });

        updateMessages = () -> {
            updateChat();
            handler.postDelayed(updateMessages, 3000);
        };

        sendMessage.setOnClickListener(v -> {
            String text = textOfMessage.getText().toString();
            if (!text.isEmpty()) {
                clientAPI.sendMessage(TOKEN_VALUE, new MessageSending(userId, chatterId, text))
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                textOfMessage.setText("");
                                updateChat();
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        handler.post(updateMessages);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateMessages);
    }

    public void updateChat() {
        clientAPI.getUser(TOKEN_VALUE, chatterId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if (user != null) {
                            ((MainActivity) getActivity()).setChatActionBar(user);
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
        clientAPI.getMessages(TOKEN_VALUE, chat_id)
                .enqueue(new Callback<List<Message>>() {
                    @Override
                    public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                        messages = response.body();
                        if (messages != null) {
                            adjustAdapter(messages);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Message>> call, Throwable t) {

                    }
                });
    }

    public void adjustAdapter(List<Message> messages) {
        itemTouchHelper.attachToRecyclerView(chat);
        chat.setAdapter(new ChatRecyclerViewAdapter(messages, getContext(), chat_id));
    }
}
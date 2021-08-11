package com.example.messenger.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messenger.R;
import com.example.messenger.models.DateConverter;
import com.example.messenger.models.Message;
import com.example.messenger.models.User;
import com.example.messenger.models.UserLoggedIn;
import com.example.messenger.rest.ClientAPI;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.messenger.models.Constants.BASE_URL;
import static com.example.messenger.models.Constants.TOKEN_VALUE;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Time pattern to display time
    public static final String TIME_PATTERN = "h:mm a";

    // Variables
    private final List<Message> messages;
    private final Context context;
    private final LayoutInflater inflater;
    private final String chat_id;

    // ViewHolder for RecyclerView
    public static final class ChatViewHolder extends RecyclerView.ViewHolder {

        // Views
        final LinearLayout main, secondary;
        final TextView messageText, time;
        final ImageView status;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.main_layout);
            secondary = itemView.findViewById(R.id.secondary_layout);
            messageText = itemView.findViewById(R.id.message_text);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
        }
    }

    public ChatRecyclerViewAdapter(List<Message> messages, Context context, String chat_id) {
        this.messages = messages;
        this.context = context;
        this.chat_id = chat_id;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(inflater.inflate(R.layout.message_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Message object to receive necessary information
        Message message = messages.get(position);

        // Getting current user
        User current = UserLoggedIn.getUser(context);

        // Initializing holder
        ChatViewHolder viewHolder = (ChatViewHolder) holder;

        // Checking if the last message was written by current user
        if (current != null && !current.getId().equals(message.getAuthorId())) {
            viewHolder.main.setGravity(Gravity.START);
            viewHolder.secondary.setGravity(Gravity.START);
            viewHolder.messageText.setBackgroundResource(R.drawable.chatter_message);
            viewHolder.messageText.setTextColor(Color.WHITE);
            viewHolder.status.setVisibility(View.GONE);
            if (!message.getIsWatched()) {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(BASE_URL)
                        .build();
                ClientAPI clientAPI = retrofit.create(ClientAPI.class);
                clientAPI.watchMessage(TOKEN_VALUE, chat_id + " " + message.getId())
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {

                            }
                        });
            }
        } else {
            if (message.getIsWatched()) {
                viewHolder.status.setImageResource(R.drawable.checked);
            }
        }

        // Setting up time to the message
        viewHolder.messageText.setText(message.getText());
        String time = "-";
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTERN);
        try {
            time = sdf.format(DateConverter.getTime(
                    message.getTime()
            ));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

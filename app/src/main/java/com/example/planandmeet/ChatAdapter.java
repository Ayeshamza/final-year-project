package com.example.planandmeet;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<ChatModel> chatModelList;

    int SENDER_VIEW = 1;
    int RECEIVER_VIEW = 0;

    String userName, userId;
    DatabaseReference userNameReference;

    public ChatAdapter(Context context, ArrayList<ChatModel> chatModelList) {
        this.context = context;
        this.chatModelList = chatModelList;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ChatModel chatModel = chatModelList.get(position);

        if (holder.getClass() == senderViewHolder.class) {
            ((senderViewHolder) holder).senderMessage.setText(chatModel.getMessage());
            ((senderViewHolder) holder).senderTime.setText(getDate(chatModel.getTimestamp().toString()));
        } else {
            ((receiverViewHolder) holder).receiverMessage.setText(chatModel.getMessage());
            ((receiverViewHolder) holder).receiverTime.setText(getDate(chatModel.getTimestamp().toString()));

            //for getting sender name
            userId = chatModel.getSenderID();
            userNameReference = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("name");

            userNameReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    userName = (String) snapshot.getValue();

                    //setting sender name
                    ((receiverViewHolder) holder).senderName.setText(userName);
                }

                @Override
                public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError error) {
                }
            });
        }
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW) {
            View view = LayoutInflater.from(context).inflate(R.layout.msg_sender_item, parent, false);
            return new senderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.msg_receiver_item, parent, false);
            return new receiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {

        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (chatModelList.get(position).getSenderID().equals(currentUser)) {
            return SENDER_VIEW;
        } else {
            return RECEIVER_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    //for receiver message
    public static class receiverViewHolder extends RecyclerView.ViewHolder {

        TextView senderName, receiverMessage, receiverTime;

        public receiverViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            receiverMessage = itemView.findViewById(R.id.receiverText);
            receiverTime = itemView.findViewById(R.id.receiverTime);
            senderName = itemView.findViewById(R.id.msgSenderName);
        }
    }

    //for sender message
    public static class senderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessage, senderTime;

        public senderViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            senderMessage = itemView.findViewById(R.id.senderText);
            senderTime = itemView.findViewById(R.id.senderTime);
        }
    }

    private String getDate(String time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));
        String myFormat = "dd/MM/yy hh:mm a";
        return DateFormat.format(myFormat, calendar).toString();
    }
}
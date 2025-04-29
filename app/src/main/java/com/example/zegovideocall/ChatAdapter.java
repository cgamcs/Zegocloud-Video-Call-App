package com.example.zegovideocall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList;
    private String currentUserId;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
        this.currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message.getSenderId().equals(currentUserId)) {
            holder.messageTextViewSent.setText(message.getMessage());
            holder.messageTextViewSent.setVisibility(View.VISIBLE);
            holder.messageTextView.setVisibility(View.GONE);
        } else {
            holder.messageTextView.setText(message.getMessage());
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.messageTextViewSent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView messageTextViewSent;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            messageTextViewSent = itemView.findViewById(R.id.messageTextViewSent);
        }
    }
}
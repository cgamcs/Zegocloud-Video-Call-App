package com.example.zegovideocall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private Button sendButton;
    private ChatAdapter chatAdapter;
    private List<Message> messageList;
    private String receiverId;
    private String receiverName;
    private FirebaseUser currentUser;
    private DatabaseReference messagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        receiverId = getIntent().getStringExtra("receiverId");
        receiverName = getIntent().getStringExtra("receiverName");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        chatRecyclerView.setAdapter(chatAdapter);

        String chatId = generateChatId(currentUser.getUid(), receiverId);
        messagesRef = FirebaseDatabase.getInstance().getReference("messages").child(chatId);

        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = messageInput.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    Message message = new Message(currentUser.getUid(), receiverId, messageText, System.currentTimeMillis());
                    messagesRef.push().setValue(message);
                    messageInput.setText("");
                }
            }
        });
    }

    private String generateChatId(String senderId, String receiverId) {
        if (senderId.compareTo(receiverId) < 0) {
            return senderId + "_" + receiverId;
        } else {
            return receiverId + "_" + senderId;
        }
    }
}
package com.example.chatbot;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView rv;
    private ChatAdapter adapter;
    private List<ChatMessage> chatMessages;
    private String username;
    private ChatService chatService;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_chat_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize chat service and username
        chatService = RetrofitClient.getChatService();
        username = getIntent().getStringExtra("username");
        if(username == null || username.isEmpty()) { username = "no username"; }
        // Greet the user and setup UI components
        welcomeUser();
        setupRecyclerView();
        setupSendButton();
    }
    // Welcome the user with a message
    private void welcomeUser()
    {
        chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage(String.format("Welcome %s!", username), true, ""));
    }
    // Setup the RecyclerView for chat messages
    private void setupRecyclerView()
    {
        rv = findViewById(R.id.chat_rv);
        adapter = new ChatAdapter(chatMessages);
        rv.setAdapter(adapter);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setStackFromEnd(true);
        rv.setLayoutManager(lm);
    }
    // Setup the send button and its click listener
    private void setupSendButton()
    {
        ImageButton sendButton = findViewById(R.id.send_button);
        EditText chatEntry = findViewById(R.id.chat_entry);


        sendButton.setOnClickListener(v -> {
            String msg = chatEntry.getText().toString();
            if (!msg.isEmpty()) {
                handleUserMessage(msg, chatEntry);
            }
        });
    }
    // Handle user's message input
    private void handleUserMessage(String message, EditText chatEntry)
    {
        ChatMessage userChat = new ChatMessage(message, false, username);
        chatMessages.add(userChat);
        adapter.notifyItemInserted(chatMessages.size() - 1);
        chatEntry.setText("");
        rv.scrollToPosition(chatMessages.size() - 1);
        List<ChatHistory> chatHist = buildChatHistory();
        ChatRequest chatRequest = new ChatRequest(message, chatHist);
        sendMessageToChatBot(chatRequest);
    }
    // Build the chat history for the chatbot request
    private List<ChatHistory> buildChatHistory()
    {
        List <ChatHistory> chatHist = new ArrayList<>();
        for(int i = 0; i < chatMessages.size(); i += 2)
        {
            ChatMessage aiMessage = chatMessages.get(i);
            ChatMessage userMessage = chatMessages.get(i + 1);

            if (!userMessage.isSentByAI() && aiMessage.isSentByAI()) {
                chatHist.add(new ChatHistory(userMessage.getMessage(), aiMessage.getMessage()));
            } else {
                // Log an error or handle the case where the pair does not consist of a user message followed by an AI message
                Log.e("ChatHistory", "Unexpected message order: User message followed by another user message or AI message not followed by user message.");
            }
        }
        return chatHist;
    }
    // Send the message to the chatbot and handle the response
    private void sendMessageToChatBot(ChatRequest chatRequest)
    {
        chatService.sendMessage(chatRequest).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response)
            {
                if(response.isSuccessful() && response.body() != null)
                {
                    String aiResponse = response.body().getMessage();
                    ChatMessage aiChat = new ChatMessage(aiResponse, true, "AI");
                    chatMessages.add(aiChat);
                    adapter.notifyItemInserted(chatMessages.size() - 1);
                    rv.scrollToPosition(chatMessages.size() - 1);
                }
                else
                {
                    Log.e("Retrofit Call", "Response error: " + response.code() + " " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ChatResponse> call, Throwable t)
            {
                Log.e("Retrofit Call", "Network error: " + t.getMessage());
            }
        });
    }
    // Hide keyboard when user presses on a non-EditText area.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if(getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}

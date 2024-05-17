package com.example.chatbot;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> chatMessages;

    public static class ChatViewHolder extends RecyclerView.ViewHolder
    {
        public TextView messageText;
        public ImageView senderIcon;
        public RelativeLayout messageContainer;

        public ChatViewHolder(View itemView)
        {
            super(itemView);
            messageText = itemView.findViewById(R.id.message_text);
            senderIcon = itemView.findViewById(R.id.sender_icon);
            messageContainer = itemView.findViewById(R.id.message_container);
        }
    }

    public ChatAdapter(List<ChatMessage> messages)
    {
        this.chatMessages = messages;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }
    @Override
    public void onBindViewHolder(ChatViewHolder holder, int pos) {
        ChatMessage msg = chatMessages.get(pos);
        Context context = holder.itemView.getContext();

        holder.messageText.setText(msg.getMessage());
        configureMessageLayout(holder, msg, context);

    }

    private void configureMessageLayout(ChatViewHolder holder, ChatMessage msg, Context context)
    {
        int iconSize = 10;
        RelativeLayout.LayoutParams msgParams = (RelativeLayout.LayoutParams) holder.messageText.getLayoutParams();
        RelativeLayout.LayoutParams imgParams = (RelativeLayout.LayoutParams) holder.senderIcon.getLayoutParams();

        if (msg.isSentByAI()) {
            setAIMessageAppearance(holder, context, msgParams, imgParams, iconSize);
        } else {
            setUserMessageAppearance(holder, context, msg, msgParams, imgParams, iconSize);
        }

        holder.messageContainer.setLayoutParams(msgParams);
        holder.senderIcon.setLayoutParams(imgParams);
    }
    private void setAIMessageAppearance(ChatViewHolder holder, Context context, RelativeLayout.LayoutParams msgParams,
                                        RelativeLayout.LayoutParams imgParams, int iconSize) {
        holder.senderIcon.setImageDrawable(CustomIconDrawable.getCustomIconDrawable(context, Color.LTGRAY, "", iconSize, iconSize, false));
        alignStart(msgParams, imgParams);
    }

    private void setUserMessageAppearance(ChatViewHolder holder, Context context, ChatMessage msg,
                                          RelativeLayout.LayoutParams msgParams, RelativeLayout.LayoutParams imgParams, int iconSize) {
        holder.senderIcon.setImageDrawable(CustomIconDrawable.getCustomIconDrawable(context, Color.LTGRAY, String.valueOf(msg.getUserName().toUpperCase().charAt(0)), iconSize, iconSize, true));
        alignEnd(msgParams, imgParams);
    }
    // Align message and icon to the start (left)
    private void alignStart(RelativeLayout.LayoutParams msgParams, RelativeLayout.LayoutParams imgParams) {
        msgParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        msgParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
        imgParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        imgParams.removeRule(RelativeLayout.ALIGN_PARENT_END);
        imgParams.addRule(RelativeLayout.ALIGN_START, R.id.message_text);
    }
    // Align message and icon to the end (right)
    private void alignEnd(RelativeLayout.LayoutParams msgParams, RelativeLayout.LayoutParams imgParams) {
        msgParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        msgParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
        imgParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        imgParams.removeRule(RelativeLayout.ALIGN_PARENT_START);
        imgParams.addRule(RelativeLayout.ALIGN_END, R.id.message_text);
    }

    @Override
    public int getItemCount()
    {
        return chatMessages.size();
    }
}

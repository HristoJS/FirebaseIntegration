package com.evilcorp.firebaseintegration.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.helper.Time;
import com.evilcorp.firebaseintegration.helper.Util;
import com.evilcorp.firebaseintegration.ui.chat.ChatContract;
import com.evilcorp.firebaseintegration.view.RounderCornerImage;
import com.evilcorp.firebaseintegration.data.firebase.model.Message;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Chat Message Recycler Adapter
 */

public class ChatAdapter extends FirebaseRecyclerAdapter<Message,ChatAdapter.MessageViewHolder> {
    private final ChatContract.View chatView;
    private static final int ALIGN_START = 0;
    private static final int ALIGN_END = 1;
    private final List<UserAccount> chatParticipants;

    @SuppressWarnings("unchecked")
    public ChatAdapter(ChatContract.View view, Query ref, List<UserAccount> chatParticipants) {
        super(Message.class, R.layout.chat_message, MessageViewHolder.class, ref);
        this.chatView = view;
        this.chatParticipants = chatParticipants;
    }

    private void alignView(View view , int alignment){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, alignment);
        view.setLayoutParams(layoutParams);
    }

    private void alignView(View view , View targetView){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams.addRule(RelativeLayout.LEFT_OF, targetView.getId());
        view.setLayoutParams(layoutParams);
    }

    @Override
    protected void populateViewHolder(MessageViewHolder viewHolder, final Message message, int position) {
        //String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date(message.getTimestamp()));
        String date = Time.timeAgo(message.getTimestamp());
        viewHolder.chatDate.setText(date);
        //String id = message.getUserId();
        viewHolder.chatMessage.setText(message.getMessage());
        if (Util.equals(message.getUserId(), chatView.getUserId())) {
            viewHolder.chatMessage.setBackgroundResource(R.drawable.ic_chat_bubble_black_24dp_right);
            alignView(viewHolder.chatAvatar,ALIGN_END);
            alignView(viewHolder.chatMessage,viewHolder.chatAvatar);
        }
        else{
            viewHolder.chatMessage.setBackgroundResource(R.drawable.ic_chat_bubble_black_24dp);
            alignView(viewHolder.chatAvatar,ALIGN_START);
            alignView(viewHolder.chatMessage,ALIGN_START);
        }
        UserAccount account = getUser(message.getUserId());
        ImageView avatar = viewHolder.chatAvatar;
        assert account != null;
        Context context = chatView.getContext();
        int color = ContextCompat.getColor(context, R.color.colorShadow);
        RounderCornerImage image = new RounderCornerImage(context, avatar, color);
        Glide.with(chatView.getContext())
                .load(account.getAvatar())
                .asBitmap()
                .centerCrop()
                .into(image);
    }

    private UserAccount getUser(final String userId) {
        for(UserAccount userAccount : chatParticipants) {
            if (Util.equals(userAccount.getId(), userId)) {
                return userAccount;
            }
        }
        return null;
    }

    //Needs to be public static in order for adapter to work
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        final TextView chatDate;
        final AppCompatTextView chatMessage;
        final ImageView chatAvatar;
        final View mView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            chatDate = (TextView) itemView.findViewById(R.id.chatDate);
            chatMessage = (AppCompatTextView) itemView.findViewById(R.id.chatMessage);
            chatAvatar = (ImageView) itemView.findViewById(R.id.chatAvatar);
        }
    }
}
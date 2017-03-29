package com.evilcorp.firebaseintegration.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.view.RounderCornerImage;
import com.evilcorp.firebaseintegration.helper.Time;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;
import com.evilcorp.firebaseintegration.model.firebase.UserStatus;

import java.util.List;

/**
 * Created by hristo.stoyanov on 01-Feb-17.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder> {
    private List<UserAccount> users;
    private Context context;
    private FriendClickListener listener = null;

    public FriendListAdapter(Context context, List<UserAccount> users, FriendClickListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @Override
    public FriendListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_detail, parent, false);
        return new FriendListViewHolder(layoutView);
    }

    @Override
    public int getItemCount() {
        int user_size = users.size();
        boolean foreverAlone = (user_size == 0);
        listener.onNoFriends(foreverAlone);
        return user_size;
    }

    public void addItem(UserAccount user) {
        users.add(user);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(FriendListViewHolder viewHolder, int position) {
        final UserAccount userAccount = users.get(position);
        viewHolder.userName.setText(userAccount.getName());
        String avatar = userAccount.getAvatar();
        int userStatus = userAccount.getUserStatus();
        if (userStatus == UserStatus.OFFLINE || userStatus == UserStatus.INVISIBLE) {
            long time = userAccount.getLastOnline();
            //String lastOnline = time==0 ? "Never" : Time.milisSinceOnline(time)/1000/60+" min ago";
            String lastOnline = time == 0 ? "Never" : Time.timeAgo(time);

            viewHolder.lastOnline.setText("Last seen: " + lastOnline);
        } else {
            viewHolder.lastOnline.setText(UserStatus.getAll()[userStatus]);
        }
        if (avatar != null) {
            int color = ContextCompat.getColor(context, R.color.colorShadow);
            RounderCornerImage image = new RounderCornerImage(context, viewHolder.userAvatar, color);
            Glide.with(context)
                    .load(avatar)
                    .asBitmap()
                    .centerCrop()
                    .into(image);
        }
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFriendClick(userAccount.getId());
                //chatView.initChat(userAccount.getId());
            }
        });
    }

    public interface FriendClickListener {
        void onFriendClick(String targetUserId);

        void onNoFriends(boolean foreverAlone);
    }

    class FriendListViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userAvatar;
        TextView lastOnline;
        View mView;

        FriendListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            userAvatar = (ImageView) itemView.findViewById(R.id.friendAvatar);
            userName = (TextView) itemView.findViewById(R.id.friendName);
            lastOnline = (TextView) itemView.findViewById(R.id.friendLastOnline);
        }
    }


}

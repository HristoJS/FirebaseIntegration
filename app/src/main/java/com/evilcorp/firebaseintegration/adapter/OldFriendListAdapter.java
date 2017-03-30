package com.evilcorp.firebaseintegration.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.evilcorp.firebaseintegration.ChatterinoApp;
import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.data.firebase.model.UserAccount;
import com.google.firebase.database.Query;
/**
 * User List Adapter
 */

public class OldFriendListAdapter extends FirebaseRecyclerAdapter<UserAccount,OldFriendListAdapter.FriendListViewHolder> {
    private static final String TAG = OldFriendListAdapter.class.getSimpleName();
    private FriendClickListener listener = null;
    private Context context;

    @SuppressWarnings("unchecked")
    public OldFriendListAdapter(Context context, FriendClickListener listener, Query ref) {
        super(UserAccount.class, R.layout.friend_detail, FriendListViewHolder.class, ref);
        this.listener = listener;
        this.context = context;
    }

    @Override
    protected void populateViewHolder(FriendListViewHolder viewHolder, final UserAccount userAccount, int position) {
        UserAccount myAcc = ChatterinoApp.getCurrentAccount();
        assert myAcc != null;
        Log.d(TAG,myAcc.toString());
        if(!userAccount.equals(myAcc)) {
            viewHolder.userName.setText(userAccount.getName());
            String avatar = userAccount.getAvatar();
            if (avatar != null) {
                Glide.with(context)
                        .load(avatar).fitCenter()
                        .into(viewHolder.userAvatar);
            }
            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFriendClick(userAccount.getId());
                    //chatView.initChat(userAccount.getId());
                }
            });
        }
    }


    public interface FriendClickListener {
        void onFriendClick(String targetUserId);
    }

    // Has to be public static in order for the adapter to work
    public static class FriendListViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userAvatar;
        View mView;

        public FriendListViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            userAvatar = (ImageView) itemView.findViewById(R.id.friendAvatar);
            userName = (TextView) itemView.findViewById(R.id.friendName);
        }
    }
}

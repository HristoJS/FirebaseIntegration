package com.evilcorp.firebaseintegration.ui.friendlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.adapter.FriendListAdapter;
import com.evilcorp.firebaseintegration.ui.base.BaseFragment;
import com.evilcorp.firebaseintegration.ui.chat.ChatActivity;
import com.evilcorp.firebaseintegration.data.firebase.model.user.UserAccount;

import java.util.List;


public class FriendListFragment extends BaseFragment implements FriendListContract.View, FriendListAdapter.FriendClickListener {

    private FriendListContract.Presenter mPresenter;
    private FriendListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mNofriendsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FriendListPresenter(this, new FriendListInteractor());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.onCreate();
    }

    @Override
    public void setupRecyclerView(List<UserAccount> users) {
        View rootView = getView();
        if (rootView != null) {
            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.user_list);
            mNofriendsTextView = (TextView) rootView.findViewById(R.id.nofriends_textview);
            assert mRecyclerView != null;
            mAdapter = new FriendListAdapter(getContext(), users, this);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            mRecyclerView.setAdapter(mAdapter);
            final SwipeRefreshLayout refresh_layout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_layout);
            refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    mAdapter.notifyDataSetChanged();
                    refresh_layout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void notifyItemAdded(UserAccount user) {
        mAdapter.addItem(user);
    }


    @Override
    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadChat(String chatId, String userId) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_ID, chatId);
        intent.putExtra(ChatActivity.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void onFriendClick(String targetUserId) {
        mPresenter.initChat(targetUserId);
    }

    @Override
    public void onNoFriends(boolean foreverAlone) {
        mRecyclerView.setVisibility(foreverAlone ? View.INVISIBLE : View.VISIBLE);
        mNofriendsTextView.setVisibility(foreverAlone ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}

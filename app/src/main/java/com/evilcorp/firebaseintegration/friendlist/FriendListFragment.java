package com.evilcorp.firebaseintegration.friendlist;

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

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.adapter.FriendListAdapter;
import com.evilcorp.firebaseintegration.base.BaseFragment;
import com.evilcorp.firebaseintegration.chat.ChatActivity;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;

import java.util.List;


public class FriendListFragment extends BaseFragment implements FriendListContract.View, FriendListAdapter.FriendClickListener {

    private FriendListContract.Presenter presenter;
    private FriendListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_list, container, false);
        //setSupportActionBar(toolbar);
        //addUserPanel();
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter = new FriendListPresenter(this);
    }

    @Override
    public void setupRecyclerView(List<UserAccount> users) {
        View rootView = getView();
        if(rootView != null) {
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.user_list);
            assert recyclerView != null;
            mAdapter = new FriendListAdapter(getContext(), users, this);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setAdapter(mAdapter);
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
    public void loadChat(String chatId, String userId){
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(ChatActivity.CHAT_ID, chatId);
        intent.putExtra(ChatActivity.USER_ID, userId);
        startActivity(intent);
    }

    @Override
    public void onFriendClick(String targetUserId) {
        presenter.initChat(targetUserId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
        presenter = null;
    }
}

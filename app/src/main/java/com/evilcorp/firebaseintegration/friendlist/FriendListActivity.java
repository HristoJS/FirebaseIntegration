package com.evilcorp.firebaseintegration.friendlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ProgressBar;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.adapter.FriendListAdapter;
import com.evilcorp.firebaseintegration.base.BaseActivity;
import com.evilcorp.firebaseintegration.chat.ChatActivity;
import com.evilcorp.firebaseintegration.chat.ChatFragment;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;

import java.util.List;


public class FriendListActivity extends BaseActivity implements FriendListContract.View, FriendListAdapter.FriendClickListener {

    private ProgressBar mProgressBar;
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private FriendListContract.Presenter presenter;
    private FriendListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendlist);
        presenter = new FriendListPresenter(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        addUserPanel();

        if (findViewById(R.id.chat_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }
    @Override
    public void setupRecyclerView(List<UserAccount> users) {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.user_list);
        assert recyclerView != null;
        mAdapter = new FriendListAdapter(this, users, this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        final SwipeRefreshLayout refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                refresh_layout.setRefreshing(false);
            }
        });
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
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ChatFragment.CHAT_ID, chatId);
            arguments.putString(ChatFragment.USER_ID, userId);
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chat_detail_container, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this, ChatActivity.class);
            intent.putExtra(ChatFragment.CHAT_ID, chatId);
            intent.putExtra(ChatFragment.USER_ID, userId);
            startActivity(intent);
        }
    }

    @Override
    public void onFriendClick(String targetUserId) {
        presenter.initChat(targetUserId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.destroy();
        presenter = null;
    }
}

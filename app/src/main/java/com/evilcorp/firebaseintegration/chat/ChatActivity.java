package com.evilcorp.firebaseintegration.chat;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseActivity;
import com.evilcorp.firebaseintegration.friendlist.FriendListFragment;
import com.evilcorp.firebaseintegration.adapter.ChatAdapter;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;

import java.util.Date;
import java.util.List;

/**
 * A fragment representing a single Chat detail screen.
 * This fragment is either contained in a {@link FriendListFragment}
 * in two-pane mode (on tablets) or a {@link ChatActivity}
 * on handsets.
 */
public class ChatActivity extends BaseActivity implements ChatContract.View, View.OnClickListener, TextWatcher {
    private static final String TAG =  ChatActivity.class.getSimpleName();
    public static final String CHAT_ID = "chat_id";
    public static final String USER_ID = "user_id";

    private RecyclerView mMessageRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private Button mSendButton;
    private EditText mMessageEditText;
    private ChatContract.Presenter presenter;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle arguments = getIntent().getExtras();
        if (arguments.containsKey(CHAT_ID)&&arguments.containsKey(USER_ID)) {
            String chatId = arguments.getString(CHAT_ID);
            String userId = arguments.getString(USER_ID);
            presenter = new ChatPresenter(this,chatId,userId);
        }
        else {
            Log.d(TAG,"No chats available.");
            finish();
        }

        mMessageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(this);

        mSendButton = (Button) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void setupRecyclerView(List<UserAccount> chatParticipants) {
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setStackFromEnd(true);
        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        final ChatAdapter chatAdapter = new ChatAdapter(this,presenter.getChatReference(),chatParticipants);
        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int chatMessageCount = chatAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (chatMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mMessageRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mMessageRecyclerView.setAdapter(chatAdapter);
    }

    @Override
    public void setupToolbar(String targetUserName) {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setTitle(targetUserName);
//        setSupportActionBar(toolbar);
//
//        // Show the Up button in the action bar.
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendButton:
                presenter.sendMessage(mMessageEditText.getText().toString(),getUserId(), new Date().getTime());
                mMessageEditText.setText("");
                break;
            default:
                break;
        }
    }

    @Override
    public String getUserId() {
        return presenter.getUserId();
    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (charSequence.toString().trim().length() > 0) {
            mSendButton.setEnabled(true);
        } else {
            mSendButton.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {}

    @Override
    public List<UserAccount> getChatParticipants() {
        return presenter.getChatParticipants();
    }

    @Override
    public Context getContext() {
        return this;
    }

}

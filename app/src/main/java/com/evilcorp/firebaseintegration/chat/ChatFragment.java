package com.evilcorp.firebaseintegration.chat;

import android.content.Context;
import android.icu.util.TimeZone;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.friendlist.FriendListActivity;
import com.evilcorp.firebaseintegration.adapter.ChatAdapter;
import com.evilcorp.firebaseintegration.model.firebase.UserAccount;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a single Chat detail screen.
 * This fragment is either contained in a {@link FriendListActivity}
 * in two-pane mode (on tablets) or a {@link ChatActivity}
 * on handsets.
 */
public class ChatFragment extends Fragment implements ChatContract.View, View.OnClickListener, TextWatcher {
    private static final String TAG =  ChatFragment.class.getSimpleName();
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
    public ChatFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments.containsKey(CHAT_ID)&&arguments.containsKey(USER_ID)) {
            String chatId = arguments.getString(CHAT_ID);
            String userId = arguments.getString(USER_ID);
            presenter = new ChatPresenter(this,chatId,userId);
        }
        else {
            Log.d(TAG,"No chats available.");
            getActivity().finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mMessageRecyclerView = (RecyclerView) rootView.findViewById(R.id.messageRecyclerView);
        mMessageEditText = (EditText) rootView.findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(this);

        mSendButton = (Button) rootView.findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(this);

        return rootView;
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
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(targetUserName);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        // Show the Up button in the action bar.
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
        return super.getContext();
    }

}

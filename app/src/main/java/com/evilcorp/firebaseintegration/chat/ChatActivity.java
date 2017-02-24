package com.evilcorp.firebaseintegration.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.base.BaseActivity;
import com.evilcorp.firebaseintegration.friendlist.FriendListActivity;

/**
 * An activity representing a single Chat detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link FriendListActivity}.
 */
public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ChatFragment.CHAT_ID,
                    getIntent().getStringExtra(ChatFragment.CHAT_ID));
            arguments.putString(ChatFragment.USER_ID,
                    getIntent().getStringExtra(ChatFragment.USER_ID));
            ChatFragment fragment = new ChatFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.chat_detail_container, fragment)
                    .commit();
        }
        String user_id = getIntent().getStringExtra(ChatFragment.USER_ID);
        setSupportActionBar(new Toolbar(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, FriendListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        navigateUpTo(new Intent(this, FriendListActivity.class));
    }



}

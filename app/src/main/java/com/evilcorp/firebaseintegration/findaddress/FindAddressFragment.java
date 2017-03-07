package com.evilcorp.firebaseintegration.findaddress;

import android.app.LoaderManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.adapter.AddressRecyclerAdapter;
import com.evilcorp.firebaseintegration.base.BaseFragment;
import com.evilcorp.firebaseintegration.db.AddressContentProvider;
import com.evilcorp.firebaseintegration.db.AddressTable;
import com.evilcorp.firebaseintegration.services.GeoService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FindAddressFragment extends BaseFragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener {
    private static final String TAG = FindAddressFragment.class.getSimpleName();
    GeoService mService;
    boolean mBound = false;
    private AddressRecyclerAdapter mAdapter;
    private List<Address> mAddressList;
    private SearchView mSearchView;
    private ProgressBar progressBar;
    private RecyclerView mAddressRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Cursor mCursor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_address, container, false);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        mSearchView = (SearchView) rootView.findViewById(R.id.addressSearchView);
        mSearchView.setOnQueryTextListener(this);
        FloatingActionButton deleteButton = (FloatingActionButton) rootView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(this);
        mAddressRecyclerView = (RecyclerView) rootView.findViewById(R.id.addressRecyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mAddressRecyclerView.setLayoutManager(linearLayoutManager);
        getActivity().getLoaderManager().initLoader(0, null, this);
        mAdapter = new AddressRecyclerAdapter(getContext(), null);
        mAddressRecyclerView.setAdapter(mAdapter);
        //mAdapter = new SimpleCursorAdapter(this,R.layout.address_item,null, AddressTable.COLUMNS,new int[]{R.id.entry_id,R.id.addressLine0,R.id.addressLine1,R.id.countryName,R.id.latLon,R.id.timeStamp},0);
        //mAddressRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper());
        itemTouchHelper.attachToRecyclerView(mAddressRecyclerView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(getContext(), GeoService.class);
        getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            getContext().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        progressBar.setVisibility(View.VISIBLE);
        mSearchView.clearFocus();
        mService.findAddress(query, new GeoService.ResultListener() {
            @Override
            public void success(final List<Address> addresses) {
                Log.d(TAG,"Success");
                Log.d(TAG,"addresses size : "+addresses.size());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String time = sdf.format(new Date().getTime());
                for(Address address : addresses) {
                    ContentValues values = new ContentValues();
                    values.put(AddressTable.ADDRESS_LINE0, address.getAddressLine(0));
                    values.put(AddressTable.ADDRESS_LINE1, address.getAddressLine(1));
                    values.put(AddressTable.COUNTRY_NAME, address.getCountryName());
                    if (address.hasLatitude() && address.hasLongitude())
                        values.put(AddressTable.LAT_LONG, address.getLatitude() + " : " + address.getLongitude());
                    else values.put(AddressTable.LAT_LONG, "No LatLong");
                    values.put(AddressTable.TIMESTAMP, time);
                    getContext().getContentResolver().insert(AddressContentProvider.ADDRESS_URI, values);
                }
            }
            @Override
            public void fail(final String reason) {
                Log.d(TAG,reason);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(),reason,Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to GeoService, cast the IBinder and get GeoService instance
            GeoService.LocalBinder binder = (GeoService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(),
                AddressContentProvider.ADDRESS_URI, AddressTable.COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.deleteButton:
                    int rows_deleted = getContext().getContentResolver().delete(AddressContentProvider.ADDRESS_URI, null, null);
                    Toast.makeText(getContext(),rows_deleted+" rows deleted.",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    private class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

        private RecyclerItemTouchHelper() {
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.RIGHT);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            final int fromPos = viewHolder.getAdapterPosition();
            final int toPos = target.getAdapterPosition();
            // move item in `fromPos` to `toPos` in adapter.
            Log.d(TAG,"From: "+ fromPos+ "To: "+ toPos);
            return true;// true if moved, false otherwise
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int pos = viewHolder.getAdapterPosition();
            final long db_pos = mAdapter.getItemId(pos);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Uri new_uri = AddressContentProvider.ADDRESS_URI.buildUpon().appendPath(String.valueOf(db_pos)).build();
                    Log.d(TAG,new_uri.toString());
                    int rows_deleted = getContext().getContentResolver().delete(new_uri,null,null);
                    Log.d(TAG,rows_deleted+" rows deleted.");
                }
            }).run();
            mAdapter.notifyItemRemoved(pos);
        }
    }
}

package com.evilcorp.firebaseintegration.adapter;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.evilcorp.firebaseintegration.R;
import com.evilcorp.firebaseintegration.db.AddressTable;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by hristo.stoyanov on 01-Feb-17.
 */

public class AddressRecyclerAdapter extends CursorRecyclerViewAdapter<AddressRecyclerAdapter.AddressViewHolder>{

    public AddressRecyclerAdapter(Context context, Cursor cursor){
        super(context,cursor);
    }

    @Override
    public AddressViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.address_item, parent, false);
        return new AddressViewHolder(layoutView);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(AddressViewHolder holder, Cursor cursor) {
        cursor.moveToPosition(cursor.getPosition());
        String addressLine0 = cursor.getString(cursor.getColumnIndex(AddressTable.ADDRESS_LINE0));
        holder.addressLine0.setText(addressLine0);
        String addressLine1 = cursor.getString(cursor.getColumnIndex(AddressTable.ADDRESS_LINE1));
        holder.addressLine1.setText(addressLine1);
        String countryName = cursor.getString(cursor.getColumnIndex(AddressTable.COUNTRY_NAME));
        holder.countryName.setText(countryName);
        String latLon = cursor.getString(cursor.getColumnIndex(AddressTable.LAT_LONG));
        holder.latLon.setText(latLon);
        String timeStamp = cursor.getString(cursor.getColumnIndex(AddressTable.TIMESTAMP));
        holder.timeStamp.setText(timeStamp);
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView addressLine0;
        TextView addressLine1;
        TextView countryName;
        TextView latLon;
        TextView timeStamp;

        AddressViewHolder(View itemView) {
            super(itemView);
            addressLine0 = (TextView) itemView.findViewById(R.id.addressLine0);
            addressLine1 = (TextView) itemView.findViewById(R.id.addressLine1);
            countryName = (TextView) itemView.findViewById(R.id.countryName);
            latLon = (TextView) itemView.findViewById(R.id.latLon);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
        }
    }


}

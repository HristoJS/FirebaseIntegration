package com.evilcorp.firebaseintegration.ui.base;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.evilcorp.firebaseintegration.R;



public abstract class BaseFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private ProgressBar mProgressBar;

    private void dismissDialog(DialogInterface dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgress();
        dismissDialog(mAlertDialog);
        dismissDialog(mProgressDialog);
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(getContext(), getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        dismissDialog(mProgressDialog);
    }

    protected void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    protected void showAlert(String msg, DialogInterface.OnClickListener onClickListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mAlertDialog = builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.ic_firebase_logo)
                .setPositiveButton("OK", onClickListener).create();
        mAlertDialog.show();
    }
    protected void showAlert(String msg) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        mAlertDialog = builder.setTitle(getResources().getString(R.string.app_name))
                .setMessage(msg)
                .setCancelable(false)
                .setIcon(R.drawable.ic_firebase_logo)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();
        mAlertDialog.show();
    }
}

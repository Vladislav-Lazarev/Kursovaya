package com.hpcc.kursovaya;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ProgressBarDialog extends DialogFragment {
    private View view;
    private Context context;
    private String title;

    public static ProgressBarDialog newInstance(View view, Context context,String title) {
        Bundle args = new Bundle();
        ProgressBarDialog fragment = new ProgressBarDialog();
        fragment.view = view;
        fragment.context = context;
        fragment.title = title;
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setView(view).create();
    }
}

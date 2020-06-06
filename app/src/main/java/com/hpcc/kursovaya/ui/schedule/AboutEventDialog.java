package com.hpcc.kursovaya.ui.schedule;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.hpcc.kursovaya.R;

public class AboutEventDialog extends DialogFragment {

    private static String TAG = AboutEventDialog.class.getSimpleName();
    private Context context;
    private String name;
    private String description;


    public static AboutEventDialog newInstance(Context context, String name, String description) {
        Bundle args = new Bundle();
        AboutEventDialog fragment = new AboutEventDialog();
        fragment.context = context;
        fragment.name = name;
        fragment.description = description;
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.about_event_dialog_title);
        View view = View.inflate(context,R.layout.dialog_about_event,null);
        TextView nameTV = view.findViewById(R.id.contentName);
        TextView descTV = view.findViewById(R.id.contentDescription);
        nameTV.setText(name);
        descTV.setText(description);
        builder.setView(view);
        builder.setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        return builder.create();
    }
}

package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Dialog pop up a message after accept or decline a friend request.
 */

public class Dialog extends AppCompatDialogFragment {
    public String answer;
    public String user;

    public Dialog(String answer, String user) {
        this.answer = answer;
        this.user = user;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Message").setMessage("Successfully " + answer + " " + user + "'s friend request").setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //No onclick activity
            }
        });
        return builder.create();
    }
}

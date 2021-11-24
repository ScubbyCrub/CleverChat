package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.view.View;

/**
 * Interface that defines a class as needing to support clickable list items
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public interface OnListItemClick {

    /**
     * Define what to do when someone clicks on an item in a list.
     *
     * @param view The view the ListItemClick exists in.
     * @param position The position the list exists in.
     */
    void onClick(View view, int position);

    /**
     * Define what to do if someone holds down a click while clicking.
     *
     * @param view The view the ListItemClick exists in.
     * @param position The position the list exists in.
     */
    void onLongClick(View view, int position);
}

package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.R;

/**
 * Defines the fragment functionality of displaying a single contact as a card.
 * @Author Vlad Tregubov
 * @version 1
 */
public class ContactCardNewChatFragment extends Fragment {

    /**
     * Constructor for ContactCardNewChatFragment
     */
    public ContactCardNewChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact_card_new_chat, container, false);
    }
}
package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatBinding;

/**
 * Chat Fragment to allow for UI elements to function when the user is interacting with
 * a chat room.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class ChatFragment extends Fragment {
    private FragmentChatBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        mBinding = FragmentChatBinding.inflate(theInflater);
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }


}

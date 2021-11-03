package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatBinding;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentSignInBinding;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }


}

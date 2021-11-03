package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactBinding;

public class ContactFragment extends Fragment {
    private FragmentContactBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentContactBinding.inflate(inflater);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
}

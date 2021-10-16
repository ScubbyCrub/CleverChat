package edu.uw.tcss450.angelans.lab1myfirstapp.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.lab1myfirstapp.R;
import edu.uw.tcss450.angelans.lab1myfirstapp.databinding.FragmentAngelaBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AngelaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AngelaFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_angela, container, false);
    }
}
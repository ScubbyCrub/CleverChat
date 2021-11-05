package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactBinding;

/**
 * Contact Fragment to allow for UI elements to function when the user is interacting with
 * their contacts page.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class ContactFragment extends Fragment {
    private FragmentContactBinding mBinding;

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        mBinding = FragmentContactBinding.inflate(theInflater);
        // Inflate the layout for this fragment
        return mBinding.getRoot();
    }
}

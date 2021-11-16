package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactListBinding;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentHomeBinding;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentSignInBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

/**
 * Contact Fragment to allow for UI elements to function when the user is interacting with
 * their contacts page.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */

public class ContactListFragment extends Fragment {

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_contact_list, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        FragmentContactListBinding binding = FragmentContactListBinding.bind(getView());
        List<ContactInfo> contactInfoList = ContactGenerator.getContactList();
        ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(contactInfoList);
        binding.listRoot.setAdapter(adapter);
        binding.layoutWait.setVisibility(View.GONE);
    }



}

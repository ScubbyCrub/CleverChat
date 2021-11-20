package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactListBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

/**
 * Contact Fragment to allow for UI elements to function when the user is interacting with
 * their contacts page.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */

public class ContactListFragment extends Fragment {

    private ContactListViewModel mContactListViewModel;
    private UserInfoViewModel mUserModel;
    private RecyclerView recyclerView;
    private TextView textView;

    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactListViewModel = provider.get(ContactListViewModel.class);
        mContactListViewModel.getContactList(mUserModel.getEmail(), mUserModel.getmJwt());
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
        ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(mContactListViewModel.getContactListByEMail(mUserModel.getEmail()));
        binding.listRoot.setAdapter(adapter);

        //if the user don't have any contact, show message instead of recyclerview
//        recyclerView = (RecyclerView) getView().findViewById(R.id.list_root);
//        textView = (TextView) getView().findViewById(R.id.empty_view);
//        if (adapter.getItemCount()==0) {
//            recyclerView.setVisibility(View.GONE);
//            textView.setVisibility(View.VISIBLE);
//        }
//        else {
//            recyclerView.setVisibility(View.VISIBLE);
//            textView.setVisibility(View.GONE);
//        }

        //set the wait fragment to invisible
        binding.layoutWait.setVisibility(View.GONE);
    }



}

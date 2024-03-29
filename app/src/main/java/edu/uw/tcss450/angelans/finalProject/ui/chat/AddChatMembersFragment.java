package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentAddChatMembersBinding;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

/**
 *
 */
public class AddChatMembersFragment extends Fragment {
    private FragmentAddChatMembersBinding mBinding;
    private AddNewChatMembersViewModel addNewChatMembersViewModel;


    public AddChatMembersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        //get shared prefs
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //make needed requests
        addNewChatMembersViewModel = new ViewModelProvider(getActivity())
                .get(AddNewChatMembersViewModel.class);
        //get the contacts of the user
        addNewChatMembersViewModel.connectGetContacts(prefs.getString("email",""),
                prefs.getString(getString(R.string.keys_prefs_jwt),""),
                "" + AddChatMembersFragmentArgs.fromBundle(getArguments()).getChatid());
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState){
        super.onViewCreated(theView,theSavedInstanceState);
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //old observer for navigating to new chat
//        mNewChatViewModel.addNewChatObserver(getViewLifecycleOwner(), created -> {
//            //set up navigation
//            NewChatFragmentDirections.ActionNewChatFragmentToSingleChatFragment dir =
//                    NewChatFragmentDirections.actionNewChatFragmentToSingleChatFragment();
//            dir.setId(mNewChatViewModel.getChatId());
//            Navigation.findNavController(getView()).navigate(dir);
//        });
        //old observer for making new chat
//        mBinding.buttonCreateNewChat.setOnClickListener(button -> {
//            //make request
//            mNewChatViewModel.connectPost(
//                    mBinding.editTextChatName.getText().toString().trim(),
//                    prefs.getString(getString(R.string.keys_prefs_jwt), "")
//            );
//
//        });
        Consumer<Contact> add = (contact) -> addNewChatMembersViewModel.addContact(contact);
        Consumer<Contact> remove = (contact) -> addNewChatMembersViewModel.removeContact(contact);
        ContactGeneratorChat stuff = new ContactGeneratorChat();
        addNewChatMembersViewModel.addSelectedChatObserver(getViewLifecycleOwner(), contactList -> {
            System.out.println("My Contact: " + contactList.toString());
            mBinding.listAddContacts.setAdapter(new ContactCardListRecyclerViewAdapter(contactList, add, remove));
        });
        mBinding.button.setOnClickListener(clicked -> {
            System.out.println("Adding Members!");
            addNewChatMembersViewModel.addMembers(prefs.getString(
                    getString(R.string.keys_prefs_jwt),""));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentAddChatMembersBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
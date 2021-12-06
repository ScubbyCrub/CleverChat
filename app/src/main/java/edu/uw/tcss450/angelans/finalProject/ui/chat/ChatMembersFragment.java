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
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatMembersBinding;
import edu.uw.tcss450.angelans.finalProject.model.Contact;
import edu.uw.tcss450.angelans.finalProject.ui.auth.signin.SignInFragmentArgs;


public class ChatMembersFragment extends Fragment {
    private ChatMembersViewModel mModel;
    public ChatMembersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mModel = new ViewModelProvider(getActivity()).get(ChatMembersViewModel.class);
        ChatMembersFragmentArgs args = ChatMembersFragmentArgs.fromBundle(getArguments());
        mModel.getChatMembers(prefs.getString(getString(R.string.keys_prefs_jwt),""),""+args.getChatid());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        //set up binding to the view
        FragmentChatMembersBinding binding = FragmentChatMembersBinding.bind(getView());
        super.onViewCreated(view,savedInstanceState);
        //add listener that connects the recycler view with the data
        Consumer<Contact> addContact = (contact) -> {
            System.out.println("Adding to contacts");
        };
        Consumer<Contact> removeFromChat = (contact) -> {
            System.out.println("Removing Contact From Chat");
        };
        mModel.addContactListObserver(getViewLifecycleOwner(), contactList -> {
            System.out.println("Setting adapter");
            System.out.println(contactList.toString());
            binding.listContact.setAdapter(
                    new ChatMembersRecyclerViewAdapter(contactList, addContact,removeFromChat)
            );
        });

    }
}
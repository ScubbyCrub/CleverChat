package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

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
        mModel.setEmail(prefs.getString("email",""));
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
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        FragmentChatMembersBinding binding = FragmentChatMembersBinding.bind(getView());
        super.onViewCreated(view,savedInstanceState);
        //add listener that connects the recycler view with the data
        Consumer<Contact> addContact = (contact) -> {
            System.out.println("Adding to contacts " + contact.getEmail());
            mModel.addMemberAsContact(prefs.getString(getString(R.string.keys_prefs_jwt),""),
                    prefs.getString("email",""),
                    contact.getEmail());
        };
        Consumer<Contact> removeFromChat = (contact) -> {
            System.out.println("Removing Contact From Chat " + contact.getEmail());
            ChatMembersFragmentArgs args = ChatMembersFragmentArgs.fromBundle(getArguments());
            mModel.deleteChatMember(prefs.getString(getString(R.string.keys_prefs_jwt),""),
                    "" + args.getChatid(),
                    "" + contact.getId());
        };
        mModel.addCurrentContactsObserver(getViewLifecycleOwner(), contactList -> {
            System.out.println("Setting adapter");
            System.out.println("Members: " + contactList.toString());
            System.out.println("Current: " + mModel.getMemberList().toString());
            System.out.println(contactList.toString());
            binding.listContact.setAdapter(
                    new ChatMembersRecyclerViewAdapter(mModel.getMemberList(),contactList, addContact,removeFromChat)
            );
        });

        binding.buttonAddNewMember.setOnClickListener(button -> {
            ChatMembersFragmentDirections.ActionChatMembersFragmentToAddChatMembersFragment dir =
               ChatMembersFragmentDirections.actionChatMembersFragmentToAddChatMembersFragment();
            dir.setChatid(ChatMembersFragmentArgs.fromBundle(getArguments()).getChatid());
            Navigation.findNavController(getView()).navigate(dir);
        });

    }
}
package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.MainActivity;
import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentNewChatBinding;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

/**
 * Fragment that allows creation of new chat
 * @author Vlad Tregubov
 * @version  1
 */
public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding mBinding;
    private NewChatViewModel mNewChatViewModel;
    public NewChatFragment() {
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
        mNewChatViewModel = new ViewModelProvider(getActivity())
                .get(NewChatViewModel.class);
        mNewChatViewModel.connectGetContacts(prefs.getString("email",""),
                prefs.getString(getString(R.string.keys_prefs_jwt),""));
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState){
        super.onViewCreated(theView,theSavedInstanceState);
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mNewChatViewModel.addNewChatObserver(getViewLifecycleOwner(), created -> {
            //set up navigation
            NewChatFragmentDirections.ActionNewChatFragmentToSingleChatFragment dir =
                    NewChatFragmentDirections.actionNewChatFragmentToSingleChatFragment();
            dir.setId(mNewChatViewModel.getChatId());
            Navigation.findNavController(getView()).navigate(dir);
        });
        mBinding.buttonCreateNewChat.setOnClickListener(button -> {
            //make request
            mNewChatViewModel.connectPost(
                    mBinding.editTextChatName.getText().toString().trim(),
                    prefs.getString(getString(R.string.keys_prefs_jwt),"")
            );

        });
        Consumer<Contact> add = (contact) -> mNewChatViewModel.addContact(contact);
        Consumer<Contact> remove = (contact) -> mNewChatViewModel.removeContact(contact);
        ContactGeneratorChat stuff = new ContactGeneratorChat();
        mNewChatViewModel.addSelectedChatObserver(getViewLifecycleOwner(), contactList -> {
            System.out.println("My Contact: " + contactList.toString());
            mBinding.listRoot.setAdapter(new ContactCardListRecyclerViewAdapter(contactList, add, remove));
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mBinding = FragmentNewChatBinding.inflate(inflater);
        return mBinding.getRoot();
    }
}
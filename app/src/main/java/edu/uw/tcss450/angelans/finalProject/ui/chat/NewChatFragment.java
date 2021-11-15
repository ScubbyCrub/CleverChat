package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentNewChatBinding;
import edu.uw.tcss450.angelans.finalProject.ui.auth.signin.SignInViewModel;


public class NewChatFragment extends Fragment {
    private FragmentNewChatBinding mBinding;
    private NewChatViewModel mNewChatViewModel;
    public NewChatFragment() {
        // Required empty public constructor
    }

    public void onCreate(@Nullable Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        mNewChatViewModel = new ViewModelProvider(getActivity())
                .get(NewChatViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState){
        super.onViewCreated(theView,theSavedInstanceState);
        String[] members = new String[1];
        members[0] = "7";
        //send the request
        mBinding.buttonCreateNewChat.setOnClickListener(button -> {
            //make request
            mNewChatViewModel.connectPost(
                    mBinding.editTextChatName.getText().toString().trim(),
                    members
            );

        });

        //add observer to navigate away from the page
        //TODO: FIGURE OUT WHY THE OBSERVER AUTO NAVIGATES YOU BACK
//        mNewChatViewModel.addNewChatObserver(getViewLifecycleOwner(), data -> {
//            Navigation.findNavController(getView()).navigate(
//                    NewChatFragmentDirections.actionNewChatFragmentToNavigationChat()
//            );
//        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mBinding = FragmentNewChatBinding.inflate(inflater);
        return mBinding.getRoot();
//        return inflater.inflate(R.layout.fragment_new_chat, container, false);
    }
}
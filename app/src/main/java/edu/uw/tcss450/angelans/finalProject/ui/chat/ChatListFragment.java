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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatListBinding;
import edu.uw.tcss450.angelans.finalProject.model.Chat;

public class ChatListFragment extends Fragment {
    private ChatListViewModel mModel;
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        System.out.println(view.toString());

        return view;
//        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }
    /*
    TODO the issue happens when we do not stay signed in, because in that case the json web token is not stored
    just kinda store it every time as a seperate jwt propperty that is diff than the one we use for loin
    only update that when we update the login one
    and boom
    perfect
    reset both when we sign out
     */
    public void onCreate(@Nullable Bundle savedInstanceState){
        //TODO determine if this is an issue because we move from the auth act to main act
        super.onCreate(savedInstanceState);
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
        Log.e("Contains json", ""+prefs.getString(getString(R.string.keys_prefs_jwt),""));
        mModel.connectGet(prefs.getString(getString(R.string.keys_prefs_jwt),""));
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        FragmentChatListBinding binding = FragmentChatListBinding.bind(getView());
        //assign the adapaer to the actual recycler view3
        Consumer<Chat> clicked = (chat) -> {
//            Log.e("clicked chat", chat.getName() + " " + chat.getId() );
            ChatListFragmentDirections.ActionNavigationChatToSingleChatFragment dir =
                    ChatListFragmentDirections.actionNavigationChatToSingleChatFragment();
            dir.setId(chat.getId());
            Navigation.findNavController(getView()).navigate(dir);
        };
        mModel.addChatListObserver(getViewLifecycleOwner(), chatList -> {
            binding.listRoot.setAdapter(
                    new ChatRecyclerViewAdapter(chatList, clicked)
            );
            //TODO: Add loading overlay here
        });

        //add listener to new chat button
        binding.buttonNewChat.setOnClickListener(button -> {
            Navigation.findNavController(getView()).navigate(
                    ChatListFragmentDirections.actionNavigationChatToNewChatFragment()
            );
        });
    }


}
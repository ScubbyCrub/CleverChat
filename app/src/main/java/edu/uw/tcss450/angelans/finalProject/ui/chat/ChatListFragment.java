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
import edu.uw.tcss450.angelans.finalProject.model.NewMessageCountViewModel;

/**
 * Fragment that displays the list of the users current chats that they are a part of
 * @Author Vlad Tregubov
 * @version 1
 */
public class ChatListFragment extends Fragment {
    private ChatListViewModel mModel;
    private NewMessageCountViewModel mNewMessageCountViewModel;

    /**
     * Constructor for chat list fragment
     */
    public ChatListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
        mModel.connectGet(prefs.getString(getString(R.string.keys_prefs_jwt),""));
        mNewMessageCountViewModel = new ViewModelProvider(getActivity())
                .get(NewMessageCountViewModel.class);
    }
    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Resumed!x");
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
        mModel.connectGet(prefs.getString(getString(R.string.keys_prefs_jwt),""));
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        //set up shared prefs
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        FragmentChatListBinding binding = FragmentChatListBinding.bind(getView());

        //handle opening chats from list when clicked
        Consumer<Chat> clicked = (chat) -> {
            SingleChatViewModel mChatModel = new ViewModelProvider(getActivity())
                    .get(SingleChatViewModel.class);
            ChatListFragmentDirections.ActionNavigationChatToSingleChatFragment dir =
                    ChatListFragmentDirections.actionNavigationChatToSingleChatFragment();
            dir.setId(chat.getId());
            dir.setChatName(chat.getName());
            mChatModel.updateMostRecentVisitedChatID(chat.getId());
            Log.d("ChatListFragment", "Recent SingleChat ID updated: " + chat.getId());
            Navigation.findNavController(getView()).navigate(dir);
        };
        Consumer<Chat> delete = (chat) -> {
            System.out.println("delete " + chat.getId());
            mModel.deleteChat(chat,prefs.getString(getString(R.string.keys_prefs_jwt), ""));
        };
        //set up recycler view on recieving data
        mModel.addChatListObserver(getViewLifecycleOwner(), chatList -> {
            mNewMessageCountViewModel.addMessageCountObserver(getViewLifecycleOwner(), idMessageMap -> {
                binding.listRoot.setAdapter(
                        new ChatRecyclerViewAdapter(chatList, clicked, delete, idMessageMap)
                );
                //TODO: Add loading overlay here
            } );
        });

        //add listener to new chat button
        binding.buttonNewChat.setOnClickListener(button -> {
            Navigation.findNavController(getView()).navigate(
                    ChatListFragmentDirections.actionNavigationChatToNewChatFragment()
            );
        });
    }


}
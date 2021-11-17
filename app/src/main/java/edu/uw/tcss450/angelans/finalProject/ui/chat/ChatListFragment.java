package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatListBinding;

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
        if (view instanceof RecyclerView) {
            //Try out a grid layout to achieve rows AND columns. Adjust the widths of the
            //cards on display
//            ((RecyclerView) view).setLayoutManager(new GridLayoutManager(getContext(), 2));

            //Try out horizontal scrolling. Adjust the widths of the card so that it is
            //obvious that there are more cards in either direction. i.e. don't have the cards
            //span the entire witch of the screen. Also, when considering horizontal scroll
            //on recycler view, ensure that thre is other content to fill the screen.
//            ((LinearLayoutManager)((RecyclerView) view).getLayoutManager())
//                    .setOrientation(LinearLayoutManager.HORIZONTAL);

//            ((RecyclerView) view).setAdapter(
//                    new ChatRecyclerViewAdapter(ChatGenerator.getBlogList()));
        }
        return view;
//        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(getActivity()).get(ChatListViewModel.class);
        mModel.connectGet();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        FragmentChatListBinding binding = FragmentChatListBinding.bind(getView());
        //assign the adapaer to the actual recycler view3
        mModel.addChatListObserver(getViewLifecycleOwner(), chatList -> {
            binding.listRoot.setAdapter(
                    new ChatRecyclerViewAdapter(chatList)
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
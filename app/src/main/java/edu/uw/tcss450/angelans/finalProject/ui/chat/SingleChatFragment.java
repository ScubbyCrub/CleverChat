package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentSingleChatBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class SingleChatFragment extends Fragment {

    //The chat ID for "global" chat
    private  int HARD_CODED_CHAT_ID;

    private SingleChatViewModel mChatModel;
    private UserInfoViewModel mUserModel;
    private SingleChatSendViewModel mSendModel;

    public SingleChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingleChatFragmentArgs args = SingleChatFragmentArgs.fromBundle(getArguments());
        HARD_CODED_CHAT_ID = args.getId();
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mChatModel = provider.get(SingleChatViewModel.class);
        mChatModel.getFirstMessages(HARD_CODED_CHAT_ID, mUserModel.getmJwt());
        mSendModel = provider.get(SingleChatSendViewModel.class);

        Log.d("ChatId", ""+args.getId());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentSingleChatBinding binding = FragmentSingleChatBinding.bind(getView());

        Log.d("Single Chat Fragment:", "Progress Bar showing start");
        //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        binding.swipeContainer.setRefreshing(true);

        final RecyclerView rv = binding.recyclerMessages;
        //Set the Adapter to hold a reference to the list FOR THIS chat ID that the ViewModel
        //holds.
        rv.setAdapter(new SingleChatRecyclerViewAdapter(
                        mChatModel.getMessageListByChatId(HARD_CODED_CHAT_ID),
                        mUserModel.getEmail()));

        Log.d("Single Chat Fragment", "Fetch more msg when scrolled to top");
        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        binding.swipeContainer.setOnRefreshListener(() -> {
            mChatModel.getNextMessages(HARD_CODED_CHAT_ID, mUserModel.getmJwt());
        });

        Log.d("Single Chat Fragment", "Inform chat list may have changed");
        mChatModel.addMessageObserver(HARD_CODED_CHAT_ID, getViewLifecycleOwner(),
                list -> {
                    //inform the RV that the underlying list has (possibly) changed
                    rv.getAdapter().notifyDataSetChanged();
                    rv.scrollToPosition(rv.getAdapter().getItemCount() - 1);
                    binding.swipeContainer.setRefreshing(false);
                });

        Log.d("Single Chat Fragment", "Send button clicked!");
        // Send button was clicked. Send the message via the SendViewModel
        binding.buttonSend.setOnClickListener(button -> {
            mSendModel.sendMessage(HARD_CODED_CHAT_ID,
                    mUserModel.getmJwt(),
                    binding.editMessage.getText().toString());
        });

        Log.d("Single Chat Fragment", "Server responded to msg send, cleared text in chat");
        // When we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response ->
                binding.editMessage.setText(""));
    }
}

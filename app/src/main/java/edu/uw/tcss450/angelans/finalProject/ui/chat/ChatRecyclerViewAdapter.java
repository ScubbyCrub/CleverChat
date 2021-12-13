package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.angelans.finalProject.model.Chat;
import edu.uw.tcss450.angelans.finalProject.model.NewMessageCountViewModel;
import edu.uw.tcss450.angelans.finalProject.ui.auth.register.RegisterViewModel;

/**
 * Recycler view adapter for list of chats
 * @Author Vlad Tregubov
 * @version  1
 */
public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder> {
    private List<Chat> mChats = new ArrayList<Chat>();
    private final Consumer<Chat> selectedChat;
    private final Consumer<Chat> deleteChat;
    protected final Map<Integer,Integer> mNotifInfo;

    /**
     * 2 argument constructor
     * @param items items to put in the list
     * @param selectedChat  Chat consumer
     */
    public ChatRecyclerViewAdapter(List<Chat> items, Consumer<Chat> selectedChat,
                                   Consumer<Chat> deleteChat, Map<Integer,Integer> theNotifInfo){
        this.mChats = items;
        this.selectedChat = selectedChat;
        this.deleteChat = deleteChat;
        this.mNotifInfo = theNotifInfo;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_chat_card,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.setChat(mChats.get(position));
    }

    @Override
    public int getItemCount() {
        return mChats.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     *
     */
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatCardBinding binding;
        private Chat mChat;

        /**
         * Constructor for ChatViewHolder
         *
         * @param view The view that this ChatViewHolder exists in
         */
        public ChatViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
        }

        /**
         * Sets the values for each individual chat in recycler view
         * @param chat chat containing values
         */
        void setChat(final Chat chat) {
            mChat = chat;
            binding.textContactName.setText(chat.getName());
            binding.cardRoot.setOnClickListener(data -> {
                selectedChat.accept(chat);
            });
            binding.buttonDeleteChat.setOnClickListener(data -> {
                deleteChat.accept(chat);
            });
            if (mNotifInfo.get(chat.getId()) != null) {
                int newMessageCount = mNotifInfo.get(chat.getId());
                Log.d("ChatRecyclerViewAdapter", "Notif added to ID:" + chat.getId());
                String stringNewMessageCount = "" + newMessageCount;
                if (newMessageCount > 9) {
                    stringNewMessageCount = "9+";
                }
                binding.chatCardNotifCounter.setText(stringNewMessageCount);
                binding.chatCardNotifCounter.setVisibility(View.VISIBLE);
                binding.chatCardNotifCircle.setVisibility(View.VISIBLE);
            } else {
                binding.chatCardNotifCounter.setVisibility(View.INVISIBLE);
                binding.chatCardNotifCircle.setVisibility(View.INVISIBLE);
            }
        }
    }
}

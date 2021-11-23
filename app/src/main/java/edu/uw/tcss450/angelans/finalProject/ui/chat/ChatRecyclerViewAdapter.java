package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatCardBinding;
import edu.uw.tcss450.angelans.finalProject.model.Chat;

public class ChatRecyclerViewAdapter extends RecyclerView.Adapter<ChatRecyclerViewAdapter.ChatViewHolder> {
    private List<Chat> mChats = new ArrayList<Chat>();
    private final Consumer<Chat> selectedChat;

    public ChatRecyclerViewAdapter(List<Chat> items, Consumer<Chat> selectedChat){
        this.mChats = items;
        this.selectedChat = selectedChat;
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
     * of rows in the Blog Recycler View.
     */
    public class ChatViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public FragmentChatCardBinding binding;
        private Chat mChat;

        public ChatViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentChatCardBinding.bind(view);
//            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
        }

        /**
         * When the button is clicked in the more state, expand the card to display
         * the blog preview and switch the icon to the less state.  When the button
         * is clicked in the less state, shrink the card and switch the icon to the
         * more state.
         * @param button the button that was clicked
         */
//        private void handleMoreOrLess(final View button) {
//            displayPreview();
//        }

        /**
         * Helper used to determine if the preview should be displayed or not.
         */
//        private void displayPreview() {
//            if (binding.textPreview.getVisibility() == View.GONE) {
//                binding.textPreview.setVisibility(View.VISIBLE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_less_grey_24dp));
//            } else {
//                binding.textPreview.setVisibility(View.GONE);
//                binding.buittonMore.setImageIcon(
//                        Icon.createWithResource(
//                                mView.getContext(),
//                                R.drawable.ic_more_grey_24dp));
//            }
//        }

        void setChat(final Chat chat) {
            mChat = chat;
//            binding.buttonFullPost.setOnClickListener(view -> {
//                //TODO add navigation later step
//            });
//            binding.textTitle.setText(blog.getTitle());
//            binding.textPubdate.setText(blog.getPubDate());
            //set the name for the chat
            binding.textContactName.setText(chat.getName());
            binding.cardRoot.setOnClickListener(data -> {
                selectedChat.accept(chat);
            });
            //Use methods in the HTML class to format the HTML found in the text
//            final String preview =  Html.fromHtml(
//                    blog.getTeaser(),
//                    Html.FROM_HTML_MODE_COMPACT)
//                    .toString().substring(0,100) //just a preview of the teaser
//                    + "...";
//            binding.textPreview.setText(preview);
//            displayPreview();
        }
    }
}

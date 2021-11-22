package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactCardNewChatBinding;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

public class ContactCardListRecyclerViewAdapter extends RecyclerView.Adapter<ContactCardListRecyclerViewAdapter.ContactViewHolder> {
    private final List<Contact> mContacts;
    private final List<Contact> selectedContacts = new ArrayList<Contact>();
    private final Consumer<Contact> mAddContact;
    private final Consumer<Contact> mRemoveContact;

    public ContactCardListRecyclerViewAdapter(List<Contact> contacts, Consumer<Contact> addCOntact,
                                              Consumer<Contact> removeContact) {
        mContacts = contacts;
        mAddContact = addCOntact;
        mRemoveContact = removeContact;
    }
    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).
                inflate(R.layout.fragment_contact_card_new_chat,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        holder.setContact(mContacts.get(position));
    }

    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    /**
     * Objects from this class represent an Individual row View from the List
     * of rows in the Blog Recycler View.
     */
    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
//        public FragmentBlogCardBinding binding;
        public FragmentContactCardNewChatBinding binding;
        private Contact mContact;

        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardNewChatBinding.bind(view);
//            binding.buittonMore.setOnClickListener(this::handleMoreOrLess);
            //TODO add on click listener that adds it to the view model array
        }

        /**
         * When the button is clicked in the more state, expand the card to display
         * the blog preview and switch the icon to the less state.  When the button
         * is clicked in the less state, shrink the card and switch the icon to the
         * more state.
         * @param button the button that was clicked
         */
        private void handleMoreOrLess(final View button) {
            displayPreview();
        }

        /**
         * Helper used to determine if the preview should be displayed or not.
         */
        private void displayPreview() {
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
            Log.e("Adaptyer", "displayPreview: Called m3ethod");
//            if(selectedContacts.contains(mContact)){
//                binding.layoutRootCardNewChat.setBackgroundColor(Color.GREEN);
//            } else {
//                binding.layoutRootCardNewChat.setBackgroundColor(Color.RED);
//            }
        }

        void setContact(final Contact contact) {
            mContact = contact;
//            if(Math.random() >  0.5){
//                selectedContacts.add(contact);
//            }
//            displayPreview();
//            mBlog = blog;
//            binding.buttonFullPost.setOnClickListener(view -> {
//                //TODO add navigation later step
//            });
            binding.layoutRootCardNewChat.setBackgroundColor(Color.LTGRAY);
            binding.cardRootNewChat.setOnClickListener(data -> {
                if(!selectedContacts.contains(contact)){
                    //add to contact
                    selectedContacts.add(contact);
                    mAddContact.accept(contact);
                    binding.layoutRootCardNewChat.setBackgroundColor(Color.GRAY);
                } else {
                    selectedContacts.remove(contact);
                    binding.layoutRootCardNewChat.setBackgroundColor(Color.LTGRAY);
                    mRemoveContact.accept(contact);
                }
            });

             binding.textContactNameNewChat.setText(contact.getFirstName());
             binding.textContactUserNameNewChat.setText(contact.getUsername());

//            binding.textTitle.setText(blog.getTitle());
//            binding.textPubdate.setText(blog.getPubDate());
//            //Use methods in the HTML class to format the HTML found in the text
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

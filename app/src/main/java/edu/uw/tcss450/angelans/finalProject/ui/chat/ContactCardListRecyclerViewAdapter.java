package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactCardNewChatBinding;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

/**
 * Adapter that handles list of contacts in new chat creation menu
 * @Author Vlad Tregubov
 * @Version 1
 */
public class ContactCardListRecyclerViewAdapter extends RecyclerView.Adapter<ContactCardListRecyclerViewAdapter.ContactViewHolder> {
    private final List<Contact> mContacts;
    private final List<Contact> selectedContacts = new ArrayList<Contact>();
    private final Consumer<Contact> mAddContact;
    private final Consumer<Contact> mRemoveContact;

    /**
     * Constructor
     * @param contacts list of contacts for list
     * @param addCOntact Consumer with contact adding behaviour
     * @param removeContact Consumer with contact removing behavior
     */
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
        public FragmentContactCardNewChatBinding binding;
        private Contact mContact;

        /**
         * Constructor for ContactViewHolder
         *
         * @param view The view that ContactViewHolder exists in
         */
        public ContactViewHolder(View view) {
            super(view);
            mView = view;
            binding = FragmentContactCardNewChatBinding.bind(view);
        }

        /**
         * Sets the contact that should be displayed in the recycler view.
         *
         * @param contact A member of the user's contact list.
         */
        void setContact(final Contact contact) {
            Resources res = mView.getContext().getResources();
            mContact = contact;
            binding.layoutRootCardNewChat.setBackgroundColor(
                    res.getColor(R.color.card_color, null));
            binding.cardRootNewChat.setOnClickListener(data -> {
                if(!selectedContacts.contains(contact)){
                    //add to contact
                    selectedContacts.add(contact);
                    mAddContact.accept(contact);
                    binding.layoutRootCardNewChat.setBackgroundColor(
                            res.getColor(R.color.card_select, null));
                } else {
                    selectedContacts.remove(contact);
                    binding.layoutRootCardNewChat.setBackgroundColor(
                            res.getColor(R.color.card_color, null));
                    mRemoveContact.accept(contact);
                }
            });

            binding.textContactNameNewChat.setTextColor(
                    res.getColor(R.color.card_main_text, null));
            binding.textContactUserNameNewChat.setTextColor(
                    res.getColor(R.color.card_main_text, null));

             binding.textContactNameNewChat.setText(contact.getFirstName());
             binding.textContactUserNameNewChat.setText(contact.getUsername());
        }
    }
}

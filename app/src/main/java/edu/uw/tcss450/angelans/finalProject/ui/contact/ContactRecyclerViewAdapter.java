package edu.uw.tcss450.angelans.finalProject.ui.contact;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;

/**
 * The RecyclerView that displays Contacts
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {
    private final List<ContactList> mContactList;

    /**
     * Constructor for ContactRecyclerViewAdapter
     *
     * @param contactLists The list of the user's contacts to display
     */
    public ContactRecyclerViewAdapter(List<ContactList> contactLists) {
        mContactList = contactLists;
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        ContactList contactList = mContactList.get(position);
        holder.mName.setText(contactList.getmName());
        holder.mUsername.setText(contactList.getmUsername());
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        public TextView mName;

        public ContactViewHolder(View view) {
            super(view);
            mUsername = view.findViewById(R.id.text_contact_username);
            mName = view.findViewById(R.id.text_contact_name);
        }
    }

}

package edu.uw.tcss450.angelans.finalProject.ui.contact;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;

/**
 * The RecyclerView that displays Contacts
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> implements Filterable {
    private final List<ContactList> mContactList;
    private List<ContactList> mContactListFilter;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Constructor for ContactRecyclerViewAdapter
     *
     * @param contactLists The list of the user's contacts to display
     */
    public ContactRecyclerViewAdapter(List<ContactList> contactLists) {
        mContactList = contactLists;
        mContactListFilter = new ArrayList<>(contactLists);
    }


    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_card, parent, false);
        ContactViewHolder vh = new ContactViewHolder(view, mListener);
        return vh;
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

    /**
     * Filter the existing contact list (for searching)
     * @return the filtered list
     */

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ContactList> filteredList = new ArrayList<>();
            //If input is empty
            if(charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(mContactListFilter);
            }
            else {
                //Take the input (not case sensitive and remove all the spaces)
                String filterPattern = charSequence.toString().toLowerCase().trim();
                //Search through the list
                for(ContactList item : mContactListFilter) {
                    if(item.getmName().toLowerCase().contains(filterPattern)
                        || item.getmUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mContactList.clear();
            mContactList.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        public TextView mName;
        public ImageView mDelete;

        public ContactViewHolder(View view, OnItemClickListener listener) {
            super(view);
            mUsername = view.findViewById(R.id.text_contact_username);
            mName = view.findViewById(R.id.text_contact_name);
            mDelete = view.findViewById(R.id.button_delete_contact);

            //Delete a contact
            mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition(); //position of the card in this contact list
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }

}

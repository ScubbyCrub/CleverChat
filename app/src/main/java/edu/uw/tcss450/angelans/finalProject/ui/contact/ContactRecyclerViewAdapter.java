package edu.uw.tcss450.angelans.finalProject.ui.contact;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.angelans.finalProject.R;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ContactViewHolder> {

    String username;
    String name;
    int[] avt;

    public ContactRecyclerViewAdapter(int[] avtimage, String Username, String Name) {
        avt = avtimage;
        username = Username;
        name = Name;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {

        holder.mAvt.setImageResource(avt[position]);
        holder.mUsername.setText();
        holder.mName.setText(name.[position]);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ContactViewHolder extends RecyclerView.ViewHolder {
        public ImageView mAvt;
        public TextView mUsername;
        public TextView mName;

        public ContactViewHolder(View view) {
            super(view);
            mAvt = view.findViewById(R.id.image_contact_avt);
            mUsername = view.findViewById(R.id.text_contact_username);
            mName = view.findViewById(R.id.text_contact_name);
        }
    }

}

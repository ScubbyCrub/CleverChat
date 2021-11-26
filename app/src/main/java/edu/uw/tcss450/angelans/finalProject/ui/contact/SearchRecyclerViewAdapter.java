package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder> {
    private final List<SearchList> mSearchList;

    public SearchRecyclerViewAdapter(List<SearchList> searchLists) {
        this.mSearchList = searchLists;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_search_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        SearchList searchList = mSearchList.get(position);
        holder.mName.setText(searchList.getmName());
        holder.mUsername.setText(searchList.getmUsername());
    }

    @Override
    public int getItemCount() {
        return mSearchList.size();
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        public TextView mName;

        public SearchViewHolder(View view) {
            super(view);
            mUsername = view.findViewById(R.id.text_search_username);
            mName = view.findViewById(R.id.text_search_name);
        }
    }

}

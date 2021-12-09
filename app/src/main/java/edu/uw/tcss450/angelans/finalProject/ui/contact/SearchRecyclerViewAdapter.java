package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;

public class SearchRecyclerViewAdapter extends RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder> {
    private final List<SearchList> mSearchList;
    private OnSearchItemClickListener mListener;

    public interface OnSearchItemClickListener {
        void onRequestClick(int position);
    }

    public void setOnSearchClickListener(OnSearchItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Constructor for SearchRecyclerViewAdapter
     *
     * @param searchLists The list of the user's contacts to display
     */
    public SearchRecyclerViewAdapter(List<SearchList> searchLists) {
        this.mSearchList = searchLists;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_search_card, parent, false);
        SearchViewHolder vh = new SearchViewHolder(view, mListener);
        return vh;
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

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        public TextView mName;
        public Button mRequest;

        public SearchViewHolder(View view, OnSearchItemClickListener listener) {
            super(view);
            mUsername = view.findViewById(R.id.text_search_username);
            mName = view.findViewById(R.id.text_search_name);
            mRequest = view.findViewById(R.id.button_search_add);

            //Send request
            mRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onRequestClick(position);
                        }
                    }
                }
            });
        }
    }

}

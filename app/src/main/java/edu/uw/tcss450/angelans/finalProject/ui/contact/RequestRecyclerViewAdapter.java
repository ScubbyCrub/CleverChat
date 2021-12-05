package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;

public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder> {
    private final List<RequestList> mRequestList;
    private OnRequestItemClickListener mListener;

    public interface OnRequestItemClickListener {
        void onAnswerClick(int position);
    }

    public void setOnRequestClickListener(OnRequestItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Constructor for RequestRecyclerViewAdapter
     *
     * @param requestLists The list of the user's friend request to display
     */
    public RequestRecyclerViewAdapter(List<RequestList> requestLists) {
        this.mRequestList = requestLists;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int ViewType) {
        return new RequestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_request_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestList requestList = mRequestList.get(position);
        holder.mName.setText(requestList.getmName());
        holder.mUsername.setText(requestList.getmUsername());
    }

    @Override
    public int getItemCount() {
        return mRequestList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        public TextView mUsername;
        public TextView mName;

        public RequestViewHolder(View view) {
            super(view);
            mUsername = view.findViewById(R.id.text_request_username);
            mName = view.findViewById(R.id.text_request_name);
        }
    }

}

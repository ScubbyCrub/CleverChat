package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;

public class RequestRecyclerViewAdapter extends RecyclerView.Adapter<RequestRecyclerViewAdapter.RequestViewHolder> {
    private final List<RequestList> mRequestList;
    private OnAcceptItemClickListener mAcceptListener;
    private OnDeclineItemClickListener mDeclineListener;

    public interface OnAcceptItemClickListener {
        void onAcceptClick(int position);
    }

    public void setAcceptClickListener(OnAcceptItemClickListener listener) {
        mAcceptListener = listener;
    }


    public interface OnDeclineItemClickListener {
        void onDeclineClick(int position);
    }

    public void setDeclineClickListener(OnDeclineItemClickListener listener) {
        mDeclineListener = listener;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_contact_request_card, parent, false);
        RequestViewHolder vh = new RequestViewHolder(view, mAcceptListener, mDeclineListener);
        return vh;
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
        public ImageButton mAccept;
        public ImageButton mDecline;

        public RequestViewHolder(View view, OnAcceptItemClickListener accept, OnDeclineItemClickListener decline) {
            super(view);
            mUsername = view.findViewById(R.id.text_request_username);
            mName = view.findViewById(R.id.text_request_name);
            mAccept = view.findViewById(R.id.button_accept);
            mDecline = view.findViewById(R.id.button_decline);

            //Accept request
            mAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(accept != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            accept.onAcceptClick(position);
                        }
                    }
                }
            });

            //Decline request
            mDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(decline != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            decline.onDeclineClick(position);
                        }
                    }
                }
            });
        }
    }

}

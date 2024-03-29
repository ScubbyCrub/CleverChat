package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.shape.CornerFamily;

import java.util.List;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentSingleChatMessageBinding;

/**
 * Class that manages the RecyclerView of a single chat room.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class SingleChatRecyclerViewAdapter
        extends RecyclerView.Adapter<SingleChatRecyclerViewAdapter.MessageViewHolder> {

    private final int CHAT_FULL_OPACITY_ALPHA = 255;

    private final List<SingleChatMessage> mMessages;
    private final String mEmail;

    /**
     * Constructor for SingleChatRecyclerViewAdapter.
     *
     * @param theMessages The list of messages for the single chatroom.
     * @param theEmail The email of the current user.
     */
    public SingleChatRecyclerViewAdapter(List<SingleChatMessage> theMessages,
                                         String theEmail) {
        this.mMessages = theMessages;
        mEmail = theEmail;
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup theParent,
                                                int theViewType) {
        return new MessageViewHolder(LayoutInflater
                .from(theParent.getContext())
                .inflate(R.layout.fragment_single_chat_message, theParent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder theHolder, int thePosition) {
        theHolder.setMessage(mMessages.get(thePosition));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private FragmentSingleChatMessageBinding mBinding;

        public MessageViewHolder(@NonNull View theView) {
            super(theView);
            mView = theView;
            mBinding = FragmentSingleChatMessageBinding.bind(theView);
        }

        void setMessage(final SingleChatMessage theMessage) {
            final Resources res = mView.getContext().getResources();
            final MaterialCardView card = mBinding.cardRoot;

            int standard = (int) res.getDimension(R.dimen.chat_margin);
            int extended = (int) res.getDimension(R.dimen.chat_margin_sided);

            if (mEmail.equals(theMessage.getSender())) {
                //This message is from the user. Format it as such
                mBinding.textMessage.setText(theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();
                //Set the left margin
                layoutParams.setMargins(extended, standard, standard, standard);
                // Set this View to the right (end) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.END;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                            res.getColor(R.color.meassage_send, null),
                                CHAT_FULL_OPACITY_ALPHA));
                mBinding.textMessage.setTextColor(
                        res.getColor(R.color.text_color, null));

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(ColorUtils.setAlphaComponent(
                        res.getColor(R.color.meassage_send, null),
                        CHAT_FULL_OPACITY_ALPHA));

                //Round the corners on the left side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCornerSize(0)
                                .setTopRightCornerSize(0)
                                .build());

                card.requestLayout();
            } else {
                //This message is from another user. Format it as such
                mBinding.textMessage.setText(theMessage.getSender().split("@")[0] +
                        ": " + theMessage.getMessage());
                ViewGroup.MarginLayoutParams layoutParams =
                        (ViewGroup.MarginLayoutParams) card.getLayoutParams();

                //Set the right margin
                layoutParams.setMargins(standard, standard, extended, standard);
                // Set this View to the left (start) side
                ((FrameLayout.LayoutParams) card.getLayoutParams()).gravity =
                        Gravity.START;

                card.setCardBackgroundColor(
                        ColorUtils.setAlphaComponent(
                                res.getColor(R.color.message_receive, null),
                                CHAT_FULL_OPACITY_ALPHA));

                card.setStrokeWidth(standard / 5);
                card.setStrokeColor(ColorUtils.setAlphaComponent(
                        res.getColor(R.color.message_receive, null),
                        CHAT_FULL_OPACITY_ALPHA));

                mBinding.textMessage.setTextColor(
                        res.getColor(R.color.text_color, null));

                //Round the corners on the right side
                card.setShapeAppearanceModel(
                        card.getShapeAppearanceModel()
                                .toBuilder()
                                .setTopRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomRightCorner(CornerFamily.ROUNDED,standard * 2)
                                .setBottomLeftCornerSize(0)
                                .setTopLeftCornerSize(0)
                                .build());
                card.requestLayout();
            }
        }
    }
}

//package edu.uw.tcss450.angelans.finalProject.ui.chat;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.RecyclerView;
//
//import edu.uw.tcss450.angelans.finalProject.R;
//import edu.uw.tcss450.angelans.finalProject.databinding.FragmentChatBinding;
//
///**
// * Chat Fragment to allow for UI elements to function when the user is interacting with
// * a chat room.
// *
// * @author Group 6: Teresa, Vlad, Tien, Angela
// * @version Sprint 1
// */
//public class ChatFragment extends Fragment {
////    private FragmentChatBinding mBinding;
////
////    @Override
////    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
////                             Bundle theSavedInstanceState) {
////
////        View view = theInflater.inflate(R.layout.fragment_chat, theContainer, false);
////        if (view instanceof RecyclerView) {
////            //Try out a grid layout to achieve rows AND columns. Adjust the widths of the
////            //cards on display
//////            ((RecyclerView) view).setLayoutManager(new GridLayoutManager(getContext(), 2));
////
////            //Try out horizontal scrolling. Adjust the widths of the card so that it is
////            //obvious that there are more cards in either direction. i.e. don't have the cards
////            //span the entire witch of the screen. Also, when considering horizontal scroll
////            //on recycler view, ensure that thre is other content to fill the screen.
//////            ((LinearLayoutManager)((RecyclerView) view).getLayoutManager())
//////                    .setOrientation(LinearLayoutManager.HORIZONTAL);
////
//////            mBinding = FragmentChatBinding.inflate(theInflater);
////        }
////        ((RecyclerView) view).setAdapter(
////                new ChatRecyclerViewAdapter(ChatGenerator.getBlogList()));
////        // Inflate the layout for this fragment
//////        return mBinding.getRoot();
////        return mBinding.getRoot();
//    }
//
//
//}

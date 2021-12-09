package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactListBinding;
import edu.uw.tcss450.angelans.finalProject.model.NewMessageCountViewModel;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.ui.auth.register.RegisterViewModel;

/**
 * Contact Fragment to allow for UI elements to function when the user is interacting with
 * their contacts page.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class ContactListFragment extends Fragment {

    private ContactListViewModel mContactListViewModel;
    private RequestViewModel mRequestViewModel;
    private UserInfoViewModel mUserModel;
    private NewMessageCountViewModel mNewMessageCountViewModel;

    /**
     * Constructor for ContactListFragment
     */
    public ContactListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mContactListViewModel = provider.get(ContactListViewModel.class);
        mRequestViewModel = provider.get(RequestViewModel.class);
        mContactListViewModel.getContactList(mUserModel.getEmail(), mUserModel.getmJwt());
        mRequestViewModel.getRequestList(mUserModel.getEmail(), mUserModel.getmJwt());
        mNewMessageCountViewModel = provider.get(NewMessageCountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_contact_list, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
//        SharedPreferences prefs =
//                getActivity().getSharedPreferences(
//                        getString(R.string.keys_shared_prefs),
//                        Context.MODE_PRIVATE);
        FragmentContactListBinding binding = FragmentContactListBinding.bind(getView());

        mContactListViewModel.addContactListObserver(mUserModel.getEmail(),getViewLifecycleOwner(), contactList -> {
            if (!contactList.isEmpty()) {
                final RecyclerView rv = binding.listContact;
                ContactRecyclerViewAdapter adapter = new ContactRecyclerViewAdapter(contactList);
                rv.setAdapter(adapter);
                rv.getAdapter().notifyDataSetChanged();


                //Delete a contact
                adapter.setOnItemClickListener(new ContactRecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onDeleteClick(int position) {
                        String username = contactList.get(position).getmUsername();
                        mContactListViewModel.deleteContact(mUserModel.getEmail(), username, mUserModel.getmJwt());
                        contactList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                });

                //Search through the existing list
                binding.searchViewExistContact.setImeOptions(EditorInfo.IME_ACTION_DONE);
                binding.searchViewExistContact.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String input) {
                        adapter.getFilter().filter(input);
                        return false;
                    }
                });
            }
        });

        mRequestViewModel.addRequestListObserver(mUserModel.getEmail(),getViewLifecycleOwner(), requestList -> {
            if (!requestList.isEmpty()) {
                final RecyclerView rv = binding.listRequest;
                RequestRecyclerViewAdapter adapter = new RequestRecyclerViewAdapter(requestList);
                rv.setAdapter(adapter);
                rv.getAdapter().notifyDataSetChanged();


                //Accept a request
                adapter.setAcceptClickListener(new RequestRecyclerViewAdapter.OnAcceptItemClickListener() {
                    @Override
                    public void onAcceptClick(int position) {
                        String username = requestList.get(position).getmUsername();
                        int answer = 1;
                        mRequestViewModel.responseRequest(mUserModel.getEmail(), username, answer, mUserModel.getmJwt());
                        requestList.remove(position);
                        adapter.notifyItemRemoved(position);
                        //Pop up message dialog
                        Dialog dialog = new Dialog("accepted", username);
                        dialog.show(getActivity().getSupportFragmentManager(), "accept dialog");
                    }
                });

                //Decline a request
                adapter.setDeclineClickListener(new RequestRecyclerViewAdapter.OnDeclineItemClickListener() {
                    @Override
                    public void onDeclineClick(int position) {
                        String username = requestList.get(position).getmUsername();
                        int answer = 0;
                        mRequestViewModel.responseRequest(mUserModel.getEmail(), username, answer, mUserModel.getmJwt());
                        requestList.remove(position);
                        adapter.notifyItemRemoved(position);
                        //Pop up message dialog
                        Dialog dialog = new Dialog("declined", username);
                        dialog.show(getActivity().getSupportFragmentManager(), "decline dialog");
                    }
                });
            }
        });

        mNewMessageCountViewModel.addContactCountObserver(getViewLifecycleOwner(), count -> {
            if (count > 0) {
                String countString = count.toString();
                if (count > 9) {
                    countString = "9+";
                }
                binding.buttonContactRequest.setText("Request (" + countString + ")");
            }
        });

        //Navigate to search page
        binding.buttonSearchContact.setOnClickListener(button ->
                Navigation.findNavController(getView()).navigate(
                        ContactListFragmentDirections.actionNavigationContactToNavigationSearch()
                ));

        //Switching between friend and request pages
        //Request
        binding.buttonContactRequest.setOnClickListener(new View.OnClickListener() {
            NewMessageCountViewModel mNewMessageModel = new ViewModelProvider(getActivity())
                    .get(NewMessageCountViewModel.class);
            @Override
            public void onClick(View view) {

                //Switch screen
                binding.viewTheFriend.setVisibility(View.GONE);
                binding.viewTheRequest.setVisibility(View.VISIBLE);
                //Change the colors of 2 buttons
                binding.buttonContactFriend.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.button_contact_switch_inactive));
                binding.buttonContactRequest.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.button_contact_switch));
                // Reset contact notifications
                mNewMessageModel.resetContactCount();
                binding.buttonContactRequest.setText("Request");
            }
        });
        //Friend
        binding.buttonContactFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Switch screen
                binding.viewTheRequest.setVisibility(View.GONE);
                binding.viewTheFriend.setVisibility(View.VISIBLE);
                //Change the colors of 2 buttons
                binding.buttonContactRequest.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.button_contact_switch_inactive));
                binding.buttonContactFriend.setBackgroundColor(ContextCompat.getColor(getActivity(),R.color.button_contact_switch));

            }
        });
    }
}

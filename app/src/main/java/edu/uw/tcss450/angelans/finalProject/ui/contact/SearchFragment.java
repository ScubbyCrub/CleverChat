package edu.uw.tcss450.angelans.finalProject.ui.contact;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.databinding.FragmentContactSearchBinding;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.utils.PasswordValidator;

public class SearchFragment extends Fragment {

    private UserInfoViewModel mUserModel;
    private SearchViewModel mSearchViewModel;

    //Search contains at least 1 character
    private PasswordValidator mSearchCheck = PasswordValidator.checkPWLength(1);

    /**
     * Search Fragment constructor.
     */
    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());
        mUserModel = provider.get(UserInfoViewModel.class);
        mSearchViewModel = provider.get(SearchViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater theInflater, ViewGroup theContainer,
                             Bundle theSavedInstanceState) {
        // Inflate the layout for this fragment
        return theInflater.inflate(R.layout.fragment_contact_search, theContainer, false);
    }

    @Override
    public void onViewCreated(@NonNull View theView, @Nullable Bundle theSavedInstanceState) {
        super.onViewCreated(theView, theSavedInstanceState);
        FragmentContactSearchBinding mBinding = FragmentContactSearchBinding.bind(getView());
        mBinding.buttonSearch.setOnClickListener(this::attemptSearch);
    }


    public void attemptSearch(final View button) {
        FragmentContactSearchBinding mBinding = FragmentContactSearchBinding.bind(getView());
        mSearchCheck.processResult(mSearchCheck.apply(mBinding.textInputSearch.getText().toString().trim()),
                this::verifyWithServer,
                result -> mBinding.textInputSearch.setError("Please enter email, username or name of the user"));
    }

    public void verifyWithServer() {
        FragmentContactSearchBinding mBinding = FragmentContactSearchBinding.bind(getView());
        mSearchViewModel.getSearchList(mBinding.textInputSearch.getText().toString(), mUserModel.getmJwt());
        final RecyclerView rv = mBinding.listContactSearch;
        SearchRecyclerViewAdapter adapter = new SearchRecyclerViewAdapter(
                mSearchViewModel.getSearchListByInput(mBinding.textInputSearch.getText().toString())
        );
        rv.setAdapter(adapter);
        mSearchViewModel.addSearchObserver(
                mBinding.textInputSearch.getText().toString(),
                getViewLifecycleOwner(),
                searchList -> {
                    if(!searchList.isEmpty()) {
                        rv.getAdapter().notifyDataSetChanged();
                    }
                }
        );
    }
}

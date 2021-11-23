package edu.uw.tcss450.angelans.finalProject.model;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

/**
 * ViewModel that manages counting new, unseen message notification while the user's app is
 * minimized.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class NewMessageCountViewModel extends ViewModel {
    private MutableLiveData<Integer> mNewMessageCount;

    /**
     * Constructor for NewMessageCountViewModel
     */
    public NewMessageCountViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(0);
    }

    /**
     * Register as an observer to listen for the new message counter.
     *
     * @param owner the fragment's lifecycle owner.
     * @param observer the observer that wants to listen to updates to new, unseen message counts.
     */
    public void addMessageCountObserver(@NonNull LifecycleOwner owner,
                                        @NonNull Observer<? super Integer> observer) {
        mNewMessageCount.observe(owner, observer);
    }

    /**
     * Increments the count of unseen new messages received by the user.
     */
    public void increment() {
        mNewMessageCount.setValue(mNewMessageCount.getValue() + 1);
    }

    /**
     * Resets the count of unseen new messages received by the user.
     */
    public void reset() {
        mNewMessageCount.setValue(0);
    }
}

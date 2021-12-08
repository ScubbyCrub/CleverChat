package edu.uw.tcss450.angelans.finalProject.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;

/**
 * ViewModel that manages counting new, unseen message notification while the user's app is
 * minimized.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class NewMessageCountViewModel extends ViewModel {
    private MutableLiveData<Map<Integer,Integer>> mNewMessageCount;

    /**
     * Constructor for NewMessageCountViewModel
     */
    public NewMessageCountViewModel() {
        mNewMessageCount = new MutableLiveData<>();
        mNewMessageCount.setValue(new HashMap<Integer, Integer>());
    }

    /**
     * Register as an observer to listen for the new message counter.
     *
     * @param theOwner the fragment's lifecycle owner.
     * @param theObserver the observer that wants to listen to updates to new, unseen
     *                    message counts.
     */
    public void addMessageCountObserver(@NonNull LifecycleOwner theOwner,
                                        @NonNull Observer<? super Map<Integer, Integer>> theObserver) {
        mNewMessageCount.observe(theOwner, theObserver);
    }

    /**
     * Increments the count of unseen new messages received by the user.
     * @param chatid
     */
    public void increment(int chatid) {
        if (chatid <= -1) {
            Log.d("NewMessageCountViewModel", "Passed ChatID default (-1) or invalid");
        } else {
            // If chat already as unread messages, increment the key (chatID)'s counter value
            if (mNewMessageCount.getValue().containsKey(chatid)) {
                int currentChatCount = mNewMessageCount.getValue().get(chatid);
                currentChatCount ++;
                mNewMessageCount.getValue().replace(chatid, currentChatCount);
                Log.d("NewMessageCountViewModel", "mNewMessageCount incremented (existing chat)"
                        + chatid + " -> " + mNewMessageCount.getValue().get(chatid));
            } else { // If a newly tracked chat got its first unread message, add it to map
                mNewMessageCount.getValue().putIfAbsent(chatid, 1);
                Log.d("NewMessageCountViewModel", "mNewMessageCount incremented (new chat)"
                        + chatid + " -> " + mNewMessageCount.getValue().get(chatid));
            }
        }
        // Tell observer a change happened.
        mNewMessageCount.setValue(mNewMessageCount.getValue());
    }

    /**
     * Resets the count of unseen new messages received by the user.
     */
    public void reset() {
        mNewMessageCount.getValue().clear();
        // Tell observer change happened
        mNewMessageCount.setValue(mNewMessageCount.getValue());
    }
}

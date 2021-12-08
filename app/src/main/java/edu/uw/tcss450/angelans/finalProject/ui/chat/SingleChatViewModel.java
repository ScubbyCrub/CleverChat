package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;

/**
 * ViewModel that stores the information of a single chat room.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class SingleChatViewModel extends AndroidViewModel {

    /**
     * A Map of Lists of Chat Messages.
     * The Key represents the Chat ID
     * The value represents the List of (known) messages for that that room.
     */
    private Map<Integer, MutableLiveData<List<SingleChatMessage>>> mMessages;
    private MutableLiveData<Integer> mCurrentChatID;

    public SingleChatViewModel(@NonNull Application theApplication) {
        super(theApplication);
        mMessages = new HashMap<>();
        mCurrentChatID = new MutableLiveData<>();
        mCurrentChatID.setValue(-1);
    }

    /**
     * Register as an observer to listen to a specific chat room's list of messages.
     *
     * @param theChatId The chat ID of the chat given to the observer.
     * @param theOwner The fragment's lifecycle owner.
     * @param theObserver The observer that wants to see updates to a specific chatroom.
     */
    public void addMessageObserver(int theChatId,
                                   @NonNull LifecycleOwner theOwner,
                                   @NonNull Observer<? super List<SingleChatMessage>>
                                           theObserver) {
        getOrCreateMapEntry(theChatId).observe(theOwner, theObserver);
    }

    public void updateMostRecentVisitedChatID(int theNewID) {
        mCurrentChatID.setValue(theNewID);
    }

    public void addMostRecentChatIDObserver(@NonNull LifecycleOwner theOwner,
                                            @NonNull Observer<Integer> theObserver) {
        mCurrentChatID.observe(theOwner, theObserver);
    }

    /**
     * Return a reference to the List<> associated with the chat room. If the View Model does
     * not have a mapping for this chatID, it will be created.
     *
     * @param theChatId The ID of the chat room List to retrieve.
     * @return A reference to the list of messages.
     */
    public List<SingleChatMessage> getMessageListByChatId(final int theChatId) {
        return getOrCreateMapEntry(theChatId).getValue();
    }

    private MutableLiveData<List<SingleChatMessage>> getOrCreateMapEntry(final int theChatId) {
        if(!mMessages.containsKey(theChatId)) {
            mMessages.put(theChatId, new MutableLiveData<>(new ArrayList<>()));
        }
        return mMessages.get(theChatId);
    }

    /**
     * Makes a request to the web service to get the first batch of messages for a given
     * Chat Room.
     * Parses the response and adds the ChatMessage object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent requests to the web service for a given chat room should be made from
     * getNextMessages()
     *
     * @param theChatId The chatroom ID to request messages of.
     * @param theJwt The users signed JWT.
     */
    public void getFirstMessages(final int theChatId, final String theJwt) {
        Log.d("SingleChatViewModel",
                "(getFirstMessages) message POST called by /api/messages/chatId");
        String url = getApplication().getResources().getString(R.string.base_url) +
                "messages/" + theChatId;

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", theJwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);

        //code here will run
    }

    /**
     * Makes a request to the web service to get the next batch of messages for a given Chat Room.
     * This request uses the earliest known ChatMessage in the associated list and passes that
     * messageId to the web service.
     * Parses the response and adds the ChatMessage object to the List associated with the
     * ChatRoom. Informs observers of the update.
     *
     * Subsequent calls to this method receive earlier and earlier messages.
     *
     * @param theChatId The chatroom id to request messages of.
     * @param theJwt The users signed JWT.
     */
    public void getNextMessages(final int theChatId, final String theJwt) {
        Log.d("SingleChatViewModel",
                "(getNextMessages) message POST called by /api/messages/chatId");
        String url = getApplication().getResources().getString(R.string.base_url) +
                "messages/" +
                theChatId +
                "/" +
                mMessages.get(theChatId).getValue().get(0).getMessageId();

        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleSuccess,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", theJwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);

        //code here will run
    }

    /**
     * When a chat message is received externally to this ViewModel, add it
     * with this method.
     *
     * @param theChatId The chat's unique ID that the message should be included in.
     * @param theMessage The content of the sent message.
     */
    public void addMessage(final int theChatId, final SingleChatMessage theMessage) {
        List<SingleChatMessage> list = getMessageListByChatId(theChatId);
        list.add(theMessage);
        getOrCreateMapEntry(theChatId).setValue(list);
    }

    /**
     * Handler if a web response to interact with messages in a single chat room is successful.
     *
     * @param theResponse The response from the web service.
     */
    private void handleSuccess(final JSONObject theResponse) {
        List<SingleChatMessage> list;
        if (!theResponse.has("chatId")) {
            throw new IllegalStateException("Unexpected response in ChatViewModel: "
                    + theResponse);
        }
        try {
            list = getMessageListByChatId(theResponse.getInt("chatId"));
            JSONArray messages = theResponse.getJSONArray("rows");
            for(int i = 0; i < messages.length(); i++) {
                JSONObject message = messages.getJSONObject(i);
                SingleChatMessage cMessage = new SingleChatMessage(
                        message.getInt("messageid"),
                        message.getString("message"),
                        message.getString("email"),
                        message.getString("timestamp")
                );
                if (!list.contains(cMessage)) {
                    // don't add a duplicate
                    list.add(0, cMessage);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("Chat message already received",
                            "Or duplicate id:" + cMessage.getMessageId());
                }

            }
            //inform observers of the change (setValue)
            getOrCreateMapEntry(theResponse.getInt("chatId")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    /**
     * If the web service responds with a Volley error, handle it here.
     *
     * @param theError The Volley error from the web service.
     */
    private void handleError(final VolleyError theError) {
        if (Objects.isNull(theError.networkResponse)) {
            Log.e("NETWORK ERROR", theError.getMessage());
        }
        else {
            String data = new String(theError.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    theError.networkResponse.statusCode +
                    " " +
                    data);
        }
    }
}

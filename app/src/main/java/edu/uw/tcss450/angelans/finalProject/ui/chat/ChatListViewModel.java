package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.app.Application;
import android.content.SharedPreferences;
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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.model.Chat;

/**
 * View model used to store data about the Chat List Fragment
 * @Author Vlad Tregubov
 * @version 1
 */
public class ChatListViewModel extends AndroidViewModel {
    //set up the data list we are going to be using
    private MutableLiveData<List<Chat>> mChatList;

    /**
     * Constructor for view model
     * @param application the application
     */
    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatList = new MutableLiveData<>();
        mChatList.setValue(new ArrayList<>());
    }

    /**
     * Add listener for when the chat list data is recieved
     * @param owner owner of the observer
     * @param observer The data type we are returning on trigger of observer
     */
    public void addChatListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Chat>> observer) {
        mChatList.observe(owner, observer);
    }

    /**
     * Handles error scenario of the request for chats
     * @param error the volley error
     */
    private void handleError(final
                             VolleyError error) {
        //TODO: Make better error handling
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    /**
     * Handle success scenario of the request to data
     * @param result the json data recieved from the request
     */
    private void handleResult(final JSONObject result) {
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            if (root.has("chats")) { //TODO move these hardcoded strings to separate file
                //This user is a part of at least one chat
                JSONArray response =
                        root.getJSONArray("chats");
                    for(int i = 0; i < response.length(); i++) {
                        JSONObject jsonChat = response.getJSONObject(i);
                        Chat chat = new Chat(jsonChat.getString("name"), jsonChat.getInt("chatid"));
                        boolean contains = false;
                        for(Chat c : mChatList.getValue()){
                            if(c.getId() == chat.getId()){
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            mChatList.getValue().add(0,chat);
                        }
                }
            } else {
                Log.e("ERROR!", "No response");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }

        mChatList.setValue(mChatList.getValue());
    }

    /**
     * Make request to backend to retrieve a users chats
     * @param jwt JWT auth token for user
     */
    public void connectGet(String jwt) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl+ "chat"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                //TODO: Replace this to use the actual jwt stored in the app
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    public void deleteChat(Chat chat, String jwt){
        //remove from the view model
        mChatList.getValue().remove(chat);
        mChatList.setValue(mChatList.getValue());
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl+ "chat/" + chat.getId(); //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null, //no body for this get request
                this::handleDeleteResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                //TODO: Replace this to use the actual jwt stored in the app
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
    public void handleDeleteResult(JSONObject result){
        System.out.println(result.toString());
    }
}

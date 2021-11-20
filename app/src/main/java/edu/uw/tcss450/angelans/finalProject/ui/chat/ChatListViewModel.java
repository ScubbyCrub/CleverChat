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

public class ChatListViewModel extends AndroidViewModel {
    //set up the data list we are going to be using
    private MutableLiveData<List<Chat>> mChatList;

    public ChatListViewModel(@NonNull Application application) {
        super(application);
        mChatList = new MutableLiveData<>();
        mChatList.setValue(new ArrayList<>());
    }
    //add observer to the chatlist
    public void addChatListObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super List<Chat>> observer) {
        mChatList.observe(owner, observer);
    }
    private void handleError(final
                             VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        //TODO: Make better error handling
        Log.e("CONNECTION ERROR", error.getLocalizedMessage());
        throw new IllegalStateException(error.getMessage());
    }

    private void handleResult(final JSONObject result) {
//        mChatList.
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = result;
            System.out.println(root.toString());
            if (root.has("chats")) { //TODO move these hardcoded strings to separate file
//                //This user is a part of at least one chat
                JSONArray response =
                        root.getJSONArray("chats");
                    for(int i = 0; i < response.length(); i++) {
                        JSONObject jsonChat = response.getJSONObject(i);
                        Chat chat = new Chat(jsonChat.getString("name"), jsonChat.getInt("chatid"));
                        //TODO FIGURE OUT WHY THIS ADDS DUPLICATES...
//
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
            System.out.println("stuff");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
        }

        mChatList.setValue(mChatList.getValue());
    }
    public void connectGet(String jwt) {

        String url =
                "http://10.0.2.2:5000/api/chat"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
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
                headers.put("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRoZXRhYmxldGd1eTIuMEBnbWFpbC5jb20iLCJtZW1iZXJpZCI6MTEsImlhdCI6MTYzNzA0MjQxNywiZXhwIjoxNjQ1NjgyNDE3fQ.jtb2T3ARPEV_4yK14gVh-rGeL0d9eTldceK3iUPmgSg");
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
}

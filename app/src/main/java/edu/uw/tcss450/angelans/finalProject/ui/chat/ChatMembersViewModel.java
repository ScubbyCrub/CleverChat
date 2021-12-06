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
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

public class ChatMembersViewModel extends AndroidViewModel {
    private MutableLiveData<List<Contact>> mContactList;
    public ChatMembersViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>();
        mContactList.setValue(new ArrayList<>());

    }

    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer){
        mContactList.observe(owner,observer);
    }

    public void getChatMembers(String jwt, String chatid) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl+ "chat/members/" + chatid; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
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

    public void handleError(final VolleyError error){
        Log.e("Error", "handleError: " + error.toString());
    }

    public void handleResult(final JSONObject data){
        Log.i("Got data", "handleResult: " + data.toString());
        try {
            if (data.has("members")) {
                //the data is present
                JSONArray members = data.getJSONArray("members");
                for(int i = 0; i < members.length(); i++){
                    JSONObject current = members.getJSONObject(i);
                    System.out.println(current.toString());
                    Contact curContact = new Contact(
                            current.getInt("memberid"),
                            current.getString("email"),
                            current.getString("username"),
                            current.getString("username"), // TODO Update with nickname
                            current.getString("firstname"),
                            current.getString("lastname")
                    );
                    mContactList.getValue().add(0,curContact);

                }
                mContactList.setValue(mContactList.getValue());
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

}

package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.app.Application;
import android.content.MutableContextWrapper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.angelans.finalProject.model.Chat;

public class NewChatViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mStatus;
    public NewChatViewModel(@NonNull Application application) {
        super(application);
        mStatus = new MutableLiveData<>();
        mStatus.setValue(false);
    }

    //add observer
    public void addNewChatObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super Boolean> observer){
        mStatus.observe(owner, observer);
    }
    //make the post request to create the new chat
    private void handleError(final
                             VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        //TODO: Make better error handling
//        Log.e("CONNECTION ERROR", error.getLocalizedMessage(;
        throw new IllegalStateException(error.getMessage());
    }
    public void handleResult(final JSONObject result) {
        //on success navigate to chats fragment
        //TODO: idk get this to navigate to the newly made chat in the end
        System.out.println("new chat has been made!");
        mStatus.setValue(true);
    }
    public void connectPost(final String name, final String[] members, String jwt) {
        //make body
        JSONObject body = new JSONObject();
        try {
            body.put("name",name);
            body.put("members",new JSONArray(members));
        } catch(JSONException e){
            e.printStackTrace();
        }
        String url =
                "http://10.0.2.2:5000/api/chat"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, // <- Praying the string array works
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
}

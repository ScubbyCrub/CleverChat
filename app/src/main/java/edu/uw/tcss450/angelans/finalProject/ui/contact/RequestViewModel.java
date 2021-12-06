package edu.uw.tcss450.angelans.finalProject.ui.contact;

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

public class RequestViewModel extends AndroidViewModel {
    private Map<String, MutableLiveData<List<RequestList>>> mRequestList;

    /**
     * Constructor for RequestViewModel
     *
     * @param application The application the ViewModel exists in
     */
    public RequestViewModel(@NonNull Application application) {
        super(application);
        mRequestList = new HashMap<>();
    }

    /**
     * Adds an observer to the request list to inform the observer of list updates.
     *
     * @param email The current user's email.
     * @param owner The fragment's lifecycle owner
     * @param observer The observer that wants to know when the request list is updated.
     */
    public void addRequestListObserver(String email,
                                       @NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<RequestList>> observer) {
        getOrCreateMapEntry(email).observe(owner, observer);
    }

    /**
     * Getter for receiving a request list of a user based on their email.
     *
     * @param email The user's email
     * @return The user's request list
     */
    public List<RequestList> getRequestListByEMail(final String email) {
        return getOrCreateMapEntry(email).getValue();
    }

    private MutableLiveData<List<RequestList>> getOrCreateMapEntry(final String email) {
        if(!mRequestList.containsKey(email)) {
            mRequestList.put(email, new MutableLiveData<>(new ArrayList<>()));
        }
        return mRequestList.get(email);
    }

    /**
     * Returns the request list of a user from the database
     *
     * @param email The user's email
     * @param jwt The user's JWT token
     */
    public void getRequestList(final String email, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "contact/requestList";

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handelRequestSuccess,
                this::handleRequestError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext()).addToRequestQueue(request);
    }

    /**
     * How to handle a successful return from the web service interaction.
     *
     * @param response The response from the web service.
     */
    private void handelRequestSuccess(final JSONObject response) {
        List<RequestList> list;
        if (!response.has("email")) {
            throw new IllegalStateException("Unexpected response in ContactListViewModel: " + response);
        }
        try {
            list = getRequestListByEMail(response.getString("email"));
            JSONArray requests = response.getJSONArray("rows");
            for(int i = 0; i < requests.length(); i++) {
                JSONObject request = requests.getJSONObject(i);
                RequestList mRequest = new RequestList(
                        request.getString("username"),
                        request.getString("name")
                );
                if (!list.contains(mRequest)) {
                    // don't add a duplicate
                    list.add(0, mRequest);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("list already received",
                            "Or duplicate email:" + mRequest.getmUsername());
                }

            }
            //inform observers of the change (setValue)
            getOrCreateMapEntry(response.getString("email")).setValue(list);
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    /**
     * How to handle a Volley error if one returns from the web service.
     *
     * @param error The Volley error from the web service.
     */
    private void handleRequestError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }

    public void responseRequest(final String myEmail, final String usernameToAnswer, final int answer, final String jwt) {
        String url =  getApplication().getResources().getString(R.string.base_url)  +
                "contact/responseRequest";

        JSONObject body = new JSONObject();
        try {
            body.put("email", myEmail);
            body.put("username", usernameToAnswer);
            body.put("answer", answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleResponseSuccess,
                this::handleResponseError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext()).addToRequestQueue(request);
    }

    /**
     * Display a log for the web service response of responding a friend request
     *
     * @param data The JSON response from the web service
     */
    public void handleResponseSuccess(final JSONObject data){
        Log.d("data", data.toString());
    }

    /**
     * Display a stack trace of if responding a request through the web service threw a Volley error
     *
     * @param error The Volley error from the web service
     */
    public void handleResponseError(final VolleyError error){
        error.printStackTrace();
    }



}

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

/**
 * ViewModel that stores information relevant to displaying contacts beyond the lifecycle
 * of a fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class ContactListViewModel extends AndroidViewModel {

    private Map<String, MutableLiveData<List<ContactList>>> mContactList;

    /**
     * Constructor for ContactListViewModel
     *
     * @param application The application the ViewModel exists in
     */
    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new HashMap<>();
    }

    /**
     * Adds an observer to the contact list to inform the observer of list updates.
     *
     * @param email The current user's email.
     * @param owner The fragment's lifecycle owner
     * @param observer The observer that wants to know when the contacts list is updated.
     */
    public void addContactListObserver(String email,
                                       @NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<ContactList>> observer) {
        getOrCreateMapEntry(email).observe(owner, observer);
    }


    /**
     * Getter for receiving a contact list of a user based on their email.
     *
     * @param email The user's email
     * @return The user's contact list
     */
    public List<ContactList> getContactListByEMail(final String email) {
        return getOrCreateMapEntry(email).getValue();
    }

    /**
     * Map one contact to another contact
     *
     * @param email The user's desired contact to connect with
     * @return A map entry for connecting two contacts
     */
    private MutableLiveData<List<ContactList>> getOrCreateMapEntry(final String email) {
        if(!mContactList.containsKey(email)) {
            mContactList.put(email, new MutableLiveData<>(new ArrayList<>()));
        }
        return mContactList.get(email);
    }


    /**
     * Returns the contact list of a user from the database
     *
     * @param email The user's email
     * @param jwt The user's JWT token
     */
    public void getContactList(final String email, final String jwt) {
       String url = getApplication().getResources().getString(R.string.base_url) +
                 "contact/list";

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
                this::handelSuccess,
                this::handleError) {

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
    private void handelSuccess(final JSONObject response) {
        List<ContactList> list;
        if (!response.has("email")) {
            throw new IllegalStateException("Unexpected response in ContactListViewModel: " + response);
        }
        try {
            list = getContactListByEMail(response.getString("email"));
            JSONArray contacts = response.getJSONArray("rows");
            for(int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                ContactList mContact = new ContactList(
                        contact.getString("username"),
                        contact.getString("name")
                );
                if (!list.contains(mContact)) {
                    // don't add a duplicate
                    list.add(0, mContact);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("list already received",
                            "Or duplicate email:" + mContact.getmUsername());
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
    private void handleError(final VolleyError error) {
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

    /**
     * Adds a contact connection to the database of contacts
     *
     * @param myEmail The user's email
     * @param toAdd The username of the contact to be added
     * @param jwt The user's JWT token
     */
    public void addContact(final String myEmail, final String toAdd, final String jwt) {

        String url =  getApplication().getResources().getString(R.string.base_url)  +
                "contact/add";

        JSONObject body = new JSONObject();
        try {
            body.put("email", myEmail);
            body.put("username", toAdd);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleAddedSuccess,
                this::handleAddedError) {

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
     * Display a log for the web service response of adding a contact
     *
     * @param data The JSON response from the web service
     */
    public void handleAddedSuccess(final JSONObject data){
        Log.d("data", data.toString());
    }

    /**
     * Display a stack trace of if adding a contact through the web service threw a Volley error
     *
     * @param error The Volley error from the web service
     */
    public void handleAddedError(final VolleyError error){
        error.printStackTrace();
    }

    /**
     * Delete a contact connection to the database of contacts
     *
     * @param myEmail The user's email
     * @param toDelete The username of the contact to be deleted
     * @param jwt The user's JWT token
     */
    public void deleteContact(final String myEmail, final String toDelete, final String jwt) {

        String url =  getApplication().getResources().getString(R.string.base_url)  +
                "contact/delete";

        JSONObject body = new JSONObject();
        try {
            body.put("email", myEmail);
            body.put("username", toDelete);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleDeletedSuccess,
                this::handleDeletedError) {

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
     * Display a log for the web service response of deleting a contact
     *
     * @param data The JSON response from the web service
     */
    public void handleDeletedSuccess(final JSONObject data){
        Log.d("data", data.toString());
    }

    /**
     * Display a stack trace of if adding a contact through the web service threw a Volley error
     *
     * @param error The Volley error from the web service
     */
    public void handleDeletedError(final VolleyError error){
        error.printStackTrace();
    }
}

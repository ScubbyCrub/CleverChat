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

import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;

public class ContactListViewModel extends AndroidViewModel {

    private Map<String, MutableLiveData<List<ContactInfo>>> mContactList;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        mContactList = new HashMap<>();
    }

    public void addContactListObserver(String email,
                                       @NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<ContactInfo>> observer) {
        getOrCreateMapEntry(email).observe(owner, observer);
    }

    public List<ContactInfo> getContactListByEMail(final String email) {
        return getOrCreateMapEntry(email).getValue();
    }

    private MutableLiveData<List<ContactInfo>> getOrCreateMapEntry(final String email) {
        if(!mContactList.containsKey(email)) {
            mContactList.put(email, new MutableLiveData<>(new ArrayList<>()));
        }
        return mContactList.get(email);
    }

    public void getContactList(final String email, final String jwt) {
        String url = "http://10.0.2.2:5000/api/contact/list";

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

    private void handelSuccess(final JSONObject response) {
        List<ContactInfo> list;
        if (!response.has("email")) {
            throw new IllegalStateException("Unexpected response in ContactListViewModel: " + response);
        }
        try {
            list = getContactListByEMail(response.getString("email"));
            JSONArray contacts = response.getJSONArray("rows");
            for(int i = 0; i < contacts.length(); i++) {
                JSONObject contact = contacts.getJSONObject(i);
                ContactInfo mContact = new ContactInfo(
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

}

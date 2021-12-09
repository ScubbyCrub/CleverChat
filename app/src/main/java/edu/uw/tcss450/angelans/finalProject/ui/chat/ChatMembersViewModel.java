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
    private MutableLiveData<List<Contact>> mCurrentContacts;
    private String JWT_TOKEN = "";
    private String EMAIL = "";
    public ChatMembersViewModel(@NonNull Application application) {
        super(application);
        mContactList = new MutableLiveData<>();
        mContactList.setValue(new ArrayList<>());

        mCurrentContacts = new MutableLiveData<>();
        mCurrentContacts.setValue(new ArrayList<>());

    }

    public List<Contact> getMemberList(){
        return mContactList.getValue();
    }
    public void setEmail(String email){
        EMAIL = email;
    }
    public void addContactListObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer){
        mContactList.observe(owner,observer);
    }

    public void addCurrentContactsObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super List<Contact>> observer){
        mCurrentContacts.observe(owner,observer);
    }

    public void getChatMembers(String jwt, String chatid) {
        JWT_TOKEN = jwt;
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
        System.out.println(error.getMessage());
    }

    public void handleResult(final JSONObject data){
        Log.i("Got data", "handleResult: " + data.toString());
        if(mContactList.getValue().size() > 0){
            mContactList.setValue(new ArrayList<>());
        }
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
            System.out.println(e.toString());
            e.printStackTrace();
        }
        getCurrentContacts(JWT_TOKEN,EMAIL);
    }
    /**
     * add a member as a contact
     */
    public void addMemberAsContact(String jwt, String yourEmail, String addContactEmail) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        JSONObject body = new JSONObject();
        try {
            body.put("email", yourEmail.trim());
            body.put("username",addContactEmail.trim());
        } catch(JSONException e){
            e.printStackTrace();
        }
        String url =
                baseUrl+ "contact/sendRequest"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleAddResult,
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
    public void handleAddResult(JSONObject data){
        System.out.println(data.toString());
    }

    /**
     * Get all the current contacts of the individual
     */
    public void getCurrentContacts(String jwt, String email) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        JSONObject body = new JSONObject();
        try {
            body.put("email", email.trim());
        } catch(JSONException e){
            e.printStackTrace();
        }
        System.out.println("Getting contacts for: " + email);
        String url =
                baseUrl+ "contact/list"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //no body for this get request
                this::handleGetContactsResult,
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
    public void handleGetContactsResult(JSONObject data){
        System.out.println("Successful! : " + data.toString());
        if(mCurrentContacts.getValue().size() > 0){
            mCurrentContacts.setValue(new ArrayList<>());
        }
        try {
            if (data.has("rows")) {
                //the data is present
                JSONArray members = data.getJSONArray("rows");
                for(int i = 0; i < members.length(); i++){
                    JSONObject current = members.getJSONObject(i);
                    System.out.println(current.toString());
                    Contact curContact = new Contact(
                            current.getInt("memberid"),
                            current.getString("username"),
                            current.getString("username"),
                            "", // TODO Update with nickname
                            "",
                           ""
                    );
                    mCurrentContacts.getValue().add(0,curContact);

                }
                mCurrentContacts.setValue(mCurrentContacts.getValue());
            }
        } catch(JSONException e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public void deleteChatMember(String jwt, String chatid, String chatMemberId) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        JSONObject body = new JSONObject();
        try {
            body.put("chatid", chatid.trim());
            body.put("memberid", chatMemberId.trim());
        } catch(JSONException e){
            e.printStackTrace();
        }
        System.out.println("Removing from chat...");
        String url =
                baseUrl+ "chat/delete"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //no body for this get request
                this::handleMemberRemoval,
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
    public void handleMemberRemoval(JSONObject data){
        System.out.println("Successful! : " + data.toString());

        try {
            if (data.has("message")) {
                //the data is present
                JSONArray removed = data.getJSONArray("message");
                JSONObject removedMember = removed.getJSONObject(0);
                List<Contact> currentContacts = mContactList.getValue();
                List<Contact> updatedContacts = new ArrayList<Contact>();
                for(Contact contact : currentContacts){
                    if(contact.getId() != Integer.parseInt(removedMember.getString("memberid"))){
                        updatedContacts.add(contact);
                    }
                }
                mContactList.setValue(updatedContacts);
                mCurrentContacts.setValue(mCurrentContacts.getValue());
                //TODO REUSE THE CHAT CRTEATEION RECYCLE VIEW TO ADD THINGS TO THE CHAT BUT CHAGNE BEHAVIOUR SLIGHTLY
            }
        } catch(JSONException e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public List<Contact> getChatMembers(){ return mContactList.getValue();}
}

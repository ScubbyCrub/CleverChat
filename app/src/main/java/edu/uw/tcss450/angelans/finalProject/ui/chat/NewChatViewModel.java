package edu.uw.tcss450.angelans.finalProject.ui.chat;

import android.app.Application;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntFunction;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;
import edu.uw.tcss450.angelans.finalProject.model.Chat;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

/**
 * View model that handles the data storage for the new chat fragment
 * @author Vlad Tregubov
 * @version 1
 */
public class NewChatViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mStatus;
    private MutableLiveData<List<Contact>> mSelectedContacts;
    private MutableLiveData<List<Contact>> mContacts;
    private MutableLiveData<Integer> mChatId;

    /**
     * Constructor for the view model
     * @param application the application
     */
    public NewChatViewModel(@NonNull Application application) {
        super(application);
        mStatus = new MutableLiveData<>();
        mSelectedContacts = new MutableLiveData<>();
        mSelectedContacts.setValue(new ArrayList<>());
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
        mChatId = new MutableLiveData<>();
        mChatId.setValue(0);

    }

    /**
     * Add observer to the status data
     * @param owner owner of the obserrver
     * @param observer observer datya type
     */
    public void addNewChatObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super Boolean> observer){
        mStatus.observe(owner, observer);
    }

    /**
     * Add observer to chat selection data
     * @param owner observer owner
     * @param observer observer
     */
    public void addSelectedChatObserver(@NonNull LifecycleOwner owner,
                                   @NonNull Observer<? super List<Contact>> observer){
        mContacts.observe(owner,observer);
    }

    /**
     * Handle any VolleyErrors that come back from web end communication.
     * @param error The Volley error received from web end communication.
     */
    private void handleError(final
                             VolleyError error) {
        //you should add much better error handling in a production release.
        //i.e. YOUR PROJECT
        //TODO: Make better error handling
        Log.e("Error.toString", error.getMessage());
        throw new IllegalStateException(error.getMessage());
    }

    /**
     * Handles the result of a successful request
     * @param result json data objeect with the result
     */
    public void handleResult(final JSONObject result) {
        try{
            mChatId.setValue(result.getInt("chatid"));
        } catch (JSONException e){
            e.printStackTrace();
        }
        mStatus.setValue(true);;
    }

    /**
     *  Create the new chat
     * @param name name of the chat
     * @param jwt the users jwt
     */
    public void connectPost(final String name, String jwt) {

        //make body
        String[] memberIds = new String[mSelectedContacts.getValue().size()];
        for(int i = 0; i < memberIds.length; i++){
            memberIds[i] ="" + mSelectedContacts.getValue().get(i).getId();
        }
        JSONObject body = new JSONObject();
        try {
            body.put("name",name);
            body.put("members",new JSONArray(memberIds));
        } catch(JSONException e){
            e.printStackTrace();
        }
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl + "chat"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
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

    /**
     * Gets a list of users contacts from the database
     * @param email The email of the user
     * @param jwt the jwt of the user
     */
    public void connectGetContacts(final String email, String jwt) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url = baseUrl + "contact/list";

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
                this::handleSuccess,
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
     * Handle a successful input from the web end communication
     * @param response The JSON web response to a web request
     */
    private void handleSuccess(final JSONObject response){
        IntFunction<String> getString =
                getApplication().getResources()::getString;
        try {
            JSONObject root = response;
            System.out.println(root.toString());
            if (root.has("rows")) { //TODO move these hardcoded strings to separate file
//                //This user is a part of at least one chat
                JSONArray contactData =
                        root.getJSONArray("rows");
                for(int i = 0; i < contactData.length(); i++) {
                    JSONObject jsonContact = contactData.getJSONObject(i);
                    Contact aContact = new Contact(jsonContact.getInt("memberid"),
                            jsonContact.getString("username"),
                            jsonContact.getString("name"),
                            jsonContact.getString("username"),
                            jsonContact.getString("name").split(" ")[0],
                            jsonContact.getString("name").split(" ")[1]);
                    boolean contains = false;
                    for(Contact c : mContacts.getValue()){
                        if(c.getId() == aContact.getId()){
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        mContacts.getValue().add(0,aContact);
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

        mContacts.setValue(mContacts.getValue());

    }

    /**
     * adds contact to list of selected contacts
     * @param contact the user's new contact to add
     */
    public void addContact(Contact contact){
        mSelectedContacts.getValue().add(contact);
        Log.d("selected contacts", "addContact: " + mSelectedContacts.getValue().toString());
    }
    /**
     * remove contact to list of selected contacts
     * @param contact the user's contact to remove
     */
    public void removeContact(Contact contact){
        mSelectedContacts.getValue().remove(contact);
        Log.d("selected contacts", "addContact: " + mSelectedContacts.getValue().toString());
    }

    /**
     * get the id of the chat
     * @return id of the chat
     */
    public int getChatId(){
        return mChatId.getValue();
    }
}

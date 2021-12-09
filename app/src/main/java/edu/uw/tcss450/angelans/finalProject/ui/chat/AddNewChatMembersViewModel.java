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
import java.util.function.IntFunction;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

public class AddNewChatMembersViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> mStatus;
    private MutableLiveData<List<Contact>> mSelectedContacts;
    private MutableLiveData<List<Contact>> mContacts;
    private MutableLiveData<Integer> mChatId;
    private MutableLiveData<List<Contact>> mCurrentContacts;
    private String JWT = "";
    private String EMAIL = "";
    private String CHAT_ID = "";

    /**
     * Constructor for the view model
     * @param application the application
     */
    public AddNewChatMembersViewModel(@NonNull Application application){
        super(application);
        mStatus = new MutableLiveData<>();
        mSelectedContacts = new MutableLiveData<>();
        mSelectedContacts.setValue(new ArrayList<>());
        mContacts = new MutableLiveData<>();
        mContacts.setValue(new ArrayList<>());
        mChatId = new MutableLiveData<>();
        mChatId.setValue(0);

        mCurrentContacts = new MutableLiveData<>();
        mCurrentContacts.setValue(new ArrayList<>());
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
     * Gets a list of users contacts from the database
     * @param email The email of the user
     * @param jwt the jwt of the user
     */
    public void connectGetContacts(final String email, String jwt, String chatid) {
        JWT = jwt;
        EMAIL = email;
        CHAT_ID = chatid;
        System.out.println(jwt + " " + email);
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
    private void  handleSuccess(final JSONObject response){
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
                    for(Contact c : mCurrentContacts.getValue()){
                        if(c.getId() == aContact.getId()){
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        mCurrentContacts.getValue().add(0,aContact);
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

        mCurrentContacts.setValue(mCurrentContacts.getValue());
        //call the get current contacts method
        getCurrentChatMembers(JWT);

    }
    /**
     * Get the current contacts of the user
     */
    private void getCurrentChatMembers(String jwt) {
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl+ "chat/members/" + CHAT_ID; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null, //no body for this get request
                this::handleChatMemberResult,
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

    private void handleChatMemberResult(JSONObject jsonObject) {
        List<Contact> currentContacts = mCurrentContacts.getValue();
        if(jsonObject.has("members")){
            try{
                JSONArray members = jsonObject.getJSONArray("members");
                System.out.println(members.length());
                boolean contains = false;
                int toRemove = -1;
                for(int i = 0; i < members.length(); i++){
                    /*
                    Check to see if the member of contacts is already in the room
                    if they are remove them from the list of contact
                    terrible algo but its 3 am so it ll work
                    n^2 run time
                     */
                    JSONObject cur = members.getJSONObject(i);
                    //get the current id
                    int curId = Integer.parseInt(cur.getString("memberid"));
                    //chck if it is already in the chat
                    for(int j = 0; j < currentContacts.size(); j++){
                        if (currentContacts.get(j).getId() == curId){
                            toRemove = j;
                            contains = true;
                            break;
                        }
                    }
                    //honestly this is wack
                    if(contains){
                        currentContacts.remove(toRemove);
                        contains = false;
                    }
                }
            } catch( JSONException e){
                e.printStackTrace();
            }
            mContacts.setValue(currentContacts);
        }
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
                System.out.println(mCurrentContacts.getValue().toString());
            }
        } catch(JSONException e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Add Chat members
     */
    public void addMembers(String jwt) {
        String[] memberIds = new String[mSelectedContacts.getValue().size()];
        for(int i = 0; i < memberIds.length; i++){
            memberIds[i] ="" + mSelectedContacts.getValue().get(i).getId();
        }
        JSONObject body = new JSONObject();
        try {
            body.put("members", new JSONArray(memberIds));
            body.put("chatid", CHAT_ID);
        } catch (JSONException err){
            err.printStackTrace();
        }
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl+ "chat/add"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //no body for this get request
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

    private void handleAddResult(JSONObject jsonObject) {
        System.out.println(jsonObject.toString());
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

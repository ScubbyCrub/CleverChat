package edu.uw.tcss450.angelans.finalProject.model;

import android.app.Application;
import android.os.AsyncTask;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;
import me.pushy.sdk.Pushy;

/**
 * ViewModel that stores the Pushy Token that is associated with a single signed-in user.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class PushyTokenViewModel extends AndroidViewModel{

    private final MutableLiveData<String> mPushyToken;
    private final MutableLiveData<JSONObject> mResponse;

    /**
     * Constructor for PushyTokenViewModel.
     *
     * @param application The application that holds this ViewModel.
     */
    public PushyTokenViewModel(@NonNull Application application) {
        super(application);
        mPushyToken = new MutableLiveData<>();
        mPushyToken.setValue("");
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Register as an observer to listen for the PushToken.
     *
     * @param owner the fragment's lifecycle owner.
     * @param observer the observer that wants to listen to updates to PushToken.
     */
    public void addTokenObserver(@NonNull LifecycleOwner owner,
                                 @NonNull Observer<? super String> observer) {
        mPushyToken.observe(owner, observer);
    }

    /**
     * Register as an observer to see when the PushToken is assigned by the Pushy website.
     *
     * @param owner the fragment's lifecycle owner.
     * @param observer the observer that wants to listen to updates to when the PushToken is
     *                 received by Pushy.
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        mResponse.observe(owner, observer);
    }

    /**
     * Have the user's sign-in instance be associated with a new or old PushyToken.
     */
    public void retrieveToken() {
        if (!Pushy.isRegistered(getApplication().getApplicationContext())) {

            Log.d("PUSH VIEW MODEL", "FETCHING NEW TOKEN");
            new RegisterForPushNotificationsAsync().execute();

        } else {
            Log.d("PUSH VIEW MODEL", "USING OLD TOKEN");
            mPushyToken.setValue(
                Pushy.getDeviceCredentials(getApplication().getApplicationContext()).token);
        }
    }

    /**
     * Helper class to communicate with the Pushy site to retrieve a user's PushyToken.
     *
     * Note the Android class AsyncTask is deprecated as of Android Q. It is fine to use now.
     */
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, String> {
        /**
         * Retrieves a PushyToken in the background processes of the app.
         *
         * @param params Parameters that might be included while retrieving PushyToken in
         *               a background task.
         * @return The PushyToken associated with the user's account.
         */
        protected String doInBackground(Void... params) {
            Log.d("PushyTokenViewModel", "Attempt RegisterForPushNotificationsAsync");
            String deviceToken;
            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getApplication().getApplicationContext());
            }
            catch (Exception e) {
                // Return exc to onPostExecute
                return e.getMessage();
            }
            // Success
            return deviceToken;
        }

        @Override
        protected void onPostExecute(String token) {
            if (token.isEmpty()) {
                // Show error in log - You should add error handling for the user.
                Log.e("ERROR RETRIEVING PUSHY TOKEN", token);
            } else {
                Log.d("PushyTokenViewModel", "RegisterForPushNotificationsAsync " +
                        "token set successfully!");
                mPushyToken.setValue(token);
            }
        }
    }

    /**
     * Send this Pushy device token to the web service.
     *
     * @param jwt The user's JWT.
     * @throws IllegalStateException when this method is called before the token is retrieve.
     */
    public void sendTokenToWebservice(final String jwt) {
        if (mPushyToken.getValue().isEmpty()) {
            throw new IllegalStateException("No pushy token. " +
                    "Do NOT call until token is retrieved");
        }

        Log.d("PushyTokenViewModel 116",
                "Attempting to traverse to {put} /pushyauth endpoint");
        String url = getApplication().getResources().getString(R.string.base_url) +
                "pushyauth";

        JSONObject body = new JSONObject();
        try {
            body.put("token", mPushyToken.getValue());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                body, //push token found in the JSONObject body
                mResponse::setValue,
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
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * Delete the PushyToken associated with the user from the Push_Token table in the
     * backend database.
     *
     * @param jwt The user's JWT.
     */
    public void deleteTokenFromWebservice(final String jwt) {
        Log.d("PushyTokenViewModel 154",
                "Attempting to traverse to {Delete} /pushyauth endpoint");
        String url = getApplication().getResources().getString(R.string.base_url) + "pushyauth";

        Request request = new JsonObjectRequest (
                Request.Method.DELETE,
                url,
                null,
                mResponse::setValue,
                this:: handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key, value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * If there's a VolleyError with interacting with the PushyToken in the database,
     * handle it here.
     *
     * @param error The Volley Error.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            try {
                mResponse.setValue(new JSONObject("{" +
                        "code:" + error.networkResponse.statusCode +
                        ", data:" + data +
                        "}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }
}

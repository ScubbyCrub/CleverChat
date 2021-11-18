package edu.uw.tcss450.angelans.finalProject.ui.auth.signin;

import android.app.Application;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;

/**
 * Sign In ViewModel that protects user input to register their account beyond the
 * lifetime of the fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class SignInViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    /**
     * Constructor for SignInViewModel.
     *
     * @param theApplication The application that SignInViewModel should exist in.
     */
    public SignInViewModel(@NonNull Application theApplication) {
        super(theApplication);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Add an observer to the sign in data to notify once data changes.
     *
     * @param theOwner The owner of the class that has an android life cycle.
     * @param theObserver The observer to respond to when data is updated.
     */
    public void addResponseObserver(@NonNull LifecycleOwner theOwner,
                                    @NonNull Observer<? super JSONObject> theObserver) {
        mResponse.observe(theOwner, theObserver);
    }

    /**
     * Send a web request to the server to sign in to an account.
     *
     * @param theEmail The user's email that will attempt to be signed in.
     * @param thePassword The user's password that will attempt to be signed in.
     */
    public void connect(final String theEmail, final String thePassword) {
        String url = "https://cleverchat.herokuapp.com/api/signin";
//        Request request = new JsonObjectRequest(
//                Request.Method.GET,
//                url, new Response.Listener<String>(){
//                    @
//        }
////        request.setRetryPolicy(new DefaultRetryPolicy(
////                10_000,
////                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
////                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
////        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
////                .addToRequestQueue(request);
    }

    /**
     * How to handle if the network response comes back with errors.
     *
     * @param theError The error sent back from the web service.
     */
    private void handleError(final VolleyError theError) {
        if (Objects.isNull(theError.networkResponse)) {
            try {
                mResponse.setValue(new JSONObject("{" +
                        "error:\"" + theError.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(theError.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", theError.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                this.mResponse.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

}

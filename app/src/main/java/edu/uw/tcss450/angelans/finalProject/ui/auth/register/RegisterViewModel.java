package edu.uw.tcss450.angelans.finalProject.ui.auth.register;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;

/**
 * Register ViewModel that protects user input to register their account beyond the
 * lifetime of the fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 3
 */
public class RegisterViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    /**
     * Constructor for RegisterViewModel.
     *
     * @param theApplication The application that RegisterViewModel should exist in.
     */
    public RegisterViewModel(@NonNull Application theApplication) {
        super(theApplication);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Add an observer to the register data to notify once data changes.
     *
     * @param theOwner The owner of the class that has an android life cycle.
     * @param theObserver The observer to respond to when data is updated.
     */
    public void addResponseObserver(@NonNull LifecycleOwner theOwner,
                                    @NonNull Observer<? super JSONObject> theObserver) {
        mResponse.observe(theOwner, theObserver);
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

    /**
     * Send a web request to the server to register a new user account.
     *
     * @param theFirst The first name of the new account.
     * @param theLast The last name of the new account.
     * @param theUsername The unique username of the new account.
     * @param theEmail The email of the new account.
     * @param thePassword The password of the new account.
     */
    public void connect(final String theFirst,
                        final String theLast,
                        final String theUsername,
                        final String theEmail,
                        final String thePassword) {
        String url = getApplication().getResources().getString(R.string.base_url) + "register";
        JSONObject body = new JSONObject();
        try {
            body.put("firstName", theFirst);
            body.put("lastName", theLast);
            body.put("username", theUsername);
            body.put("email", theEmail);
            body.put("password", thePassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse::setValue,
                this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
}


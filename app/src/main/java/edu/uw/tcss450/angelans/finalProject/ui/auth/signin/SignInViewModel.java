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

    private MutableLiveData<JSONObject> response;

    /**
     * Constructor for SignInViewModel.
     *
     * @param application The application that SignInViewModel should exist in.
     */
    public SignInViewModel(@NonNull Application application) {
        super(application);
        response = new MutableLiveData<>();
        response.setValue(new JSONObject());
    }

    /**
     * Add an observer to the sign in data to notify once data changes.
     *
     * @param owner The owner of the class that has an android life cycle.
     * @param observer The observer to respond to when data is updated.
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        response.observe(owner, observer);
    }

    /**
     * Send a web request to the server to sign in to an account.
     *
     * @param email The user's email that will attempt to be signed in.
     * @param password The user's password that will attempt to be signed in.
     */
    public void connect(final String email, final String password) {
        String url = "https://cleverchat.herokuapp.com/api/signin";
        Request request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response::setValue,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = email + ":" + password;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext())
                .addToRequestQueue(request);
    }

    /**
     * How to handle if the network response comes back with errors.
     *
     * @param error The error sent back from the web service.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            try {
                response.setValue(new JSONObject("{" +
                        "error:\"" + error.getMessage() +
                        "\"}"));
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset())
                    .replace('\"', '\'');
            try {
                JSONObject response = new JSONObject();
                response.put("code", error.networkResponse.statusCode);
                response.put("data", new JSONObject(data));
                this.response.setValue(response);
            } catch (JSONException e) {
                Log.e("JSON PARSE", "JSON Parse Error in handleError");
            }
        }
    }

}

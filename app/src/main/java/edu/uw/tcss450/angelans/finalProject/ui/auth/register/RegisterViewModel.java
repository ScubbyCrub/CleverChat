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

/**
 * Register ViewModel that protects user input to register their account beyond the
 * lifetime of the fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class RegisterViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> response;

    /**
     * Constructor for RegisterViewModel.
     *
     * @param application The application that RegisterViewModel should exist in.
     */
    public RegisterViewModel(@NonNull Application application) {
        super(application);
        response = new MutableLiveData<>();
        response.setValue(new JSONObject());
    }

    /**
     * Add an observer to the register data to notify once data changes.
     *
     * @param owner The owner of the class that has an android life cycle.
     * @param observer The observer to respond to when data is updated.
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        response.observe(owner, observer);
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

    /**
     * Send a web request to the server to register a new user account.
     *
     * @param first The first name of the new account.
     * @param last The last name of the new account.
     * @param email The email of the new account.
     * @param password The password of the new account.
     */
    public void connect(final String first,
                        final String last,
                        final String email,
                        final String password) {
        String url = "https://cleverchat.herokuapp.com/api/register";
        JSONObject body = new JSONObject();
        try {
            body.put("firstName", first);
            body.put("lastName", last);
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response::setValue,
                this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }
}


package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
 * Password Reset ViewModel that protects user input to reset their password beyond the
 * lifetime of the fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class PasswordResetViewModel extends AndroidViewModel {
    private MutableLiveData<JSONObject> response;

    /**
     * Constructor for PasswordResetViewModel.
     *
     * @param application The application that PasswordResetViewModel should exist in.
     */
    public PasswordResetViewModel(@NonNull Application application){
        super(application);
        response = new MutableLiveData<>();
        response.setValue(new JSONObject());
    }

    /**
     * Add an observer to the PasswordReset data to notify once data changes.
     *
     * @param owner The owner of the class that has an android life cycle.
     * @param observer The observer to respond to when data is updated.
     */
    public void addResponseObserver(@NonNull LifecycleOwner owner,
                                    @NonNull Observer<? super JSONObject> observer) {
        response.observe(owner, observer);
    }

    /**
     * Send a web request to the server to request a user's password be changed.
     *
     * @param email The user's email.
     */
    public void connect(final String email){
        String url = "https://cleverchat.herokuapp.com/api/passwordreset";
        JSONObject body = new JSONObject();
        //add stuff to body
        try{
            body.put("userEmail",email);
        } catch( JSONException e){
            e.printStackTrace();
        }

        //make request
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response:: setValue,
                this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
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

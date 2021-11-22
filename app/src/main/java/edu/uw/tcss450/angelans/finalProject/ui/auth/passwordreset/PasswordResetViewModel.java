package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

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
 * Password Reset ViewModel that protects user input to reset their password beyond the
 * lifetime of the fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class PasswordResetViewModel extends AndroidViewModel {
    private MutableLiveData<JSONObject> mResponse;

    /**
     * Constructor for PasswordResetViewModel.
     *
     * @param theApplication The application that PasswordResetViewModel should exist in.
     */
    public PasswordResetViewModel(@NonNull Application theApplication){
        super(theApplication);
        mResponse = new MutableLiveData<>();
        mResponse.setValue(new JSONObject());
    }

    /**
     * Add an observer to the PasswordReset data to notify once data changes.
     *
     * @param theOwner The owner of the class that has an android life cycle.
     * @param theObserver The observer to respond to when data is updated.
     */
    public void addResponseObserver(@NonNull LifecycleOwner theOwner,
                                    @NonNull Observer<? super JSONObject> theObserver) {
        mResponse.observe(theOwner, theObserver);
    }

    /**
     * Send a web request to the server to request a user's password be changed.
     *
     * @param theEmail The user's email.
     */
    public void connect(final String theEmail) {
        String url = getApplication().getResources().getString(R.string.base_url) + "passwordreset";
        JSONObject body = new JSONObject();
        //add stuff to body
        try{
            body.put("userEmail",theEmail);
        } catch( JSONException e){
            e.printStackTrace();
        }

        //make request
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                mResponse:: setValue,
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

package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.model.Contact;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.ui.home.HomeViewModel;

/**
 * Password Reset ViewModel that protects user input to reset their password in-app
 * beyond the lifetime of the fragment.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 3
 */
public class AppPasswordResetViewModel extends AndroidViewModel {
    public MutableLiveData<Boolean> mResetComplete;

    /**
     * Constructor for AppPasswordResetViewModel
     *
     * @param application The application this ViewModel belongs to
     */
    public AppPasswordResetViewModel(@NonNull Application application) {
        super(application);
        mResetComplete = new MutableLiveData<Boolean>();
        mResetComplete.setValue(false);
    }

    /**
     * Resets the state to false so the user can navigate to the page again.
     */
    public void resetViewModelState() {
        mResetComplete.setValue(false);
    }

    /**
     * Add an observer to the AppPasswordReset data to notify once data changes.
     *
     * @param theOwner The owner of the class that has an android life cycle.
     * @param theObserver The observer to respond to when data is updated.
     */
    public void addObserver(@NonNull LifecycleOwner theOwner,
                            @NonNull Observer<? super Boolean> theObserver){
        mResetComplete.observe(theOwner,theObserver);
    }

    /**
     * Send an in-app password reset request to the web service.
     * @param email
     * @param oldPassword
     * @param newPassword
     * @param theUserJWT
     */
    public void resetPassword(String email, String oldPassword,
                              String newPassword, String theUserJWT){
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", oldPassword);
            body.put("newPassword",newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String baseUrl = getApplication().getResources().getString(R.string.base_url);
        String url =
                baseUrl+ "passwordresetinapp"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleResult,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", theUserJWT);
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
     * How to handle if the network response comes back with errors.
     *
     * @param theError The error sent back from the web service.
     */
    private void handleError(final VolleyError theError) {
        mResetComplete.setValue(false);
    }

    /**
     * How to handle if the network response comes back a success.
     *
     * @param jsonObject The reply from the web service.
     */
    private void handleResult(JSONObject jsonObject) {
        System.out.println(jsonObject.toString());
        mResetComplete.setValue(true);
    }
}

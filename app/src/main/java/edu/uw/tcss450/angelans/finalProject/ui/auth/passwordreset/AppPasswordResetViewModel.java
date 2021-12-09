package edu.uw.tcss450.angelans.finalProject.ui.auth.passwordreset;

import android.app.Application;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

public class AppPasswordResetViewModel extends AndroidViewModel {
    public MutableLiveData<Boolean> mResetComplete;
    public AppPasswordResetViewModel(@NonNull Application application) {
        super(application);
        mResetComplete = new MutableLiveData<Boolean>();
    }
    public void addObserver(@NonNull LifecycleOwner owner,
                                       @NonNull Observer<? super Boolean> observer){
        mResetComplete.observe(owner,observer);
    }

    public void resetPassword(String email, String oldPassword, String newPassword){
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
                baseUrl+ "passwordreset/verify"; //TODO NOTE WE USE  10.0.2.2 FOR LOCALHOST
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body, //no body for this get request
                this::handleResult,
                this::handleError) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                //TODO: Replace this to use the actual jwt stored in the app
//                headers.put("Authorization", jwt);
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

    private void handleError(VolleyError error) {
        error.printStackTrace();
    }

    private void handleResult(JSONObject jsonObject) {
        System.out.println(jsonObject.toString());
        mResetComplete.setValue(true);
    }
}

package edu.uw.tcss450.angelans.finalProject.ui.auth.signin;

import android.app.Application;
import android.util.Base64;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;

public class EmailVerificationViewModel extends AndroidViewModel {

    public EmailVerificationViewModel(@NonNull Application application) {
        super(application);
    }

    public void connect(String email){
        //TODO updatae this to point to the web endpoint
        String url = getApplication().getResources().getString(R.string.base_url) + "verification";
        JSONObject body = new JSONObject();
        try {
            body.put("userEmail",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handleSuccess,
                this::handleError);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(getApplication().getApplicationContext())
                .add(request);
    }

    private void handleError(VolleyError volleyError) {
        //empty to not crash the class
    }

    public void handleSuccess(final JSONObject result){
        //here in case we need it in the future
    }
}

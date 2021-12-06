package edu.uw.tcss450.angelans.finalProject.ui.home;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.ui.chat.SingleChatMessage;
import edu.uw.tcss450.angelans.finalProject.ui.contact.ContactInfo;

public class HomeViewModel extends AndroidViewModel {

    private MutableLiveData<JSONObject> mResponse;

    public HomeViewModel(@NonNull Application theApplication) {
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

    public void connectForUsername(final String theEmail) {
        String url = getApplication().getResources().getString(R.string.base_url) + "username";
        JSONObject body = new JSONObject();
        try {
            body.put("email", "angelans@uw.edu");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("HomeViewModel", "error with building JSON ");
        }
        Log.d("HomeViewModel", "about to send JSON "
                + body.toString());
        Request request = new JsonObjectRequest(
                Request.Method.GET,
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

    /**
     * How to handle a successful return from the web service interaction.
     *
     * @param theResponse The response from the web service.
     */
    private void handleSuccess(final JSONObject theResponse) {
        if (theResponse.length() > 0) {
            if (!theResponse.has("username")) {
                Log.e("Home Fragment","Username GET responded with error");
            } else {
                mResponse.setValue(theResponse);
            }
        } else {
            Log.d("JSON Response (HomeFragment 89)", "No Response");
        }
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
                Log.e("JSON PARSE", "(HomeViewModel) JSON Parse Error in handleError");
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
                Log.e("JSON PARSE", "(HomeViewModel) JSON Parse Error in handleError");
            }
        }
    }
}

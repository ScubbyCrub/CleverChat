package edu.uw.tcss450.angelans.finalProject.ui.contact;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.io.RequestQueueSingleton;

public class SearchViewModel extends AndroidViewModel {
    private Map<String, MutableLiveData<List<SearchList>>> mSearchList;

    public SearchViewModel(@NonNull Application application) {
        super(application);
        mSearchList = new HashMap<>();
    }

    public void addSearchObserver(String input,
                                  @NonNull LifecycleOwner owner,
                                  @NonNull Observer<? super List<SearchList>> observer) {
        getOrCreateMapEntry(input).observe(owner, observer);
    }

    public List<SearchList> getSearchListByInput(final String input) {
        return getOrCreateMapEntry(input).getValue();
    }

    private MutableLiveData<List<SearchList>> getOrCreateMapEntry(final String input) {
        if(!mSearchList.containsKey(input)) {
            mSearchList.put(input, new MutableLiveData<>(new ArrayList<>()));
        }
        return mSearchList.get(input);
    }

    public void getSearchList(final String input, final String email, final String jwt) {
        String url = getApplication().getResources().getString(R.string.base_url) +
                "search";
        JSONObject body = new JSONObject();
        try {
            body.put("search", input);
            body.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                this::handelSuccess,
                this::handleError) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                // add headers <key,value>
                headers.put("Authorization", jwt);
                return headers;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                10_000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Instantiate the RequestQueue and add the request to the queue
        RequestQueueSingleton.getInstance(getApplication().getApplicationContext()).addToRequestQueue(request);
    }

    private void handelSuccess(final JSONObject response) {
        List<SearchList> list;
        if (!response.has("search")) {
            throw new IllegalStateException("Unexpected response in SearchViewModel: " + response);
        }
        try {
            list = getSearchListByInput(response.getString("search"));
            JSONArray searches = response.getJSONArray("user");
            for(int i = 0; i < searches.length(); i++) {
                JSONObject search = searches.getJSONObject(i);
                SearchList mSearch = new SearchList(
                        search.getString("username"),
                        search.getString("name")
                );
                if (!list.contains(mSearch)) {
                    // don't add a duplicate
                    list.add(0, mSearch);
                } else {
                    // this shouldn't happen but could with the asynchronous
                    // nature of the application
                    Log.wtf("list already received",
                            "Or duplicate input:" + mSearch.getmUsername());
                }

            }
        }catch (JSONException e) {
            Log.e("JSON PARSE ERROR", "Found in handle Success ChatViewModel");
            Log.e("JSON PARSE ERROR", "Error: " + e.getMessage());
        }
    }

    /**
     * How to handle a Volley error if one returns from the web service.
     *
     * @param error The Volley error from the web service.
     */
    private void handleError(final VolleyError error) {
        if (Objects.isNull(error.networkResponse)) {
            Log.e("NETWORK ERROR", error.getMessage());
        }
        else {
            String data = new String(error.networkResponse.data, Charset.defaultCharset());
            Log.e("CLIENT ERROR",
                    error.networkResponse.statusCode +
                            " " +
                            data);
        }
    }

}

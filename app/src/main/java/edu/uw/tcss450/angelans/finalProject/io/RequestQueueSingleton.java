package edu.uw.tcss450.angelans.finalProject.io;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * This class handles network requests and caches the requests throughout the lifetime of
 * the app.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class RequestQueueSingleton {
    private static RequestQueueSingleton mInstance;
    private static Context mContext;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    /**
     * Private constructor for RequestQueueSingleton.
     *
     * @param context globally available, application-specific resources and classes for
     *                this app.
     */
    private RequestQueueSingleton(Context context) {
        RequestQueueSingleton.mContext = context;
        mRequestQueue = getmRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    /**
     * Create a synchronized RequestQueueSingleton that lasts the lifetime of the app.
     *
     * @param theContext globally available, application-specific resources and classes for
     *                this app.
     * @return A synchronized RequestQueueSingleton that lasts the lifetime of the app.
     */
    public static synchronized RequestQueueSingleton getInstance(Context theContext) {
        if (mInstance == null) {
            mInstance = new RequestQueueSingleton(theContext);
        }
        return mInstance;
    }

    /**
     * Getter method for mRequestQueue.
     *
     * @return a copy of mRequestQueue.
     */
    public RequestQueue getmRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adds a request to the request queue.
     *
     * @param req The request to be added to the request queue.
     * @param <T> The type of request that can be added to the request queue.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getmRequestQueue().add(req);
    }

    /**
     * Getter methods for mImageLoader.
     *
     * @return a reference to mImageLoader.
     */
    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }
}


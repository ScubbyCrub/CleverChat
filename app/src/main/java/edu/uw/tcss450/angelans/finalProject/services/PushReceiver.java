package edu.uw.tcss450.angelans.finalProject.services;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONException;

import edu.uw.tcss450.angelans.finalProject.AuthActivity;
import edu.uw.tcss450.angelans.finalProject.R;
import edu.uw.tcss450.angelans.finalProject.ui.chat.SingleChatMessage;
import me.pushy.sdk.Pushy;

/**
 * BroadcastReceiver that stores information sent from Pushy.
 * Currently stores message information, but can be modified to track other Pushy messages.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 3
 */
public class PushReceiver extends BroadcastReceiver {

    public static final String RECEIVED_NEW_MESSAGE = "new message from pushy";

    private static final String CHANNEL_ID = "1";

    private static final String TYPE_MESSAGE = "msg";
    private static final String TYPE_CONTACT = "contact";

    @Override
    public void onReceive(Context theContext, Intent theIntent) {
        String typeOfMessage = theIntent.getStringExtra("type");
        Log.d("PushReceiver", "Type of Message: " + typeOfMessage);
        SingleChatMessage message = null;
        boolean isContact = false;
        int chatId = -1;
        try{
            if (typeOfMessage.equals(TYPE_MESSAGE)) { // If incoming push is a message
                message = SingleChatMessage.createFromJsonString(
                        theIntent.getStringExtra("message"));
                chatId = theIntent.getIntExtra("chatid", -1);
            } else if (typeOfMessage.equals(TYPE_CONTACT)){ // If incoming push is a contact request
                isContact = true;
            } else {
                Log.d("PushReceiver", "Type of push not recognized");
            }
        } catch (JSONException e) {
            //Web service sent us something unexpected...I can't deal with this.
            throw new IllegalStateException("Error from Web Service. Contact Dev Support");
        }

        ActivityManager.RunningAppProcessInfo appProcessInfo =
                new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(appProcessInfo);

        // App is in foreground
        if (appProcessInfo.importance == IMPORTANCE_FOREGROUND
                || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
            if (typeOfMessage.equals(TYPE_MESSAGE)) {
                //app is in the foreground so send the message to the active Activities
                Log.d("PushReceiver", "Message received in foreground: " + message);

                //create an Intent to broadcast a message to other parts of the app.
                Intent i = new Intent(RECEIVED_NEW_MESSAGE);
                i.putExtra("chatMessage", message);
                i.putExtra("chatid", chatId);
                i.putExtras(theIntent.getExtras());

                theContext.sendBroadcast(i);
            }
        } else {
            //app is in the background so create and post a notification
            if (typeOfMessage.equals(TYPE_MESSAGE)) {
                Log.d("PushReceiver", "Message received in background: " + message.getMessage());

                Intent i = new Intent(theContext, AuthActivity.class);
                i.putExtras(theIntent.getExtras());

                PendingIntent pendingIntent = PendingIntent.getActivity(theContext, 0,
                        i, PendingIntent.FLAG_UPDATE_CURRENT);

                //research more on notifications the how to display them
                //https://developer.android.com/guide/topics/ui/notifiers/notifications
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(theContext, CHANNEL_ID)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_chat_notification_24dp)
                        .setContentTitle("Message from " + message.getSender())
                        .setContentText("\"" + message.getMessage() + "\"")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent);

                // Automatically configure a ChatMessageNotification Channel for devices
                // running Android O+
                Pushy.setNotificationChannel(builder, theContext);

                // Get an instance of the NotificationManager service
                NotificationManager notificationManager =
                        (NotificationManager) theContext.getSystemService(
                                theContext.NOTIFICATION_SERVICE);

                // Build the notification and display it
                notificationManager.notify(1, builder.build());
            } else if (isContact) {
                Log.d("PushReceiver", "Contact received in background");

                Intent i = new Intent(theContext, AuthActivity.class);
                i.putExtras(theIntent.getExtras());

                PendingIntent pendingIntent = PendingIntent.getActivity(theContext, 0,
                        i, PendingIntent.FLAG_UPDATE_CURRENT);

                //research more on notifications the how to display them
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(theContext, CHANNEL_ID)
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.ic_contact_black_notification_24)
                                .setContentTitle("New contact request!")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setContentIntent(pendingIntent);

                // Automatically configure a ChatMessageNotification Channel for devices
                // running Android O+
                Pushy.setNotificationChannel(builder, theContext);

                // Get an instance of the NotificationManager service
                NotificationManager notificationManager =
                        (NotificationManager) theContext.getSystemService(
                                theContext.NOTIFICATION_SERVICE);

                // Build the notification and display it
                notificationManager.notify(2, builder.build());
            }
        }
    }
}

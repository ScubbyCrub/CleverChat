package edu.uw.tcss450.angelans.finalProject.ui.chat;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * This class encapsulate chat message details.
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public final class SingleChatMessage implements Serializable {

    private final int mMessageId;
    private final String mMessage;
    private final String mSender;
    private final String mTimeStamp;

    /**
     * Constructor that holds the details for a single chat message.
     *
     * @param messageId The unique ID number for a single message in a single chatroom.
     * @param message The contents of the user's sent message.
     * @param sender The email of the user that sent the message.
     * @param timeStamp The time at which the message was sent.
     */
    public SingleChatMessage(int messageId, String message, String sender, String timeStamp) {
        mMessageId = messageId;
        mMessage = message;
        mSender = sender;
        mTimeStamp = timeStamp;
    }

    /**
     * Static factory method to turn a properly formatted JSON String into a
     * ChatMessage object.
     *
     * @param cmAsJson the String to be parsed into a ChatMessage Object.
     * @return a ChatMessage Object with the details contained in the JSON String.
     * @throws JSONException when cmAsString cannot be parsed into a ChatMessage.
     */
    public static SingleChatMessage createFromJsonString(final String cmAsJson) throws JSONException {
        final JSONObject msg = new JSONObject(cmAsJson);
        return new SingleChatMessage(msg.getInt("messageid"),
                msg.getString("message"),
                msg.getString("email"),
                msg.getString("timestamp"));
    }

    /**
     * Getter for the message's contents.
     *
     * @return The message's contents.
     */
    public String getMessage() {
        return mMessage;
    }

    /**
     * Getter for the user's email who sent the message.
     *
     * @return The user's email that sent the message.
     */
    public String getSender() {
        return mSender;
    }

    /**
     * Getter for the time at which the message was sent.
     *
     * @return The time at which the message was sent.
     */
    public String getTimeStamp() {
        return mTimeStamp;
    }

    /**
     * Getter for the unique ID of the message in the single chatroom.
     *
     * @return The unique ID of the message in the single chatroom.
     */
    public int getMessageId() {
        return mMessageId;
    }

    /**
     * Provides equality solely based on MessageId.
     * @param other the other object to check for equality.
     * @return true if other message ID matches this message ID, false otherwise.
     */
    @Override
    public boolean equals(@Nullable Object other) {
        boolean result = false;
        if (other instanceof SingleChatMessage) {
            result = mMessageId == ((SingleChatMessage) other).mMessageId;
        }
        return result;
    }
}

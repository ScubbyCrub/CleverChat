package edu.uw.tcss450.angelans.finalProject.ui.chat;

import java.util.Arrays;
import java.util.List;

import edu.uw.tcss450.angelans.finalProject.model.Chat;
import edu.uw.tcss450.angelans.finalProject.model.Contact;

/**
 * Generates mock contacts
 *
 * @author Vlad Tregubov
 * @version  1
 */
public class ContactGeneratorChat {
    private static final Contact[] CONTACTS;
    public static final int COUNT = 20;


    static {
        CONTACTS = new Contact[COUNT];
        for (int i = 0; i < CONTACTS.length; i++) {
            CONTACTS[i] = new Contact(i , "Mail@MAIL.COM", "Johny123", "Johnny","John", "Doe");
        }
    }

    /**
     * Getter for a list of the user's contacts.
     *
     * @return A list of the user's contacts
     */
    public static List<Contact> getContactList() {
        return Arrays.asList(CONTACTS);
    }

    /**
     * Getter for an array of the user's contacts.
     *
     * @return An array of the user's contacts.
     */
    public static Contact[] getContacts() {
        return Arrays.copyOf(CONTACTS, CONTACTS.length);
    }

    /**
     * Constructor for ContactGeneratorChat
     */
    public  ContactGeneratorChat() { }
}

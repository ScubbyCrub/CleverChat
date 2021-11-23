package edu.uw.tcss450.angelans.finalProject.model;

import edu.uw.tcss450.angelans.finalProject.ui.chat.ContactCardListNewChatFragment;

/**
 * This class is an model of a contact
 * @Author Vlad Tregubov
 */
public class Contact {
    final String username;
    final String nickname;
    final String firstName;
    final String lastName;
    final int id;


    final String email;

    /**
     * Creates an instance of a contact
     * @param id the id of the contact
     * @param email email of the contact
     * @param username username of the contact
     * @param nickname nickname of the contact
     * @param firstName first name of the contact
     * @param lastName last name of the contact
     */
    public Contact(int id, String email, String username, String nickname, String firstName, String lastName){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname= nickname;
        this.username = username;
        this.email = email;
    }

    /**
     * Returns the username of the contact
     * @return contact username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the nickname of the contact
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the first name of the contact
     * @return first name of the contact
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the contact
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the email of the contact
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     *  returns the id of the contact
     * @return contact id
     */
    public int getId() {return id;}

    /**
     * returns the string representation of the contact
     * @return string representation of the contact
     */
    @Override
    public String toString(){
        return "" + id + " "+ firstName + " "+ lastName + " "+ email + " "+ nickname + " ";
    }

}

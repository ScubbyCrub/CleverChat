package edu.uw.tcss450.angelans.finalProject.model;

import edu.uw.tcss450.angelans.finalProject.ui.chat.ContactCardListNewChatFragment;

public class Contact {
    final String username;
    final String nickname;
    final String firstName;
    final String lastName;
    final int id;


    final String email;

    public Contact(int id, String email, String username, String nickname, String firstName, String lastName){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nickname= nickname;
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getEmail() {
        return email;
    }
    public int getId() {return id;}
    @Override
    public String toString(){
        return "" + id + " "+ firstName + " "+ lastName + " "+ email + " "+ nickname + " ";
    }

}

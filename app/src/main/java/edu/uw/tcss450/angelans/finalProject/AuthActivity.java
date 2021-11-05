package edu.uw.tcss450.angelans.finalProject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The Activity that hosts authorization-related fragments (SignIn, Register,
 * PasswordRest, etc.).
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
    }
}

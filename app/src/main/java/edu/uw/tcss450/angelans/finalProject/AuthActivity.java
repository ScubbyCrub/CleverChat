package edu.uw.tcss450.angelans.finalProject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import edu.uw.tcss450.angelans.finalProject.model.PushyTokenViewModel;
import me.pushy.sdk.Pushy;

/**
 * The Activity that hosts authorization-related fragments (SignIn, Register,
 * PasswordRest, etc.).
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class AuthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);
        setContentView(R.layout.activity_auth);

        // If not yet running, start Pushy listening service
        Pushy.listen(this);

        initiatePushyTokenRequest();
    }

    private void initiatePushyTokenRequest() {
        new ViewModelProvider(this).get(PushyTokenViewModel.class).retrieveToken();
    }
}

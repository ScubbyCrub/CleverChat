package edu.uw.tcss450.angelans.finalProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;

/**
 * The activity that hosts fragments of the app that are available after a successful
 * sign in to an account (Home, Contacts, Chat, Weather, etc.).
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 1
 */
public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    /**
     * Removes the users jwt token from the saved preferences on signout
     */
    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        "shared_prefs",
                        Context.MODE_PRIVATE);
        prefs.edit().remove("jwt").apply();
        //End the app completely
        finishAndRemoveTask();
    }
    /**
     * This method inflates the menu and displays it on the screen
     * @param menu the menu object to inflate
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.drop_down, menu);
        return true;
    }

    /**
     * This handles clicks to the menu and signs out the user
     * @param item the item we are checking click for
     * @return the item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if(id == R.id.action_sign_out){
            signOut();
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     *
     * @param theSavedInstanceState
     */
    @Override
    protected void onCreate(Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt())
        ).get(UserInfoViewModel.class);

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_contact, R.id.navigation_chat, R.id.navigation_weather)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
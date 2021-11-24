package edu.uw.tcss450.angelans.finalProject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import edu.uw.tcss450.angelans.finalProject.databinding.ActivityMainBinding;
import edu.uw.tcss450.angelans.finalProject.model.NewMessageCountViewModel;
import edu.uw.tcss450.angelans.finalProject.model.PushyTokenViewModel;
import edu.uw.tcss450.angelans.finalProject.model.UserInfoViewModel;
import edu.uw.tcss450.angelans.finalProject.services.PushReceiver;
import edu.uw.tcss450.angelans.finalProject.ui.chat.SingleChatMessage;
import edu.uw.tcss450.angelans.finalProject.ui.chat.SingleChatViewModel;

/**
 * The activity that hosts fragments of the app that are available after a successful
 * sign in to an account (Home, Contacts, Chat, Weather, etc.).
 *
 * @author Group 6: Teresa, Vlad, Tien, Angela
 * @version Sprint 2
 */
public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private ActivityMainBinding mBinding;

    private MainPushMessageReceiver mPushMessageReceiver;

    private NewMessageCountViewModel mNewMessageModel;

    /**
     * Creates the activity
     *
     * @param theSavedInstanceState Saved instance states that should be applied to this
     *                              sign in session.
     */
    @Override
    protected void onCreate(Bundle theSavedInstanceState) {
        super.onCreate(theSavedInstanceState);

        MainActivityArgs args = MainActivityArgs.fromBundle(getIntent().getExtras());

        new ViewModelProvider(this,
                new UserInfoViewModel.UserInfoViewModelFactory(args.getEmail(), args.getJwt())
        ).get(UserInfoViewModel.class);

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

//        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_contact, R.id.navigation_chat,
                R.id.navigation_weather)
                .build();
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        mNewMessageModel = new ViewModelProvider(this).get(NewMessageCountViewModel.class);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this,
                R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPushMessageReceiver == null) {
            mPushMessageReceiver = new MainPushMessageReceiver();
        }
        IntentFilter iFilter = new IntentFilter(PushReceiver.RECEIVED_NEW_MESSAGE);
        registerReceiver(mPushMessageReceiver, iFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPushMessageReceiver != null) {
            unregisterReceiver(mPushMessageReceiver);
        }
    }

    /**
     * This method inflates the menu and displays it on the screen
     *
     * @param menu The menu object to inflate.
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.drop_down, menu);
        return true;
    }

    /**
     * This handles clicks to the options menu and signs out the user.
     *
     * @param item The item we are checking click for.
     * @return The item the user clicked on.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.action_sign_out) {
            signOut();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Removes the users JWT token from the saved preferences on sign out.
     */
    private void signOut() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);

        prefs.edit().remove(getString(R.string.keys_prefs_jwt)).apply();

        PushyTokenViewModel model = new ViewModelProvider(this)
                .get(PushyTokenViewModel.class);

        // When we heard back from the web service, quit
        model.addResponseObserver(this, result -> finishAndRemoveTask());

        model.deleteTokenFromWebservice(
                new ViewModelProvider(this)
                        .get(UserInfoViewModel.class).getmJwt()
        );
    }

    /**
     * A BroadcastReceiver that listens for messages sent from PushReceiver.
     */
    private class MainPushMessageReceiver extends BroadcastReceiver {
        private SingleChatViewModel mModel = new ViewModelProvider(MainActivity.this)
                .get(SingleChatViewModel.class);

        @Override
        public void onReceive(Context context, Intent intent) {
            NavController nc = Navigation.findNavController(
                    MainActivity.this, R.id.nav_host_fragment);

            NavDestination nd = nc.getCurrentDestination();

            if (intent.hasExtra("chatMessage")) {
                SingleChatMessage cm = (SingleChatMessage)
                        intent.getSerializableExtra("chatMessage");

                // If the user is not on the chat screen, update the
                // NewMessageCountView Model
                if (nd.getId() != R.id.navigation_chat) {
                    mNewMessageModel.increment();
                }

                // Inform the view model holding chatroom messages of the new message
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }
}
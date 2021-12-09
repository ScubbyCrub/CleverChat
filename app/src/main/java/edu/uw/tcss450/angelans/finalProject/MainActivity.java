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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Iterator;
import java.util.Set;

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
 * @version Sprint 3
 */
public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;

    private ActivityMainBinding mBinding;

    private MainPushMessageReceiver mPushMessageReceiver;

    private NewMessageCountViewModel mNewMessageModel;

    private SingleChatViewModel mSingleChatModel;

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
        mSingleChatModel = new ViewModelProvider(this).get(SingleChatViewModel.class);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.singleChatFragment) {
                // When the user navigates to the chats page, modify the new message count
                // TODO Bug: MainActivity.onCreate() happens before SingleChatFragment.onCreate(),
                // Leading to mostRecentlyVisitedChatID to be old by the time it tries
                // to delete live notifs.
                final int[] mostRecentlyVisitedChatID = {0};

                mSingleChatModel.addMostRecentChatIDObserver(MainActivity.this, count -> {
                    mostRecentlyVisitedChatID[0] = count;
                });
                Log.d("MainPushMessageReceiver",
                        "Most Recent Visited Chat (onCreate) = " + mostRecentlyVisitedChatID[0]);

                mNewMessageModel.reset(mostRecentlyVisitedChatID[0]);
            }
        });

        mNewMessageModel.addMessageCountObserver(this, count -> {
            BadgeDrawable badge = mBinding.navView.getOrCreateBadge(R.id.navigation_chat);
            badge.setMaxCharacterCount(2);

            Set<Integer> countKeys = count.keySet();
            Iterator<Integer> iteratorCountKeys = countKeys.iterator();
            int totalMessageCount = 0;

            // Find total value of new messages among all chats
            while (iteratorCountKeys.hasNext()) {
                totalMessageCount += count.get(iteratorCountKeys.next());
            }

            if (totalMessageCount > 0) {
                // New messages! Update and show the notification badge.
                badge.setNumber(totalMessageCount);
                badge.setVisible(true);
            } else {
                // User did some action to clear the new messages, remove the badge
                badge.clearNumber();
                badge.setVisible(false);
            }
        });
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

            int incomingChatID = intent.getIntExtra("chatid", -1);

            Log.d("MainPushMessageReceiver", "Incoming chat ID:" + incomingChatID);

            // If the incoming intent is a chatMessage notificication
            if (intent.hasExtra("chatMessage")) {
                SingleChatMessage cm = (SingleChatMessage)
                        intent.getSerializableExtra("chatMessage");

                final int[] mostRecentlyVisitedChatID = {0};

                mModel.addMostRecentChatIDObserver(MainActivity.this, count -> {
                    mostRecentlyVisitedChatID[0] = count;
                });

                // If the user is not on the chat screen, update the
                // NewMessageCountView Model
                Log.d("MainPushMessageReceiver",
                        "Most Recent Visited Chat (onReceive) = " + mostRecentlyVisitedChatID[0]);
                if (nd.getId() != R.id.singleChatFragment
                        || mostRecentlyVisitedChatID[0] != incomingChatID) {
                    Log.d("MainPushMessageReceiver",
                            "Different fragment or different chat view? "
                                    + "Increment message count!");
                    mNewMessageModel.increment(intent.getIntExtra("chatid",-1));
                }

                // Inform the view model holding chatroom messages of the new message
                mModel.addMessage(intent.getIntExtra("chatid", -1), cm);
            }
        }
    }
}
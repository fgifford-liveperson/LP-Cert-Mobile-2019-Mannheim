package com.liveperson.mobilemessagingexercise;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.liveperson.mobilemessagingexercise.ActivityRunners.ClearRunner;
import com.liveperson.mobilemessagingexercise.Conversations.AskUsConversation;
import com.liveperson.mobilemessagingexercise.Fragments.MyAccountFragment;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/******************************************************************
 * Parent class providing common capabilities for the activities.
 * in the Mobile Messaging Exercise
 *****************************************************************/
public class MobileMessagingExerciseActivity extends AppCompatActivity {
    private static final String TAG = MobileMessagingExerciseActivity.class.getSimpleName();

    private ApplicationStorage applicationStorage;
    private MobileMessagingExerciseApplication applicationInstance;
    private ClearRunner clearRunner;

    /**
     * Android callback invoked as the activity is created.
     * @param savedInstanceState any instance state data saved in a previous execution
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        applicationInstance = (MobileMessagingExerciseApplication) getApplication();
        applicationStorage = ApplicationStorage.getInstance();

        //Create an instance of the class that clears conversations and runs an activity
        clearRunner = new ClearRunner(this, applicationStorage);

        Log.i(TAG, "MobileMessagingActivity created");
    }

    /**
     * Android callback invoked as the activity is destroyed
     */
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Convenience method to display a pop up toast message from any activity
     * @param message the text of the message to be shown
     */
    protected void showToast(String message) {
        //Delegate to the method in the application
        applicationInstance.showToast(message);
    }

    /**
     * Transfer control to the Welcome activity
     */
    protected void startWelcome() {
        Intent intentWelcome = new Intent(this, WelcomeActivity.class);
        this.startActivity(intentWelcome);
    }

    /**
     * Transfer control to the Login activity
     */
    protected void startLogin() {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        this.startActivity(intentLogin);
    }

    /**
     * Transfer control to the My Account activity
     */
    protected void startMyAccount() {
        Intent intentMyAccount = new Intent(this, MyAccountFragment.class);
        this.startActivity(intentMyAccount);
    }

    /**
     * Transfer control to the Ask Us activity
     */
    protected void startAskUs() {
        AskUsConversation askUsConversation = new AskUsConversation(this, applicationStorage);
        clearRunner.clearAndRun(askUsConversation);
    }

    /**
     * Convenience method to retrieve the Brand Server URL
     * @return the Brand Server URL
     */
    protected String getBrandServerBaseUrl() {
        return ApplicationConstants.BRAND_SERVER_URL;
    }

    /**
     * Determine whether or not the activity was started by a Live Engage push message
     * @param intent the intent associated with starting the activity
     * @return true if the activity was started by a Live Engage push message and false otherwise
     */
    protected boolean startedByLePushMessage(Intent intent) {
        return intent.getBooleanExtra(ApplicationConstants.LP_IS_FROM_PUSH, false);
    }

    /*************************
     * Bean methods
     ************************/
    public ApplicationStorage getApplicationStorage() {
        return applicationStorage;
    }

    public MobileMessagingExerciseApplication getApplicationInstance() {
        return applicationInstance;
    }

    public ClearRunner getClearRunner() {
        return clearRunner;
    }
}

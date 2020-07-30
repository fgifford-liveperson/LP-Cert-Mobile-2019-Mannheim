package com.liveperson.mobilemessagingexercise.Conversations;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPConversationsHistoryStateToDisplay;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/**************************************************************************************
 * Class to display the My Account Screen.
 * Provides the LivePerson initialization callback
 *************************************************************************************/
public class MyAccountConversation implements Runnable, InitLivePersonCallBack {
    private static final String TAG = MyAccountConversation.class.getSimpleName();

    private Activity hostContext;
    private ApplicationStorage applicationStorage;
    private MobileMessagingExerciseApplication applicationInstance;
    private LPAuthenticationParams authParams;
    private ConversationViewParams conversationViewParams;

    /**
     * Convenience constructor.
     * @param hostContext the context of the activity in which the screen is to run
     * @param applicationStorage the singleton holding the shared storage for the app
     */
    public MyAccountConversation(Activity hostContext, ApplicationStorage applicationStorage) {
        this.hostContext = hostContext;
        this.applicationStorage = applicationStorage;
        this.applicationInstance = (MobileMessagingExerciseApplication) hostContext.getApplication();
    }

    /**
     * Run the My Account screen as a LivePerson conversation.
     */
    @Override
    public void run() {
        //Set up the parameters needed for initializing LivePerson
        //TODO Phase 3: Set up the properties needed by LivePerson initialization

        //Initialize LivePerson for the My Account screen
        //TODO Phase 3: Implement initialization of LivePerson
    }

    /**
     * Set up and show the LivePerson conversation associated with the My Account screen.
     * Invoked if initialization of LivePerson is successful
     */
    @Override
    public void onInitSucceed() {
        //Display and log a confirmation message
        Log.i(TAG, "LivePerson SDK initialize completed");
        showToast("LivePerson SDK initialize completed");

        //Set up the authentication parameters
        //TODO Phase 3: Set up the authentication parameters for an authenticated conversation

        //Set up the conversation view parameters
        //TODO Phase 3: Set up the parameters controlling the conversation view

        //Start the conversation
        //TODO Phase 3: Show the specified conversation
    }

    /**
     * Report an initialization error.
     * Invoked if initialization of LivePerson fails
     * @param e the exception associated with the failure
     */
    @Override
    public void onInitFailed(Exception e) {
        //Display and log the error
        Log.e(TAG, "LivePerson SDK initialize failed", e);
        showToast("Unable to initialize LivePerson");
    }

    /**
     * Convenience method to display a pop up toast message from any activity
     * @param message the text of the message to be shown
     */
    protected void showToast(String message) {
        //Delegate to the method in the application
        applicationInstance.showToast(message);
    }
}


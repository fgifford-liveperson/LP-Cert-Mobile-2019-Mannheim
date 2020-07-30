package com.liveperson.mobilemessagingexercise.Conversations;

import android.app.Activity;
import android.util.Log;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPConversationsHistoryStateToDisplay;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/***********************************************************************************
 * Class to display the Ask Us Screen.
 * Provides the LivePerson initialization callback
 **********************************************************************************/
public class AskUsConversation implements Runnable, InitLivePersonCallBack {
    private static final String TAG = AskUsConversation.class.getSimpleName();

    private Activity hostContext;
    private ApplicationStorage applicationStorage;
    private ConversationViewParams conversationViewParams;
    private ConsumerProfile consumerProfile;
    private MobileMessagingExerciseApplication applicationInstance;

    /**
     * Convenience constructor.
     * @param hostContext the context of the activity in which the screen is to run
     * @param applicationStorage the singleton holding the shared storage for the app
     */
    public AskUsConversation(Activity hostContext, ApplicationStorage applicationStorage) {
        this.hostContext = hostContext;
        this.applicationStorage = applicationStorage;
        this.applicationInstance = (MobileMessagingExerciseApplication) hostContext.getApplication();
    }

    /**
     * Run the Ask Us screen as a LivePerson conversation.
     */
    @Override
    public void run() {
        //Set up the parameters needed for initializing LivePerson for messaging
        //TODO Phase 1: Set up the properties needed by LivePerson initialization

        //Initialize LivePerson
        //TODO Phase 1: Implement initialization of LivePerson
    }

    /**
     * Set up and show the LivePerson conversation associated with the Ask Us screen.
     * Invoked if initialization of LivePerson is successful
     */
    @Override
    public void onInitSucceed() {
        //Display and log a confirmation message
        Log.i(TAG, "LivePerson SDK initialize completed");
        showToast("LivePerson SDK initialize completed");

        //Set up the consumer profile from data in application storage
        //TODO Phase 1: Initialize the LivePerson consumer profile

        //Set up the user profile
        //TODO Phase 1: Configure LivePerson with the consumer profile

        //Set up the authentication parameters
        //TODO Phase 1: Set up the authentication parameters for an anonymous conversation

        //Set up the conversation view parameters
        //TODO Phase 1: Set up the parameters controlling the conversation view

        //Start the conversation
        //TODO Phase 1: Show the specified conversation
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



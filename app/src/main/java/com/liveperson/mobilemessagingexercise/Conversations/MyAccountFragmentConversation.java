package com.liveperson.mobilemessagingexercise.Conversations;

import androidx.fragment.app.FragmentActivity;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPConversationsHistoryStateToDisplay;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.infra.auth.LPAuthenticationType;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.Fragments.MyAccountFragment;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/*********************************************************************************************
 * Class to display the My Account Screen using the LivePerson fragment mechanism.
 * Provides the LivePerson initialization callback
 * Provides the OnComplete listener to deal with Firebase callback
 * Provides the ICallback listener to deal with LivePerson push message registration callback
 *********************************************************************************************/
public class MyAccountFragmentConversation implements Runnable, InitLivePersonCallBack, OnCompleteListener<InstanceIdResult>, ICallback<Void, Exception> {
    private static final String TAG = MyAccountFragmentConversation.class.getSimpleName();

    private static final String LIVEPERSON_FRAGMENT = "liveperson_fragment";

    private MyAccountFragment myAccountFragment;
    private ApplicationStorage applicationStorage;
    private MobileMessagingExerciseApplication applicationInstance;
    private LPAuthenticationParams authParams;
    private ConversationViewParams conversationViewParams;
    private ConversationFragment lpConversationFragment;

    /**
     * Convenience constructor.
     * @param myAccountFragment the fragment container in which this conversation is to run
     * @param applicationStorage the singleton holding the shared storage for the app
     */
    public MyAccountFragmentConversation(MyAccountFragment myAccountFragment, ApplicationStorage applicationStorage) {
        this.myAccountFragment = myAccountFragment;
        this.applicationStorage = applicationStorage;
        this.applicationInstance = (MobileMessagingExerciseApplication) myAccountFragment.getApplication();
    }

    /**
     * Run the My Account screen as a LivePerson conversation.
     */
    @Override
    public void run() {
        //Set up the parameters needed for initializing LivePerson
        InitLivePersonProperties initLivePersonProperties =
                new InitLivePersonProperties(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER,
                        ApplicationConstants.LIVE_PERSON_APP_ID,
                        null,
                        this);

        //Initialize LivePerson for the My Account screen
        LivePerson.initialize(this.myAccountFragment, initLivePersonProperties);
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
        authParams = new LPAuthenticationParams(LPAuthenticationType.AUTH);
        authParams.setAuthKey("");
        authParams.addCertificatePinningKey("");
        authParams.setHostAppJWT(applicationStorage.getJwt());

        //Set up the conversation view parameters
        conversationViewParams = new ConversationViewParams(false);
        conversationViewParams.setHistoryConversationsStateToDisplay(LPConversationsHistoryStateToDisplay.ALL);

        //TODO Phase 4: Get the LivePerson fragment

        if (isValidState(myAccountFragment)) {
            //TODO Phase 4: Add the LivePerson fragment to the screen

            //TODO Phase 4: Retrieve the Firebase token to use
        }
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
     * Process the result of retrieving the Firebase FCM token for this app.
     * @param task the task whose completion triggered this method being called
     */
    @Override
    public void onComplete(Task<InstanceIdResult> task) {
        if (!task.isSuccessful()) {
            Log.w(TAG, "getInstanceId failed", task.getException());
            return;
        }

        // Retrieve the Firebase FCM token from the result
        String fcmToken = task.getResult().getToken();

        // Log the token value
        Log.d(TAG +  " Firebase FCM token: ", fcmToken);

        //TODO Phase 4: Register to receive push messages with the new firebase token
    }

    /**
     * Registration for push messages with LiveEngage was successful.
     * @param aVoid the parameter for the successful registration
     */
    @Override
    public void onSuccess(Void aVoid) {
        Log.d(TAG, "Registered for push notifications");
    }

    /**
     * Registration for push messages with LiveEngage failed.
     * @param e the Exception associated with the failure
     */
    public void onError(Exception e) {
        Log.d(TAG, "Unable to register for push notifications");
    }

    /**
     * Check that we are in a suitable state
     * @return true if the state is suitable and false otherwise
     */
    private boolean isValidState(FragmentActivity fragmentActivity) {
        return !fragmentActivity.isFinishing() && !fragmentActivity.isDestroyed();
    }

    /**
     * Convenience method to display a pop up toast message from any activity
     * @param message the text of the message to be shown
     */
    private void showToast(String message) {
        //Delegate to the method in the application
        applicationInstance.showToast(message);
    }
}


package com.liveperson.mobilemessagingexercise.Conversations;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.LPConversationsHistoryStateToDisplay;
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
public class AskUsConversation implements Runnable, InitLivePersonCallBack, OnCompleteListener<InstanceIdResult>, ICallback<Void, Exception> {
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
        InitLivePersonProperties initLivePersonProperties =
                new InitLivePersonProperties(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER,
                        ApplicationConstants.LIVE_PERSON_APP_ID,
                        this);

        //Initialize LivePerson
        LivePerson.initialize(this.hostContext, initLivePersonProperties);
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
        this.consumerProfile = new ConsumerProfile.Builder()
             .setFirstName(applicationStorage.getFirstName())
             .setLastName(applicationStorage.getLastName())
             .build();

        //Set up the user profile
        LivePerson.setUserProfile(consumerProfile);

        //Set up the authentication parameters
        LPAuthenticationParams authParams = new LPAuthenticationParams();
        authParams.setAuthKey("");
        authParams.addCertificatePinningKey("");

        //Set up the conversation view parameters
        ConversationViewParams conversationViewParams = new ConversationViewParams(false);
        conversationViewParams.setHistoryConversationsStateToDisplay(LPConversationsHistoryStateToDisplay.ALL);

        //Start the conversation
        LivePerson.showConversation(hostContext, authParams, conversationViewParams);

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(this);
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

        //Register to receive push messages from LivePerson with the firebase FCM token
        LivePerson.registerLPPusher(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER, ApplicationConstants.LIVE_PERSON_APP_ID,
                fcmToken, null, this);
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

}



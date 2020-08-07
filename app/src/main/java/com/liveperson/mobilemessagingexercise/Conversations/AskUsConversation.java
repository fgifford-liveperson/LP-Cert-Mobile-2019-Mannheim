package com.liveperson.mobilemessagingexercise.Conversations;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPConversationsHistoryStateToDisplay;
import com.liveperson.infra.MonitoringInitParams;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.model.ConsumerProfile;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;
import com.liveperson.monitoring.model.EngagementDetails;
import com.liveperson.monitoring.model.LPMonitoringIdentity;
import com.liveperson.monitoring.sdk.MonitoringParams;
import com.liveperson.monitoring.sdk.api.LivepersonMonitoring;
import com.liveperson.monitoring.sdk.callbacks.EngagementCallback;
import com.liveperson.monitoring.sdk.callbacks.MonitoringErrorType;
import com.liveperson.monitoring.sdk.responses.LPEngagementResponse;

import org.intellij.lang.annotations.Language;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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

        MonitoringInitParams monitoringInitParams = new MonitoringInitParams(ApplicationConstants.LIVE_PERSON_APP_INSTALLATION_ID);

        //Set up the parameters needed for initializing LivePerson for messaging with C4M.
        InitLivePersonProperties initLivePersonProperties =
                new InitLivePersonProperties(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER,
                        ApplicationConstants.LIVE_PERSON_APP_ID,
                        monitoringInitParams,
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


        //Creating Identities array and LPMonitoringIdentity
        ArrayList<LPMonitoringIdentity> identityList = new ArrayList<>();
        final LPMonitoringIdentity monitoringIdentity = new LPMonitoringIdentity();
        identityList.add(monitoringIdentity);

        //Create Monitoring Params, Engagement Attributes and Entry Points
        @Language("json")
        final String entryPointString = "[\"unauth\"]";

        // Creating engagement attributes
        @Language("json")
        final String engagementAttributeString = "[\n"
                + "  {\n"
                + "    \"type\": \"purchase\",\n"
                + "    \"total\": 11.7,\n"
                + "    \"orderId\": \"Dx342\"\n"
                + "  }\n"
                + "]";

        JSONArray entryPoints = new JSONArray();
        JSONArray engagementAttributes = new JSONArray();

        try {
            entryPoints = new JSONArray(entryPointString);
            engagementAttributes = new JSONArray(engagementAttributeString);
        } catch (final JSONException e) {
            Log.w(TAG, e.getLocalizedMessage(), e);
        }

        final MonitoringParams monitoringParams = new MonitoringParams("PageId", entryPoints, engagementAttributes);

        //Invoke getEngagement
        LivepersonMonitoring.getEngagement(hostContext, identityList, monitoringParams, new EngagementCallback() {
            @Override
            public void onSuccess(@NonNull LPEngagementResponse lpEngagementResponse) {
                final List<EngagementDetails> engagementDetails = lpEngagementResponse.getEngagementDetailsList();
                if (engagementDetails != null && !engagementDetails.isEmpty()) {


                    //Construct CampaignInfo Object
                    final Long campaignID = Long.parseLong(engagementDetails.get(0).getCampaignId());
                    final Long engagementId = Long.parseLong(engagementDetails.get(0).getEngagementId());
                    final String contextId = engagementDetails.get(0).getContextId();

                    final String sessionId  = lpEngagementResponse.getSessionId();
                    final String visitorId = lpEngagementResponse.getVisitorId();

                    try {
                        final CampaignInfo campaignInfo = new CampaignInfo(campaignID,
                                engagementId,
                                contextId,
                                sessionId,
                                visitorId);

                        //Set up the conversation view parameters
                        conversationViewParams = new ConversationViewParams(false);
                        conversationViewParams.setHistoryConversationsStateToDisplay(LPConversationsHistoryStateToDisplay.ALL);

                        //Set CampaignInfo Object in conversationViewParam
                        conversationViewParams.setCampaignInfo(campaignInfo);

                        LivePerson.showConversation(hostContext, authParams, conversationViewParams);

                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(AskUsConversation.this);


                    } catch (Exception e) {
                        //Handle Exception
                        Log.w(TAG, e.getLocalizedMessage(), e);
                    }

                }

            }

            @Override
            public void onError(@NonNull MonitoringErrorType errorType, @Nullable Exception exception) {
                //Handle Exception
                Log.w(TAG, "Error " + errorType + ": " + exception, exception);
            }
        });



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
        Log.d(TAG, "Unable to register for push notifications", e);
    }

}



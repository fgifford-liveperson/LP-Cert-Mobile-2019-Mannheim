package com.liveperson.mobilemessagingexercise.Conversations;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.LPConversationsHistoryStateToDisplay;
import com.liveperson.infra.MonitoringInitParams;
import com.liveperson.infra.callbacks.InitLivePersonCallBack;
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.Fragments.MyAccountFragment;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.R;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;
import com.liveperson.monitoring.model.EngagementDetails;
import com.liveperson.monitoring.model.LPMonitoringIdentity;
import com.liveperson.monitoring.sdk.MonitoringParams;
import com.liveperson.monitoring.sdk.api.LivepersonMonitoring;
import com.liveperson.monitoring.sdk.callbacks.EngagementCallback;
import com.liveperson.monitoring.sdk.callbacks.MonitoringErrorType;
import com.liveperson.monitoring.sdk.responses.LPEngagementResponse;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

        //TODO C4M auth 2 Create MonitoringInitParams
        final MonitoringInitParams monitoringInitParams = null;

        //TODO C4M auth 3 Add MonitoringInitParams to InitLivePersonProperties
        //Set up the parameters needed for initializing LivePerson
        InitLivePersonProperties initLivePersonProperties =
                new InitLivePersonProperties(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER,
                        ApplicationConstants.LIVE_PERSON_APP_ID,
                        this);

        //Initialize LivePerson for the My Account screen
        LivePerson.initialize(myAccountFragment, initLivePersonProperties);

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
        authParams = new LPAuthenticationParams(LPAuthenticationParams.LPAuthenticationType.AUTH);
        authParams.setAuthKey("");
        authParams.addCertificatePinningKey("");
        authParams.setHostAppJWT(applicationStorage.getJwt());

        //TODO C4M auth 4 Create identities array and LPMonitoringIdentity
        final ArrayList<LPMonitoringIdentity> identityList = null;
        final LPMonitoringIdentity monitoringIdentity;


        //TODO C4M auth 5 Create Monitoring Params, Engagement Attributes and Entry Points
        final JSONArray entryPoints = new JSONArray();


        // Creating engagement attributes
        final JSONArray engagementAttributes = new JSONArray();
        final JSONObject purchase = new JSONObject();
        final JSONObject lead = new JSONObject();


        engagementAttributes.put(purchase);
        // engagementAttributes.put(lead);

        final MonitoringParams monitoringParams = new MonitoringParams("PageId", entryPoints, engagementAttributes);

        //TODO C4M auth 6 Invoke getEngagement
        LivepersonMonitoring.getEngagement(myAccountFragment.getBaseContext(), identityList, monitoringParams, new EngagementCallback() {
            @Override
            public void onSuccess(@NotNull LPEngagementResponse lpEngagementResponse) {

                if(lpEngagementResponse.getEngagementDetailsList().size() > 0){
                    final List<EngagementDetails> engagementDetails = lpEngagementResponse.getEngagementDetailsList();

                    //TODO C4M auth 7 Construct CampaignInfo Object
                    Long campaignID = 0L;
                    Long engagementId = 0L;
                    String contextId = "";

                    String sessionId  = null;
                    String visitorId = null;

                    try {
                        CampaignInfo campaignInfo= new CampaignInfo(campaignID,
                                engagementId,
                                contextId,
                                sessionId,
                                visitorId);

                        //Set up the conversation view parameters
                        conversationViewParams = new ConversationViewParams(false);
                        conversationViewParams.setHistoryConversationsStateToDisplay(LPConversationsHistoryStateToDisplay.ALL);

                        //TODO  C4M auth 8 set CampaignInfo Object in conversationViewParam

                        //TODO  C4M auth 9 set CampaignInfo Object in conversationViewParam
                        //Create the fragment that holds the LivePerson conversation
                        lpConversationFragment = (ConversationFragment) LivePerson.getConversationFragment(authParams, conversationViewParams);

                        if (isValidState(myAccountFragment)) {
                            //Add the LivePerson conversation fragment to the screen
                            FragmentTransaction ft = myAccountFragment.getSupportFragmentManager().beginTransaction();
                            ft.add(R.id.my_account_fragment_container, lpConversationFragment, LIVEPERSON_FRAGMENT).commitAllowingStateLoss();
                            //Retrieve the Firebase FCM token to use to regiser with LivePerson
                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener((MyAccountFragmentConversation.this));
                        }

                        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(MyAccountFragmentConversation.this);


                    } catch (final Exception e) {
                        //Handle Exception
                        Log.w(e.getLocalizedMessage(), e);
                    }

                }

            }

            @Override
            public void onError(@NotNull MonitoringErrorType errorType, @Nullable Exception exception) {
                //Handle Exception
                Log.w(exception.getLocalizedMessage(), exception);
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


package com.liveperson.mobilemessagingexercise.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.liveperson.api.LivePersonIntents;
import com.liveperson.api.sdk.LPConversationData;
import com.liveperson.api.sdk.PermissionType;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.messaging.TaskType;
import com.liveperson.messaging.model.AgentData;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/****************************************************
 * Class to receive events broadcast by LivePerson.
 ***************************************************/
public class LivePersonBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = LivePersonBroadcastReceiver.class.getSimpleName();
    private MobileMessagingExerciseApplication applicationInstance;

    /**
     * Constuctor.
     * @param applicationInstance the instance of the application for which this is the receiver
     */
    public LivePersonBroadcastReceiver(MobileMessagingExerciseApplication applicationInstance) {
        this.applicationInstance = applicationInstance;
    }

    /**
     * Invoked when an event, broadcast by LivePerson, is received.
     * @param context the application context in which the event was raised
     * @param intent the intent broadcast by LivePerson
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Got LP intent event with action " + intent.getAction());

        //Route the event to the appropriate method
        switch (intent.getAction()) {
            case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_AVATAR_TAPPED_INTENT_ACTION:
                onAgentAvatarTapped(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_DETAILS_CHANGED_INTENT_ACTION:
                onAgentDetailsChanged(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_AGENT_TYPING_INTENT_ACTION:
                onAgentTyping(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONNECTION_CHANGED_INTENT_ACTION:
                onConnectionChanged(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_MARKED_AS_NORMAL_INTENT_ACTION:
                onConversationMarkedAsNormal(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_MARKED_AS_URGENT_INTENT_ACTION:
                onConversationMarkedAsUrgent(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_RESOLVED_INTENT_ACTION:
                 onConversationResolved(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CONVERSATION_STARTED_INTENT_ACTION:
                onConversationStarted(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_LAUNCHED_INTENT_ACTION:
                onCsatLaunched(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_DISMISSED_INTENT_ACTION:
                onCsatDismissed(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_SKIPPED_INTENT_ACTION:
                onCsatSkipped(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_CSAT_SUBMITTED_INTENT_ACTION:
                onCsatSubmitted(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_ERROR_INTENT_ACTION:
                onError(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_OFFLINE_HOURS_CHANGES_INTENT_ACTION:
                onOfflineHoursChanges(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_TOKEN_EXPIRED_INTENT_ACTION:
                onTokenExpired(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_USER_DENIED_PERMISSION:
                onUserDeniedPermission(intent);
            break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_USER_ACTION_ON_PREVENTED_PERMISSION:
                onUserActionOnPreventedPermission(intent);
                break;

            case LivePersonIntents.ILivePersonIntentAction.LP_ON_STRUCTURED_CONTENT_LINK_CLICKED:
                onStructuredContentLinkClicked(intent);
                break;

        }

    }

    /**
     * Process the Avatar Tapped event
     * @param intent the associated intent
     */
    private void onAgentAvatarTapped(Intent intent) {
        AgentData agentData = LivePersonIntents.getAgentData(intent);
        showToast("Agent Avatar Tapped: " + agentData.mFirstName
                + " " + agentData.mLastName);
    }

    /**
     * Process the Agent Details Changed event
     * @param intent the associated intent
     */
    private void onAgentDetailsChanged(Intent intent) {
        AgentData agentData = LivePersonIntents.getAgentData(intent);
        showToast("Agent Details Changed: " + agentData);
    }

    /**
     * Process the Agent Typing event
     * @param intent the associated intent
     */
    private void onAgentTyping(Intent intent) {
        boolean isTyping = LivePersonIntents.getAgentTypingValue(intent);
        showToast("Agent is typing: " + isTyping);
    }

    /**
     * Process the Connection Changed event
     * @param intent the associated intent
     */
    private void onConnectionChanged(Intent intent) {
        boolean isConnected = LivePersonIntents.getConnectedValue(intent);
        showToast("Connected to LiveEngage: " + isConnected);
    }

    /**
     * Process the Conversation Marked as Normal event
     * @param intent the associated intent
     */
    private void onConversationMarkedAsNormal(Intent intent) {
        showToast("Conversation Marked As Normal");
    }

    /**
     * Process the Conversation Marked as Urgent event
     * @param intent the associated intent
     */
    private void onConversationMarkedAsUrgent(Intent intent) {
        showToast("Conversation Marked As Urgent");
    }

    /**
     * Process the Conversation Resolved event
     * @param intent the associated intent
     */
    private void onConversationResolved(Intent intent) {
        LPConversationData conversationData = LivePersonIntents.getLPConversationData(intent);
        showToast("Conversation started " + conversationData.getId()
                + " reason " + conversationData.getCloseReason());
    }

    /**
     * Process the Conversation Started event
     * @param intent the associated intent
     */
    private void onConversationStarted(Intent intent) {
        LPConversationData conversationData = LivePersonIntents.getLPConversationData(intent);
        showToast("Conversation started " + conversationData.getId()
                + " reason " + conversationData.getCloseReason());
    }

    /**
     * Process the Customer Satisfaction Screen Launched event
     * @param intent the associated intent
     */
    private void onCsatLaunched(Intent intent) {
        showToast("CSAT launched");
    }

    /**
     * Process the Customer Satisfaction Screen Dismissed event
     * @param intent the associated intent
     */
    private void onCsatDismissed(Intent intent) {
        showToast("CSAT skipped");
    }

    /**
     * Process the Customer Satisfaction Screen Skipped event
     * @param intent the associated intent
     */
    private void onCsatSkipped(Intent intent) {
        showToast("CSAT skipped");
    }

    /**
     * Process the Customer Satisfaction Submitted event
     * @param intent the associated intent
     */
    private void onCsatSubmitted(Intent intent) {
        String conversationId = LivePersonIntents.getConversationID(intent);
        showToast("CSAT submitted for conversation: " + conversationId);
    }

    /**
     * Process the Error event
     * @param intent the associated intent
     */
    private void onError(Intent intent) {
        TaskType type = LivePersonIntents.getOnErrorTaskType(intent);
        String message = LivePersonIntents.getOnErrorMessage(intent);
        showToast("Error: " + type.name() + " " + message);
    }

    /**
     * Process the Offline Hours Changes event
     * @param intent the associated intent
     */
    private void onOfflineHoursChanges(Intent intent) {
        boolean isOfflineHoursOn = LivePersonIntents.getOfflineHoursOn(intent);
        showToast("Offline hours changes: " + isOfflineHoursOn);
    }

    /**
     * Process the Token Expired Event
     * @param intent the associated event
     */
    private void onTokenExpired(Intent intent) {
        showToast("Token Expired");
        LivePerson.reconnect(new LPAuthenticationParams().setAuthKey(
                ApplicationStorage.getInstance().getAuthCode()));

    }

    /**
     * Process the User Denied Permission event
     * @param intent the associated intent
     */
    private void onUserDeniedPermission(Intent intent) {
        PermissionType permissionType = LivePersonIntents.getPermissionType(intent);
        boolean doNotShowAgainMarked = LivePersonIntents.getPermissionDoNotShowAgainMarked(intent);
        showToast("User Denied Permission: " + permissionType.name()
                + " doNotShowAgainMarked = " + doNotShowAgainMarked);
    }

    /**
     * Process the User Action on Prevented Permission event
     * @param intent the associated intent
     */
    private void onUserActionOnPreventedPermission(Intent intent) {
        PermissionType permissionType = LivePersonIntents.getPermissionType(intent);
        showToast("User Action On Prevented Permission: " + permissionType.name());
    }

    /**
     * Process the Structured Content Link Clicked event
     * @param intent the associated intent
     */
    private void onStructuredContentLinkClicked(Intent intent) {
        String uri = LivePersonIntents.getLinkUri(intent);
        showToast("Structured Content Link Clicked. Uri: " + uri);
    }

    /**
     * Convenience method to display a pop up toast message
     * @param message the text of the message to be shown
     */
    protected void showToast(String message) {
        //Delegate to the method in the application
        applicationInstance.showToast(message);
    }

}

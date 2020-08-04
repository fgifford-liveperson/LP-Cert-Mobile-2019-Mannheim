package com.liveperson.mobilemessagingexercise.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.model.PushMessage;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.R;
import com.liveperson.mobilemessagingexercise.WelcomeActivity;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

import java.util.Map;

import static com.liveperson.mobilemessagingexercise.model.ApplicationConstants.LP_IS_FROM_PUSH;

/** Service for handling Firebase push notifications. */
public class LpFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = LpFirebaseMessagingService.class.getSimpleName();

    private ApplicationStorage applicationStorage;
    private MobileMessagingExerciseApplication applicationInstance;
    private PushMessage pushMessage;
    private UnreadMessagesHandler unreadMessagesHandler;
    private PushRegistrationHandler pushRegistrationHandler;

    /** Constructor. */
    public LpFirebaseMessagingService() {
        super();
        Log.d(TAG, "Constructor called");
    }

    /**
     * Android callback invoked as the service is created.
     */
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate called");
        super.onCreate();

        //Link to the main application
        applicationInstance = (MobileMessagingExerciseApplication) getApplication();
        applicationStorage = ApplicationStorage.getInstance();
        unreadMessagesHandler = new UnreadMessagesHandler();
        pushRegistrationHandler = new PushRegistrationHandler();
    }

    /**
     * Android callback invoked when a push message is received.
     * @param remoteMessage the push message
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        //Retrieve the message payload, if any
        Map<String, String> messageData = remoteMessage.getData();

        if (!messageData.isEmpty()) {
            //The message does have a payload, so log the details
            Log.d(TAG, "Message data payload: ");
            for (Map.Entry<String, String> entry : messageData.entrySet()) {
                Log.d(TAG, "  " + entry.getKey() + " : " + entry.getValue());
            }

            //Retrieve the LivePerson PushMessage instance from the Firebase message
            pushMessage = LivePerson.handlePushMessage(this, remoteMessage.getData(),
                    ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER, false);

            if (pushMessage != null) {
                //The message contains a valid LivePerson push message, so read the
                //number of unread LiveEngage messages
                LivePerson.getUnreadMessagesCount(ApplicationConstants.LIVE_PERSON_APP_ID, unreadMessagesHandler);
            }
        }

        //Log the message payload, if any
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    /**
     * Process the arrival of an updated Firebase FCM push message token.
     * @param fcmToken the new Firebase FCM push message token
     */
    @Override
    public void onNewToken(String fcmToken) {
        Log.d(TAG, "New Firebase token received: " + fcmToken);
        //Update the registration with the new token
        LivePerson.registerLPPusher(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER, ApplicationConstants.LIVE_PERSON_APP_ID,
                fcmToken, null, pushRegistrationHandler);
    }

    /**
     * Create the text for the count of unread messages
     * @param messageNumber the number of unread messages
     * @return a message about the unread messages, suitable for the consumer
     */
    private String createUnreadMessageText(int messageNumber) {
        String messageNumberStr;
        String unreadText = getLeString(R.string.unreadMessages);
        switch(messageNumber) {
            case 0:
                messageNumberStr = getLeString(R.string.no);
                break;
            case 1:
                unreadText = getLeString(R.string.unreadMessage);
            default:
                messageNumberStr = Integer.toString(messageNumber);
                break;
        }

        return getLeString(R.string.youHave) + messageNumberStr + unreadText;
    }

    /**
     * Transform a string from the resources into one suitable for display
     * @param id the id of the string resource containing the message
     * @return a String in which all occurrences of underscore have been replaced with blank.
     * Underscore is used in the resource to preserve leading and trailing blanks
     */
    private String getLeString(int id) {
        String baseString = getString(id);
        return baseString.replace('_', ' ');
    }

    /**
     * Creates an Android notification channel
     * @param context the context in which the channel is being created
     * @param channelId the unique id for the channel within the application
     * @param channelName the name of the channel as shown in the notification preferences UI in Android
     * @param isHighImportance whether the channel holds important messages or not.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context, String channelId, String channelName, boolean isHighImportance) {
        NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        if (isHighImportance) {
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        }
        getNotificationManager(context).createNotificationChannel(notificationChannel);
    }

    private NotificationManager getNotificationManager(Context ctx) {
        return (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Class to handle the callbacks from LivePerson after retrieving the number of unread messages
     * NOTE: A separate class is used because two of the callbacks used in this service implement the
     * same interface, namely ICallback.
     */
    private class UnreadMessagesHandler implements ICallback<Integer, Exception> {
        /**
         * Create the notification and show it to the consumer, once the count of unread messages is known
         * @param unreadMessageCount the number of unread messages.
         */
        @Override
        public void onSuccess(Integer unreadMessageCount) {
            Context context = LpFirebaseMessagingService.this;
            Notification.Builder builder = createNotificationBuilder(context,
                    ApplicationConstants.LP_PUSH_NOTIFICATION_CHANNNEL_ID, "Push Notification", true);

            builder.setContentIntent(createPendingIntent(context))
                    .setContentTitle(pushMessage.getFrom() + getLeString(R.string.said))
                    .setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setNumber(pushMessage.getCurrentUnreadMessagesCounter())
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setStyle(new Notification.InboxStyle()
                    .addLine(pushMessage.getMessage())
                    .addLine(createUnreadMessageText(unreadMessageCount - 1)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder
                    .setCategory(Notification.CATEGORY_MESSAGE);
            }

            getNotificationManager(context).notify(ApplicationConstants.LP_PUSH_NOTIFICATION_ID, builder.build());
        }

        /**
         * Can't get the number of unread messages
         * @param e the exception associated with the failure
         */
        @Override
        public void onError(Exception e) {
            Log.e(TAG, "Unable to get count of unread messages", e);
        }

        /**
         * Create notification builder.
         * @param ctx the context in which the notification is being built
         * @param channelId the id of the channel for the notification. Only significant on Android 8.0 ('O') and above
         * @param channelName the name of the channel as it appears in the Android notification preferences. Only significant on Android 8.0 ('O') and above
         * @param isHighImportance whether the channel holds important messages or not. Only significant on Android 8.0 ('O') and above
         * @return the notification builder
         */
        private Notification.Builder createNotificationBuilder(Context ctx, String channelId, String channelName, boolean isHighImportance) {
            Notification.Builder builder;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                //Create the notification builder for the specified context
                builder = new Notification.Builder(ctx);
            } else {
                //Create the channel for the notification. Has no effect if the channel already exists.
                createNotificationChannel(ctx, channelId, channelName, isHighImportance);
                //Create the notification builder for the context and channel
                builder = new Notification.Builder(ctx, channelId);
            }

            return builder;
        }

        /**
         * Create a pending intent which will start the Welcome activity from the notification
         * @param ctx the context in which the intent is being created
         * @return the pending intent which will allow the Welcome activity to be started from
         *      the notification
         */
        private PendingIntent createPendingIntent(Context ctx) {
            Intent welcomeActivityIntent = new Intent(ctx, WelcomeActivity.class);
            //Add the indication that this is from a LivePerson push message
            welcomeActivityIntent.putExtra(LP_IS_FROM_PUSH, true);

            //Create the pending intent which can be used from the notification Mark the
            //intent to reuse any existing instance, while updating any extra data
            PendingIntent welcomeActivityPendingIntent = PendingIntent.getActivity(ctx,
                    0, welcomeActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            return welcomeActivityPendingIntent;
        }

    }

    /**
     * Class to handle the callbacks from LivePerson after processing a request to register an
     * FCM token
     * NOTE: A separate class is used because two of the callbacks used in this service implement the
     * same interface, namely ICallback.
     */
    private static class PushRegistrationHandler implements ICallback<Void, Exception> {
        /**
         * Log the successful processing of the registration of the FCM token
         * @param empty the parameter, which is void.
         */
        @Override
        public void onSuccess(Void empty) {
            Log.d(TAG, "New FCM token successfully registered with LivePerson");
        }

        /**
         * Log the failure to register the FCM token with LivePerson
         * @param e the exception associated with the failure
         */
        @Override
        public void onError(Exception e) {
            Log.e(TAG, "Unable to register FCM token with LivePerson", e);
        }
    }

}



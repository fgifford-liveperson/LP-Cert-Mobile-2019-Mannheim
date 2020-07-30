package com.liveperson.mobilemessagingexercise.ActivityRunners;

import android.app.Activity;
import android.util.Log;

import com.liveperson.infra.ICallback;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.callbacks.LogoutLivePersonCallback;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseApplication;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/**************************************************************************
 * Class to clear any existing conversation before running a new activity.
 * Provides the LivePerson log out callback
 *************************************************************************/
public class ClearRunner implements LogoutLivePersonCallback {
    private static final String TAG = ClearRunner.class.getSimpleName();

    private Activity hostContext;
    private ApplicationStorage applicationStorage;
    private MobileMessagingExerciseApplication applicationInstance;
    private Runnable runnable;

    /**
     * Convenience constructor.
     * @param hostContext the context of the activity that starts this instance
     * @param applicationStorage the singleton holding the shared storage for the app
     */
    public ClearRunner(Activity hostContext, ApplicationStorage applicationStorage) {
        this.hostContext = hostContext;
        this.applicationStorage = applicationStorage;
        this.applicationInstance = (MobileMessagingExerciseApplication) hostContext.getApplication();
    }

    /**
     * Clear any existing conversation, and then execute the specified runnable.
     * @param runnable the runnable to execute
     */
    public void clearAndRun(Runnable runnable) {
        this.runnable = runnable;

        //Unregister from push notifications
        LivePerson.unregisterLPPusher(
                ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER,
                ApplicationConstants.LIVE_PERSON_APP_ID,
                new ICallback<Void, Exception>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast("Unregistered from push notifications");
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.w(TAG, e.getLocalizedMessage(), e);
                    }
                });

        //Log out from LivePerson, clearing any existing conversation
        LivePerson.logOut(hostContext, ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER,
                ApplicationConstants.LIVE_PERSON_APP_ID, this);
    }

    /**
     * Run the specified activity.
     * Invoked if logout from LivePerson is successful
     */
    @Override
    public void onLogoutSucceed() {
        Log.i(TAG, "LivePerson SDK logout completed");
        showToast("LivePerson SDK logout completed");

        //Logout has been successful, so execute the runnable
        hostContext.runOnUiThread(runnable);
    }

    /**
     * Report the failure to log out.
     * Invoked if logout from LivePerson fails
     */
    @Override
    public void onLogoutFailed() {
        Log.e(TAG, "LivePerson SDK logout failed");
        showToast("Unable to log out from LivePerson");
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


package com.liveperson.mobilemessagingexercise;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.messaging.sdk.api.callbacks.LogoutLivePersonCallback;
import com.liveperson.mobilemessagingexercise.ActivityRunners.ClearRunner;
import com.liveperson.mobilemessagingexercise.Conversations.AskUsConversation;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;
import com.liveperson.mobilemessagingexercise.receivers.LivePersonBroadcastReceiver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({
        AskUsConversation.class,
        LivePerson.class,
        LocalBroadcastManager.class,
})
@RunWith(PowerMockRunner.class)
public class Phase1UnitTest {
    @Mock
    public Activity activity;

    @Before
    public final void setUp() {
        Mockito.when(activity.getApplication())
                .thenReturn(Mockito.mock(MobileMessagingExerciseApplication.class));
        Mockito.doAnswer(invocation -> {
                    ((Runnable) invocation.getArgument(0)).run();
                    return null;
                }
        ).when(activity).runOnUiThread(any(Runnable.class));
    }

    @Test
    public final void testApplicationConstants() {
        Assert.assertNotEquals(
                "TODO - Replace with your app's LiveEngage account number",
                "20553802",
                ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER);
    }

    @Test
    public final void testMobileMessagingExerciseApplication() {
        final LocalBroadcastManager localBroadcastManager =
                PowerMockito.mock(LocalBroadcastManager.class);
        PowerMockito.mockStatic(LocalBroadcastManager.class);
        Mockito.when(LocalBroadcastManager.getInstance(eq(null)))
                .thenReturn(localBroadcastManager);
        new MobileMessagingExerciseApplication().onCreate();
        Mockito.verify(localBroadcastManager,
                Mockito.description("TODO Phase 1: Register for LivePerson events"))
                .registerReceiver(
                        any(LivePersonBroadcastReceiver.class),
                        any(IntentFilter.class));
    }

    @Test
    public final void testClearRunnerClearAndRun() {
        PowerMockito.mockStatic(LivePerson.class);
        new ClearRunner(activity, null).clearAndRun(null);
        PowerMockito.verifyStatic(LivePerson.class,
                Mockito.description("TODO Phase 1: Implement logout from LivePerson"));
        LivePerson.logOut(any(Context.class), anyString(), anyString(),
                any(LogoutLivePersonCallback.class));
    }

    @Test
    public final void testClearRunnerOnLogoutSucceed() {
        final Runnable runnable = Mockito.mock(Runnable.class);
        final ClearRunner clearRunner = new ClearRunner(activity, null);
        clearRunner.clearAndRun(runnable);
        clearRunner.onLogoutSucceed();
        Mockito.verify(runnable,
                Mockito.description("TODO Phase 1: Execute the runnable on the UI thread")).run();
    }

    @Test
    public final void testAskUsConversationInitialize() throws Exception {
        final InitLivePersonProperties initLivePersonProperties =
                Mockito.mock(InitLivePersonProperties.class);
        PowerMockito.mockStatic(LivePerson.class);
        PowerMockito.whenNew(InitLivePersonProperties.class).withAnyArguments()
                .thenReturn(initLivePersonProperties);
        new AskUsConversation(activity, null).run();
        PowerMockito.verifyNew(InitLivePersonProperties.class, Mockito.description(
                "TODO Phase 1: Set up the properties needed by LivePerson initialization"))
                .withArguments(
                        eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER),
                        eq(ApplicationConstants.LIVE_PERSON_APP_ID),
                        any(AskUsConversation.class));
        PowerMockito.verifyStatic(LivePerson.class,
                Mockito.description("TODO Phase 1: Implement initialization of LivePerson"));
        LivePerson.initialize(any(Context.class), eq(initLivePersonProperties));
    }

    @Test
    public final void testAskUsConversationOnInitSucceed() {
        final ApplicationStorage applicationStorage = ApplicationStorage.getInstance();
        PowerMockito.mockStatic(LivePerson.class);
        new AskUsConversation(activity, applicationStorage).onInitSucceed();
        PowerMockito.verifyStatic(LivePerson.class,
                Mockito.description("TODO Phase 1: Initialize the LivePerson consumer profile/"
                        + "TODO Phase 1: Configure LivePerson with the consumer profile"));
        LivePerson.setUserProfile(argThat(argument ->
                applicationStorage.getFirstName().equals(argument.getFirstName())
                        && applicationStorage.getLastName().equals(argument.getLastName())
        ));
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO Phase 1: Show the specified conversation"));
        LivePerson.showConversation(eq(activity),
                any(LPAuthenticationParams.class),
                any(ConversationViewParams.class));
    }
}
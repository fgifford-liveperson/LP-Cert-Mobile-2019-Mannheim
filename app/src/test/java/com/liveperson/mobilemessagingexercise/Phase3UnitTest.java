package com.liveperson.mobilemessagingexercise;

import android.app.Activity;
import android.content.Context;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.MonitoringInitParams;
import com.liveperson.infra.auth.LPAuthenticationType;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.Conversations.MyAccountConversation;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({
        InitLivePersonProperties.class,
        LivePerson.class,
        LocalBroadcastManager.class,
        MyAccountConversation.class,
})
@RunWith(PowerMockRunner.class)
public class Phase3UnitTest {
    @Test
    public final void testMyAccountConversationRun() throws Exception {
        final InitLivePersonProperties initLivePersonProperties =
                Mockito.mock(InitLivePersonProperties.class);
        PowerMockito.mockStatic(LivePerson.class);
        PowerMockito.whenNew(InitLivePersonProperties.class).withAnyArguments()
                .thenReturn(initLivePersonProperties);
        new MyAccountConversation(Mockito.mock(Activity.class), null).run();
        PowerMockito.verifyNew(InitLivePersonProperties.class, Mockito.description(
                "TODO Phase 3: Set up the properties needed by LivePerson initialization"))
                .withArguments(
                        eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER),
                        eq(ApplicationConstants.LIVE_PERSON_APP_ID),
                        argThat(argument -> argument == null ||
                                argument instanceof MonitoringInitParams),
                        any(MyAccountConversation.class));
        PowerMockito.verifyStatic(LivePerson.class,
                Mockito.description("TODO Phase 3: Implement initialization of LivePerson"));
        LivePerson.initialize(any(Context.class), eq(initLivePersonProperties));
    }

    @Test
    public final void testMyAccountConversationOnInitSucceed() {
        final Activity activity = Mockito.mock(Activity.class);
        Mockito.when(activity.getApplication())
                .thenReturn(Mockito.mock(MobileMessagingExerciseApplication.class));
        final ApplicationStorage applicationStorage = ApplicationStorage.getInstance();
        PowerMockito.mockStatic(LivePerson.class);
        new MyAccountConversation(activity, applicationStorage).onInitSucceed();
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO Phase 3: Show the specified conversation"));
        LivePerson.showConversation(eq(activity),
                argThat(argument -> {
                    Assert.assertNotNull(
                            "TODO Phase 3: Set up the authentication parameters for an authenticated conversation",
                            argument);
                    Assert.assertEquals(
                            "TODO Phase 3: Set up the authentication parameters for an authenticated conversation",
                            LPAuthenticationType.AUTH,
                            argument.getAuthType());
                    return true;
                }),
                any(ConversationViewParams.class));
    }
}
package com.liveperson.mobilemessagingexercise;

import android.app.Activity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.LPAuthenticationParams;
import com.liveperson.infra.MonitoringInitParams;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.Conversations.AskUsConversation;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;
import com.liveperson.monitoring.model.EngagementDetails;
import com.liveperson.monitoring.model.LPMonitoringIdentity;
import com.liveperson.monitoring.sdk.MonitoringParams;
import com.liveperson.monitoring.sdk.api.LivepersonMonitoring;
import com.liveperson.monitoring.sdk.callbacks.EngagementCallback;
import com.liveperson.monitoring.sdk.responses.LPEngagementResponse;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({
        AskUsConversation.class,
        FirebaseInstanceId.class,
        LivePerson.class,
        LivepersonMonitoring.class,
})
@RunWith(PowerMockRunner.class)
public class Phase5UnitTest {
    @Mock
    public Activity activity;

    @Mock
    public FirebaseInstanceId firebaseInstanceId;

    @Mock
    public Task<InstanceIdResult> instanceIdResultTask;

    @Mock
    public LPEngagementResponse lpEngagementResponse;

    @Before
    public final void setUp() {
        Mockito.when(instanceIdResultTask.getResult())
                .thenReturn(Mockito.mock(InstanceIdResult.class));
        Mockito.when(instanceIdResultTask.isSuccessful()).thenReturn(true);
        Mockito.when(firebaseInstanceId.getInstanceId()).thenReturn(instanceIdResultTask);
        Mockito.when(activity.getApplication())
                .thenReturn(Mockito.mock(MobileMessagingExerciseApplication.class));
        Mockito.doAnswer(invocation -> {
                    ((Runnable) invocation.getArgument(0)).run();
                    return null;
                }
        ).when(activity).runOnUiThread(any(Runnable.class));
        Mockito.when(lpEngagementResponse.getEngagementDetailsList()).thenReturn(
                Collections.singletonList(new EngagementDetails(
                        "1111",
                        "2222",
                        "engagementRevision",
                        "contextId",
                        "conversationId",
                        "status",
                        "connectorId")));
        Mockito.when(lpEngagementResponse.getSessionId()).thenReturn("sessionId");
        Mockito.when(lpEngagementResponse.getVisitorId()).thenReturn("visitorId");
    }

    @Test
    public final void testApplicationConstants() {
        Assert.assertNotEquals(
                "TODO C4M 1 - Replace with your app's LiveEngage App Installation ID",
                "",
                ApplicationConstants.LIVE_PERSON_APP_INSTALLATION_ID);
    }

    @Test
    public final void testAskUsConversationRun() throws Exception {
        final InitLivePersonProperties initLivePersonProperties =
                Mockito.mock(InitLivePersonProperties.class);
        PowerMockito.whenNew(InitLivePersonProperties.class).withAnyArguments()
                .thenReturn(initLivePersonProperties);
        final MonitoringInitParams monitoringInitParams = Mockito.mock(MonitoringInitParams.class);
        PowerMockito.whenNew(MonitoringInitParams.class).withAnyArguments()
                .thenReturn(monitoringInitParams);
        PowerMockito.mockStatic(LivePerson.class);
        final AskUsConversation askUsConversation = new AskUsConversation(activity, null);
        askUsConversation.run();
        PowerMockito.verifyNew(MonitoringInitParams.class, Mockito.description(
                "TODO C4M unauth 2 Create MonitoringInitParams"
        )).withArguments(eq(ApplicationConstants.LIVE_PERSON_APP_INSTALLATION_ID));
        PowerMockito.verifyNew(InitLivePersonProperties.class, Mockito.description(
                "TODO C4M unauth 3 Add MonitoringInitParams to InitLivePersonProperties"
        )).withArguments(
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER),
                eq(ApplicationConstants.LIVE_PERSON_APP_ID),
                eq(monitoringInitParams),
                eq(askUsConversation)
        );
    }

    @Test
    public final void testAskUsConversationOnInitSucceed() throws Exception {
        final ApplicationStorage applicationStorage = ApplicationStorage.getInstance();
        PowerMockito.mockStatic(FirebaseInstanceId.class);
        Mockito.when(FirebaseInstanceId.getInstance()).thenReturn(firebaseInstanceId);
        PowerMockito.mockStatic(LivePerson.class);
        PowerMockito.mockStatic(LivepersonMonitoring.class);
        PowerMockito.whenNew(LPMonitoringIdentity.class).withAnyArguments()
                .thenReturn(Mockito.mock(LPMonitoringIdentity.class));
        final MonitoringParams monitoringParams = Mockito.mock(MonitoringParams.class);
        PowerMockito.whenNew(MonitoringParams.class).withAnyArguments()
                .thenReturn(monitoringParams);
        final ArgumentCaptor<ConversationViewParams> conversationViewParams =
                ArgumentCaptor.forClass(ConversationViewParams.class);
        PowerMockito.doAnswer(invocation -> null).when(LivePerson.class);
        LivePerson.showConversation(eq(activity), any(LPAuthenticationParams.class),
                conversationViewParams.capture());
        final ArgumentCaptor<EngagementCallback> engagementCallback =
                ArgumentCaptor.forClass(EngagementCallback.class);
        PowerMockito.doAnswer(invocation -> {
            engagementCallback.getValue().onSuccess(lpEngagementResponse);
            return null;
        }).when(LivepersonMonitoring.class);
        LivepersonMonitoring.getEngagement(
                eq(activity),
                anyList(),
                eq(monitoringParams),
                engagementCallback.capture()
        );
        new AskUsConversation(activity, applicationStorage).onInitSucceed();
        PowerMockito.verifyNew(LPMonitoringIdentity.class, Mockito.description(
                "TODO C4M unauth 4  Creating Identities array and LPMonitoringIdentity"
        )).withNoArguments();
        PowerMockito.verifyNew(MonitoringParams.class, Mockito.description(
                "TODO C4M unauth 5 Create Monitoring Params, Engagement Attributes and Entry Points"
        )).withArguments(
                anyString(),
                any(JSONArray.class),
                any(JSONArray.class)
        );
        PowerMockito.verifyStatic(LivepersonMonitoring.class, Mockito.description(
                "TODO C4M unauth 6 Invoke getEngagement"
        ));
        LivepersonMonitoring.getEngagement(
                eq(activity),
                anyList(),
                eq(monitoringParams),
                eq(engagementCallback.getValue())
        );
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO  C4M unauth 9 set CampaignInfo Object in conversationViewParam"
        ));
        LivePerson.showConversation(
                eq(activity),
                any(LPAuthenticationParams.class),
                eq(conversationViewParams.getValue())
        );
        final CampaignInfo actualCampaignInfo = conversationViewParams.getValue().getCampaignInfo();
        Assert.assertNotNull(
                "TODO  C4M unauth 8 set CampaignInfo Object in conversationViewParam",
                actualCampaignInfo
        );
        final Map<String, String> expectedCampaignDetails = new HashMap<>();
        expectedCampaignDetails.put("1111", actualCampaignInfo.getCampaignId().toString());
        expectedCampaignDetails.put("2222", actualCampaignInfo.getEngagementId().toString());
        expectedCampaignDetails.put("contextId", actualCampaignInfo.getContextId());
        expectedCampaignDetails.put("sessionId", actualCampaignInfo.getSessionId());
        expectedCampaignDetails.put("visitorId", actualCampaignInfo.getVisitorId());
        for (final String key : expectedCampaignDetails.keySet()) {
            Assert.assertEquals(
                    "TODO C4M unauth 7 Construct CampaignInfo Object",
                    key,
                    expectedCampaignDetails.get(key)
            );
        }
    }
}

package com.liveperson.mobilemessagingexercise;

import com.liveperson.infra.CampaignInfo;
import com.liveperson.infra.ConversationViewParams;
import com.liveperson.infra.InitLivePersonProperties;
import com.liveperson.infra.MonitoringInitParams;
import com.liveperson.infra.auth.LPAuthenticationParams;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.Conversations.MyAccountFragmentConversation;
import com.liveperson.mobilemessagingexercise.Fragments.MyAccountFragment;
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
        LivePerson.class,
        LivepersonMonitoring.class,
        MyAccountFragmentConversation.class,
})
@RunWith(PowerMockRunner.class)
public class Phase6UnitTest {

    @Mock
    public MobileMessagingExerciseApplication mobileMessagingExerciseApplication;

    @Mock
    public MyAccountFragment myAccountFragment;

    @Mock
    public LPEngagementResponse lpEngagementResponse;

    @Before
    public final void setUp() {
        Mockito.when(myAccountFragment.getApplication())
                .thenReturn(mobileMessagingExerciseApplication);
        Mockito.when(myAccountFragment.getBaseContext())
                .thenReturn(mobileMessagingExerciseApplication);
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
    public final void testMyAccountFragmentConversationRun() throws Exception {
        final InitLivePersonProperties initLivePersonProperties =
                Mockito.mock(InitLivePersonProperties.class);
        PowerMockito.whenNew(InitLivePersonProperties.class).withAnyArguments()
                .thenReturn(initLivePersonProperties);
        final MonitoringInitParams monitoringInitParams = Mockito.mock(MonitoringInitParams.class);
        PowerMockito.whenNew(MonitoringInitParams.class).withAnyArguments()
                .thenReturn(monitoringInitParams);
        PowerMockito.mockStatic(LivePerson.class);
        final MyAccountFragmentConversation myAccountFragmentConversation =
                new MyAccountFragmentConversation(myAccountFragment, null);
        myAccountFragmentConversation.run();
        PowerMockito.verifyNew(MonitoringInitParams.class, Mockito.description(
                "TODO C4M unauth 2 Create MonitoringInitParams"
        )).withArguments(eq(ApplicationConstants.LIVE_PERSON_APP_INSTALLATION_ID));
        PowerMockito.verifyNew(InitLivePersonProperties.class, Mockito.description(
                "TODO C4M unauth 3 Add MonitoringInitParams to InitLivePersonProperties"
        )).withArguments(
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER),
                eq(ApplicationConstants.LIVE_PERSON_APP_ID),
                eq(monitoringInitParams),
                eq(myAccountFragmentConversation)
        );
    }

    @Test
    public final void testAskUsConversationOnInitSucceed() throws Exception {
        final ApplicationStorage applicationStorage = ApplicationStorage.getInstance();
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
        LivePerson.getConversationFragment(any(LPAuthenticationParams.class),
                conversationViewParams.capture());
        final ArgumentCaptor<EngagementCallback> engagementCallback =
                ArgumentCaptor.forClass(EngagementCallback.class);
        PowerMockito.doAnswer(invocation -> {
            engagementCallback.getValue().onSuccess(lpEngagementResponse);
            return null;
        }).when(LivepersonMonitoring.class);
        LivepersonMonitoring.getEngagement(
                eq(mobileMessagingExerciseApplication),
                anyList(),
                eq(monitoringParams),
                engagementCallback.capture()
        );
        new MyAccountFragmentConversation(myAccountFragment, applicationStorage).onInitSucceed();
        PowerMockito.verifyNew(LPMonitoringIdentity.class, Mockito.description(
                "TODO C4M auth 4 Create identities array and LPMonitoringIdentity"
        )).withArguments(anyString(), anyString());
        PowerMockito.verifyNew(MonitoringParams.class, Mockito.description(
                "TODO C4M auth 5 Create Monitoring Params, Engagement Attributes and Entry Points"
        )).withArguments(
                anyString(),
                any(JSONArray.class),
                any(JSONArray.class)
        );
        PowerMockito.verifyStatic(LivepersonMonitoring.class, Mockito.description(
                "TODO C4M auth 6 Invoke getEngagement"
        ));
        LivepersonMonitoring.getEngagement(
                eq(mobileMessagingExerciseApplication),
                anyList(),
                eq(monitoringParams),
                eq(engagementCallback.getValue())
        );
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO  C4M auth 9 set CampaignInfo Object in conversationViewParam"
        ));
        LivePerson.getConversationFragment(
                any(LPAuthenticationParams.class),
                eq(conversationViewParams.getValue())
        );
        final CampaignInfo actualCampaignInfo = conversationViewParams.getValue().getCampaignInfo();
        Assert.assertNotNull(
                "TODO  C4M auth 8 set CampaignInfo Object in conversationViewParam",
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
                    "TODO C4M auth 7 Construct CampaignInfo Object",
                    key,
                    expectedCampaignDetails.get(key)
            );
        }
    }
}

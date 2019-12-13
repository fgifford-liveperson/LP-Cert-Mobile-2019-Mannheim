package com.liveperson.mobilemessagingexercise;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.RemoteMessage;
import com.liveperson.infra.ICallback;
import com.liveperson.infra.messaging_ui.fragment.ConversationFragment;
import com.liveperson.infra.model.PushMessage;
import com.liveperson.messaging.sdk.api.LivePerson;
import com.liveperson.mobilemessagingexercise.Conversations.MyAccountFragmentConversation;
import com.liveperson.mobilemessagingexercise.Fragments.MyAccountFragment;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;
import com.liveperson.mobilemessagingexercise.services.LpFirebaseMessagingService;

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
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({
        FirebaseInstanceId.class,
        LivePerson.class,
        LpFirebaseMessagingService.class,
})
@RunWith(PowerMockRunner.class)
public class Phase4UnitTest {

    @Mock
    public Notification.Builder builder;

    @Mock
    public FirebaseInstanceId firebaseInstanceId;

    @Mock
    public FragmentManager fragmentManager;

    @Mock
    public FragmentTransaction fragmentTransaction;

    @Mock
    public Notification.InboxStyle inboxStyle;

    @Mock
    public Task<InstanceIdResult> instanceIdResultTask;

    private final Map<String, String> messageData = Collections.singletonMap("key", "value");

    @Mock
    public RemoteMessage remoteMessage;

    @Before
    public final void setUp() {
        Mockito.when(instanceIdResultTask.getResult())
                .thenReturn(Mockito.mock(InstanceIdResult.class));
        Mockito.when(instanceIdResultTask.isSuccessful()).thenReturn(true);
        Mockito.when(firebaseInstanceId.getInstanceId()).thenReturn(instanceIdResultTask);
        Mockito.when(fragmentTransaction.add(
                anyInt(),
                any(ConversationFragment.class),
                anyString()))
                .thenReturn(fragmentTransaction);
        Mockito.when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        Mockito.when(remoteMessage.getData()).thenReturn(messageData);
        Mockito.when(builder.setAutoCancel(anyBoolean())).thenReturn(builder);
        Mockito.when(builder.setContentIntent(eq(null))).thenReturn(builder);
        Mockito.when(builder.setContentIntent(any(PendingIntent.class))).thenReturn(builder);
        Mockito.when(builder.setContentTitle(any(CharSequence.class))).thenReturn(builder);
        Mockito.when(builder.setDefaults(anyInt())).thenReturn(builder);
        Mockito.when(builder.setNumber(anyInt())).thenReturn(builder);
        Mockito.when(builder.setPriority(anyInt())).thenReturn(builder);
        Mockito.when(builder.setSmallIcon(anyInt())).thenReturn(builder);
        Mockito.when(builder.setStyle(any(Notification.Style.class))).thenReturn(builder);
        Mockito.when(inboxStyle.addLine(eq(null))).thenReturn(inboxStyle);
        Mockito.when(inboxStyle.addLine(any(CharSequence.class))).thenReturn(inboxStyle);
    }

    @Test
    public final void testMyAccountFragmentConversationOnComplete() {
        PowerMockito.mockStatic(LivePerson.class);
        final MyAccountFragmentConversation myAccountFragmentConversation =
                new MyAccountFragmentConversation(Mockito.mock(MyAccountFragment.class),
                        ApplicationStorage.getInstance());
        myAccountFragmentConversation.onComplete(instanceIdResultTask);
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO Phase 4: Register to receive push messages with the new firebase token"));
        LivePerson.registerLPPusher(
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER),
                eq(ApplicationConstants.LIVE_PERSON_APP_ID),
                eq(null),
                eq(null),
                eq(myAccountFragmentConversation));
    }

    @Test
    public final void testLpFirebaseMessagingServiceOnMessageReceived() {
        final LpFirebaseMessagingService lpFirebaseMessagingService =
                new LpFirebaseMessagingService();
        PowerMockito.mockStatic(LivePerson.class);
        Mockito.when(LivePerson.handlePushMessage(eq(lpFirebaseMessagingService), eq(messageData),
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER), anyBoolean()))
                .thenReturn(Mockito.mock(PushMessage.class));
        lpFirebaseMessagingService.onMessageReceived(remoteMessage);
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO Phase 4: Retrieve the LivePerson PushMessage instance from the Firebase message"));
        LivePerson.handlePushMessage(eq(lpFirebaseMessagingService), eq(messageData),
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER), anyBoolean());
        PowerMockito.verifyStatic(LivePerson.class, Mockito.description(
                "TODO Phase 4: Get the count of unread messages"));
        LivePerson.getNumUnreadMessages(eq(ApplicationConstants.LIVE_PERSON_APP_ID), eq(null));
    }

    @Test
    public final void testLpFirebaseMessagingServiceUnreadMessagesHandlerOnSuccess()
            throws Exception {
        final String message = "TODO Phase 4 Add a line containing the message text from the agent";
        final PushMessage pushMessage = Mockito.mock(PushMessage.class);
        Mockito.when(pushMessage.getMessage()).thenReturn(message);
        final LpFirebaseMessagingService lpFirebaseMessagingService =
                Mockito.spy(LpFirebaseMessagingService.class);
        Mockito.when(lpFirebaseMessagingService.getString(anyInt())).thenReturn("");
        Mockito.when(lpFirebaseMessagingService.getSystemService(eq(Context.NOTIFICATION_SERVICE)))
                .thenReturn(Mockito.mock(NotificationManager.class));
        PowerMockito.mockStatic(LivePerson.class);
        Mockito.when(LivePerson.handlePushMessage(eq(lpFirebaseMessagingService), eq(messageData),
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER), anyBoolean()))
                .thenReturn(pushMessage);
        final ArgumentCaptor<ICallback<Integer, Exception>> unreadMessagesHandler =
                ArgumentCaptor.forClass(ICallback.class);
        PowerMockito.doAnswer(invocation -> {
            unreadMessagesHandler.getValue().onSuccess(1);
            return null;
        }).when(LivePerson.class);
        LivePerson.getNumUnreadMessages(anyString(), unreadMessagesHandler.capture());
        PowerMockito.whenNew(Notification.Builder.class).withAnyArguments().thenReturn(builder);
        PowerMockito.whenNew(Notification.InboxStyle.class).withAnyArguments().thenReturn(inboxStyle);
        lpFirebaseMessagingService.onCreate();
        lpFirebaseMessagingService.onMessageReceived(remoteMessage);
        Mockito.verify(inboxStyle, Mockito.description(
                "TODO Phase 4 Add a line containing the message text from the agent"))
                .addLine(eq(message));
    }

    @Test
    public final void testLpFirebaseMessagingServiceUnreadMessagesHandlerCreatePendingIntent()
            throws Exception {
        final LpFirebaseMessagingService lpFirebaseMessagingService =
                Mockito.spy(LpFirebaseMessagingService.class);
        Mockito.when(lpFirebaseMessagingService.getString(anyInt())).thenReturn("");
        Mockito.when(lpFirebaseMessagingService.getSystemService(eq(Context.NOTIFICATION_SERVICE)))
                .thenReturn(Mockito.mock(NotificationManager.class));
        PowerMockito.mockStatic(LivePerson.class);
        Mockito.when(LivePerson.handlePushMessage(eq(lpFirebaseMessagingService), eq(messageData),
                eq(ApplicationConstants.LIVE_PERSON_ACCOUNT_NUMBER), anyBoolean()))
                .thenReturn(Mockito.mock(PushMessage.class));
        final ArgumentCaptor<ICallback<Integer, Exception>> unreadMessagesHandler =
                ArgumentCaptor.forClass(ICallback.class);
        PowerMockito.doAnswer(invocation -> {
            unreadMessagesHandler.getValue().onSuccess(1);
            return null;
        }).when(LivePerson.class);
        LivePerson.getNumUnreadMessages(anyString(), unreadMessagesHandler.capture());
        PowerMockito.whenNew(Notification.Builder.class).withAnyArguments().thenReturn(builder);
        PowerMockito.whenNew(Notification.InboxStyle.class).withAnyArguments()
                .thenReturn(inboxStyle);
        final Intent intent = Mockito.mock(Intent.class);
        PowerMockito.whenNew(Intent.class).withAnyArguments().thenReturn(intent);
        lpFirebaseMessagingService.onCreate();
        lpFirebaseMessagingService.onMessageReceived(remoteMessage);
        PowerMockito.verifyNew(Intent.class, Mockito.description(
                "TODO Phase 4 Create the Intent to start the Welcome Activity"
        )).withArguments(eq(lpFirebaseMessagingService), eq(WelcomeActivity.class));
        Mockito.verify(intent, Mockito.description(
                "TODO Phase 4 Add the indication that this is from a LivePerson push message"
        )).putExtra(eq(ApplicationConstants.LP_IS_FROM_PUSH), eq(true));
    }
}

package com.liveperson.mobilemessagingexercise;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.liveperson.mobilemessagingexercise.model.ApplicationConstants;
import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;

@PrepareForTest({
        LoginActivity.class,
        Volley.class,
})
@RunWith(PowerMockRunner.class)
public class Phase2UnitTest {
    @Test
    public final void testLoginActivityLogUserIn() {
        final View view = Mockito.mock(View.class);
        Mockito.when(view.getId()).thenReturn(R.id.loginButton);
        final LoginActivity loginActivity = Mockito.mock(LoginActivity.class);
        Mockito.doCallRealMethod().when(loginActivity).onClick(any(View.class));
        Mockito.when(loginActivity.getBrandServerBaseUrl()).thenCallRealMethod();
        Mockito.when(loginActivity.findViewById(anyInt())).thenAnswer(invocation -> {
            final Editable editable = Mockito.mock(Editable.class);
            Mockito.when(editable.toString()).thenReturn(invocation.getArgument(0).toString());
            final EditText result = Mockito.mock(EditText.class);
            Mockito.when(result.getText()).thenReturn(editable);
            return result;
        });
        final RequestQueue requestQueue = Mockito.mock(RequestQueue.class);
        PowerMockito.mockStatic(Volley.class);
        Mockito.when(Volley.newRequestQueue(eq(loginActivity))).thenReturn(requestQueue);
        loginActivity.onClick(view);
        Mockito.verify(requestQueue, Mockito.description(
                "TODO Phase 2: Send the HTTP request to log the user in to the Brand Server"
        )).add(argThat(argument -> {
                    boolean result;
                    try {
                        final JSONObject body = new JSONObject(new String(argument.getBody()));
                        Assert.assertEquals(
                                "TODO Phase 2: Set up the body of the HTTP request to log the user in to the Brand Server",
                                Integer.toString(R.id.userId),
                                body.optString("userId"));
                        Assert.assertEquals(
                                "TODO Phase 2: Set up the body of the HTTP request to log the user in to the Brand Server",
                                Integer.toString(R.id.password),
                                body.optString("password"));
                        Assert.assertEquals(
                                "TODO Phase 2: Create the HTTP request to send to the Brand Server",
                                argument.getUrl(),
                                ApplicationConstants.BRAND_SERVER_URL + "/authenticate"
                        );
                        result = Integer.toString(R.id.userId).equals(body.optString("userId"))
                                && Integer.toString(R.id.password).equals(body.optString("password"))
                                && argument.getUrl().startsWith(ApplicationConstants.BRAND_SERVER_URL)
                                && argument.getUrl().endsWith("/authenticate");
                    } catch (final AuthFailureError | JSONException e) {
                        Assert.fail(e.getMessage());
                        result = false;
                    }
                    return result;
                }
        ));
    }

    @Test
    public final void testLoginActivityOnResponse() throws Exception {
        final JSONObject response = new JSONObject();
        response.put("loginSuccessful", true);
        response.put("jwt", "jwt");
        final LoginActivity loginActivity = Mockito.spy(new LoginActivity());
        Mockito.when(loginActivity.getApplicationStorage())
                .thenReturn(ApplicationStorage.getInstance());
        Mockito.doNothing().when(loginActivity).showToast(anyString());
        Mockito.doNothing().when(loginActivity).startMyAccount();
        final ApplicationStorage applicationStorage = ApplicationStorage.getInstance();
        applicationStorage.setLoggedIn(false);
        applicationStorage.setJwt("");
        loginActivity.onResponse(response);
        Assert.assertTrue("TODO Phase 2: Save the results of the login request",
                applicationStorage.isLoggedIn());
        Assert.assertEquals("TODO Phase 2: Save the results of the login request",
                applicationStorage.getJwt(), "jwt");
    }
}

package com.liveperson.mobilemessagingexercise;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/********************************************************************************
 * Class for the activity associated with the application Login screen.
 * NOTE: This class also provides the listeners for click events for the controls
 * on the screen and for the response from the authentication process
 *******************************************************************************/
public class LoginActivity extends MobileMessagingExerciseActivity
        implements View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Android callback invoked as the activity is created
     * @param savedInstanceState any instance state data saved in a previous execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Set up the click listeners
        findViewById(R.id.loginButton).setOnClickListener(this);
    }

    /**
     * Android callback invoked as the options menu is created.
     * @param menu the options menu in the toolbar
     * @return true, if the menu is to be displayed, and false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Add the appropriate menu items to the toolbar menu
        getMenuInflater().inflate(R.menu.menu_ask_us, menu);
        //Ensure the menu is displayed
        return true;
    }

    /**
     * Android callback invoked as an option is selected from the options menu.
     * @param item the selected menu item
     * @return true if the menu item has been processed here, and false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //Process selection of the Welcome item
            case R.id.welcome:
                startWelcome();
                break;

            //Process selection of any other items
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Log the user in
     * @param userId the user id to be used for login
     * @param password the password to be used for login
     */
    private void logUserIn(String userId, String password) {
        RequestQueue authenticationQueue = Volley.newRequestQueue(this);
        JSONObject credentials = new JSONObject();
        try {
            //Set up the body for the POST request to the authentication API
            credentials.put("username", userId);
            credentials.put("password", password);
            //Create the authentication POST request
            JsonObjectRequest authenticationRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    getBrandServerBaseUrl() + "/authenticate",
                    credentials,
                    this,
                    this
            );
            //Send the authentication POST request
            authenticationQueue.add(authenticationRequest);
        }
        catch (Exception e) {
            Log.e(TAG, e.getMessage());
            showToast(e.getMessage());
        }
    }

    /**
     * Handle click events for controls on the Login screen.
     * @param view the control on which the event occurred
     */
    public void onClick(View view) {
        switch (view.getId()) {
            //Process clicks on the Login button
            case R.id.loginButton:
                EditText userIdControl = findViewById(R.id.userId);
                EditText passwordControl = findViewById(R.id.password);
                logUserIn(userIdControl.getText().toString(), passwordControl.getText().toString());
                break;
            default:
                break;
        }
    }

    /**
     * Process responses for login requests that completed normally. The response
     * indicates whether login was successful or not.
     * @param authenticationResponse the response to the request
     */
    @Override
    public void onResponse(JSONObject authenticationResponse) {
        try {
            //Save the results of the login attempt
            getApplicationStorage().setLoggedIn(authenticationResponse.getBoolean("loginSuccessful"));
            getApplicationStorage().setJwt(authenticationResponse.getString("jwt"));

            if (getApplicationStorage().isLoggedIn()) {
                showToast("Successfully logged in");
                startMyAccount();
            }
            else {
                showToast("Unable to log in");
                startWelcome();
            }
        }
        catch (Exception e) {
            //There was a problem parsing the response from the server
            Log.e(TAG, e.getMessage());
            showToast("Unable to log in");
        }
    }

    /**
     * Process responses from login requests that failed.
     * @param error the error information associated with the failure
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(TAG, "Call to login failed: " + error.getMessage());
        showToast("Unable to log in: " + error.getMessage());
        startWelcome();
    }

}

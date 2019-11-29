package com.liveperson.mobilemessagingexercise;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

/******************************************************************************
 * Class for the activity associated with the application Welcome screen.
 * NOTE: This class also provides the listener for click events on the screen
 *****************************************************************************/
public class WelcomeActivity extends MobileMessagingExerciseActivity implements View.OnClickListener {
    private static final String TAG = WelcomeActivity.class.getSimpleName();

    /**
     * Android callback invoked as the activity is created
     * @param savedInstanceState any instance state data saved in a previous execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Set up the click listeners
        findViewById(R.id.ask_us_button).setOnClickListener(this);
        findViewById(R.id.my_account_button).setOnClickListener(this);
        Log.i(TAG, "Welcome activity created");
    }

    /**
     * Android callback invoked as the activity is resumed
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Load saved data into the controls on this screen
        ApplicationStorage applicationStorage = getApplicationStorage();
        EditText firstNameControl = findViewById(R.id.firstName);
        firstNameControl.setText(applicationStorage.getFirstName());
        EditText lastNameControl = findViewById(R.id.lastName);
        lastNameControl.setText(applicationStorage.getLastName());
    }

    /**
     * Android callback invoked as the options menu is created.
     * @param menu the options menu in the toolbar
     * @returns true, if the menu is to be displayed, and false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Add the appropriate menu items to the toolbar menu
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            //Process selection of the My Account item
            case R.id.action_my_account:
                if (getApplicationStorage().isLoggedIn()) {
                    //User already logged in, so go straight there
                    startMyAccount();
                }
                else {
                    //Not logged in, so need to do that first
                    startLogin();
                }
                break;

            //Process selection of the Ask Us item
            case R.id.action_ask_us:
                startAskUs();
                break;

            //Process selection of any other items
            default:
                return super.onOptionsItemSelected(item);
        }

        //The selection has been processed here
        return true;
    }

    /**
     * Handle click events for controls on the Welcome screen.
     * @param view the control on which the event occurred
     */
    public void onClick(View view) {
        switch (view.getId()) {
        //Process clicks on the My Account button
        case R.id.my_account_button:
            if (getApplicationStorage().isLoggedIn()) {
                //User already logged in, so start the My Account screen
                startMyAccount();
            }
            else {
                //Use not logged in, so start the Login screen
                startLogin();
            }
            break;

        //Process clicks on the Ask Us button
        case R.id.ask_us_button:
            //Start the Ask Us screen
            startAskUs();
            break;
        }
    }

    /**
     * Start the Ask Us activity using any data entered on the Welcome screen
     */
    @Override
    protected void startAskUs() {
        //Capture any user input from the Welcome screen
        EditText firstNameControl = findViewById(R.id.firstName);
        EditText lastNameControl = findViewById(R.id.lastName);
        getApplicationStorage().setFirstName(firstNameControl.getText().toString());
        getApplicationStorage().setLastName(lastNameControl.getText().toString());

        //Start the Ask Us activity
        super.startAskUs();

    }
}

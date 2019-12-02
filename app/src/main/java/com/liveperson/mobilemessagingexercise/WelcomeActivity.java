package com.liveperson.mobilemessagingexercise;

import android.app.Activity;
import android.os.Bundle;


/******************************************************************************
 * Class for the activity associated with the application Welcome screen.
 * NOTE: This class also provides the listener for click events on the screen
 *****************************************************************************/
public class WelcomeActivity extends Activity {

    /**
     * Android callback invoked as the activity is created
     * @param savedInstanceState any instance state data saved in a previous execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }
}

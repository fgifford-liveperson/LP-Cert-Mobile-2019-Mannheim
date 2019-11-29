package com.liveperson.mobilemessagingexercise;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;


/******************************************************************************
 * Class for the activity associated with the application Welcome screen.
 * NOTE: This class also provides the listener for click events on the screen
 ****************************************************************************implemen*/
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

    /**
     * Android callback invoked as the options menu is created.
     * @param menu the options menu in the toolbar
     * @return true, if the menu is to be displayed, and false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Add the appropriate menu items to the toolbar menu
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        //Ensure the menu is displayed
        return true;
    }
}

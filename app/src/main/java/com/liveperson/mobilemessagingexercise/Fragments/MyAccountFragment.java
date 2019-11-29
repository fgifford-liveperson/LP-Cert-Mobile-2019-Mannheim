package com.liveperson.mobilemessagingexercise.Fragments;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.liveperson.mobilemessagingexercise.Conversations.MyAccountFragmentConversation;
import com.liveperson.mobilemessagingexercise.MobileMessagingExerciseActivity;
import com.liveperson.mobilemessagingexercise.R;

/**********************************************************************************************
 * Class to initiate display of the My Account Screen using the LivePerson Fragment mechanism.
 *********************************************************************************************/
public class MyAccountFragment extends MobileMessagingExerciseActivity {
    private static final String TAG = MyAccountFragment.class.getSimpleName();

    private MyAccountFragmentConversation myAccountFragmentConversation;

    /**
     * Android callback invoked as the activity is created
     * @param savedInstanceState any instance state data saved in a previous execution
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_acount);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        //Run the fragment conversation, clearing any existing conversation first
        myAccountFragmentConversation = new MyAccountFragmentConversation(this, getApplicationStorage());
        getClearRunner().clearAndRun(myAccountFragmentConversation);
    }

    /**
     * Android callback invoked as the options menu is created.
     * @param menu the options menu in the toolbar
     * @return true, if the menu is to be displayed, and false otherwise
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Add the appropriate menu items to the toolbar menu
        getMenuInflater().inflate(R.menu.menu_my_account, menu);
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
}


package com.liveperson.mobilemessagingexercise;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.liveperson.mobilemessagingexercise.model.ApplicationStorage;

public class AskUsActivity extends AppCompatActivity {

    ApplicationStorage applicationStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * Access the shared data for the application
         */
        applicationStorage = ApplicationStorage.getInstance();

        /* TODO - Replace with your LiveEngage Account Number */
        applicationStorage.setBrandAccountNumber("20553802");
        /* TODO - Replace with the brand's JWT public key */
        applicationStorage.setJwtPublicKey("kldjflkdjlakjd;lkjd");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ask_us) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.liveperson.mobilemessagingexercise;


import android.app.Activity;
import android.widget.TextView;

import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

/**
 * Test {@link WelcomeActivity}.
 */
public class WelcomeActivityInstrumentedTest {
    @Rule
    public final ActivityTestRule<WelcomeActivity> activityTestRule =
            new ActivityTestRule(WelcomeActivity.class);

    @MediumTest
    @Test
    public void testActivity() {
        final Activity activity = activityTestRule.getActivity();
        onView(withId(R.id.textView));
        Assert.assertEquals(activity.getString(R.string.welcome_message),
                ((TextView) activity.findViewById(R.id.textView)).getText().toString());
    }

}
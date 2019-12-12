package com.liveperson.mobilemessagingexercise;


import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressMenuKey;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Test {@link WelcomeActivity}.
 */
public class WelcomeActivityInstrumentedTest {
    @Rule
    public final ActivityTestRule<WelcomeActivity> activityTestRule =
            new ActivityTestRule(WelcomeActivity.class);

    /** Test that activity is displayed correctly. */
    @MediumTest
    @Test
    public void testActivity() {
        onView(withId(R.id.textView1)).check(matches(withText(R.string.welcome_intro1)));
    }

    /** Test that menu is displayed correctly. */
    @MediumTest
    @Test
    public void testMenu() {
        onView(withId(R.id.textView1)).perform(pressMenuKey());
        onView(withText(R.string.ask_us)).check(matches(isDisplayed()));
    }

    /** Test that menu is displayed correctly. */
    @MediumTest
    @Test
    public void testMyAccount() {
        onView(withId(R.id.my_account_button)).perform(click());
        onView(withText(R.string.login_intro)).check(matches(isDisplayed()));
    }

}
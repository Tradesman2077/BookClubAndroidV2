package com.example.book_club;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class TextViewTitleTest {
    @RunWith(AndroidJUnit4.class)
    @LargeTest
    public class CheckTextView {
        @Rule
        public ActivityScenarioRule<MainActivity> activityRule =
                new ActivityScenarioRule<>(MainActivity.class);
        @Test
        public void textViewTest() {
            onView(withText("Comp-Sci Book Club")).check(matches(isDisplayed()));

        }
    }

}

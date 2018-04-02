package com.example.android.bakingrecipes;


import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/*
* Espresso test class testing the MainActivity UI
* */

@RunWith(AndroidJUnit4.class)
public class MainActivityEspressoTest {

    @Rule public ActivityTestRule<MainActivity> mActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;


    @Before
    public void registerIdlingResource(){
        mIdlingResource = mActivityTestRule.getActivity().returnIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void recyclerViewDisplaysRecipesTest(){

        onView(ViewMatchers.withId(R.id.recipe_recyclerView)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withText("Nutella Pie")).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.recipe_recyclerView)).perform(RecyclerViewActions.scrollToPosition(1));
        onView(withText("Brownies")).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.recipe_recyclerView)).perform(RecyclerViewActions.scrollToPosition(2));
        onView(withText("Yellow Cake")).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.recipe_recyclerView)).perform(RecyclerViewActions.scrollToPosition(3));
        onView(withText("Cheesecake")).check(matches(isDisplayed()));

    }


    @After
    public void unregisterIdlingResource(){
        if(mIdlingResource!=null){
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}

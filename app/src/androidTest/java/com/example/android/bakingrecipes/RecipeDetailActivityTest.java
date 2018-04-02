package com.example.android.bakingrecipes;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;

@RunWith(AndroidJUnit4.class)
public class RecipeDetailActivityTest {


    @Before
    public void stubAllExternalIntents(){
    }

    @Test
    public void testFirstItemInRecipeStepsIsIntroduction(){
        onData(anything()).inAdapterView(withId(R.id.steps_recyclerView))
                .atPosition(1).perform(click());

    }
}

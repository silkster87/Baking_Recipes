package com.example.android.bakingrecipes;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class MainActivityIntentsTest {

    public static final String RECIPE_BUNDLE = "recipeDetails";

    @Rule public IntentsTestRule<MainActivity> mIntentsTestRule
            = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void recipeItemLaunchesIntent(){

        onView(withId(R.id.recipe_recyclerView)).perform(RecyclerViewActions.scrollToPosition(0));
        onView(withId(R.id.recipe_recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        intended(hasComponent(RecipeDetailActivity.class.getName()));
        intended(hasExtraWithKey(RECIPE_BUNDLE));

    }
}

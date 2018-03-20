package com.example.android.bakingrecipes.UI;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**This video fragment will be used in the master detail flow layout for tablets in landscape mode.
 * The video will display the recipe step and will be replaced by a new one when the user clicks on a
 * different step.
 * Created by Silky on 20/03/2018.
 */

public class VideoFragment extends Fragment {

    public VideoFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}

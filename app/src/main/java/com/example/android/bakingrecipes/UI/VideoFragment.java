package com.example.android.bakingrecipes.UI;

import android.support.v4.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devbrackets.android.exomedia.ui.widget.VideoControls;
import com.example.android.bakingrecipes.R;

/**This video fragment will be used in the master detail flow layout for tablets in landscape mode.
 * The video will display the recipe step and will be replaced by a new one when the user clicks on a
 * different step.
 * Created by Silky on 20/03/2018.
 */

public class VideoFragment extends Fragment {

    private String videoURL;
    private com.devbrackets.android.exomedia.ui.widget.VideoView videoView;
    public static final String VIDEO_URL = "video_url";
    private static final String VIDEO_POSITION = "video_position";


    public VideoFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.video_fragment_layout, container, false);
        long currentPosition = 0;

        if(savedInstanceState != null){
            videoURL = savedInstanceState.getString(VIDEO_URL);
            currentPosition = savedInstanceState.getLong(VIDEO_POSITION);
        }
        videoView = rootView.findViewById(R.id.video_view_fragment);
        videoView.setVideoURI(Uri.parse(videoURL));

        VideoControls controls = videoView.getVideoControls();
        controls.setRewindButtonEnabled(true);

        videoView.seekTo(currentPosition);
        videoView.start();
        return rootView;
    }

    public void setUpVideo(String videoURL){
        this.videoURL=videoURL;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
            videoView.stopPlayback();
            videoView.release();

    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putString(VIDEO_URL, videoURL);
        long videoCurrentPosition = videoView.getCurrentPosition();

        currentState.putLong(VIDEO_POSITION, videoCurrentPosition);
    }
}

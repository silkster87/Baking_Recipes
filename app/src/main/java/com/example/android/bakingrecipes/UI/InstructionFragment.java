package com.example.android.bakingrecipes.UI;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingrecipes.R;


/**This is the instruction fragment to be used in the master detail flow. When a user clicks on a
 * step the instruction fragment will be replaced by a corresponding instruction to the step.
 * Created by Silky on 22/03/2018.
 */

public class InstructionFragment extends Fragment {

    public static final String INSTRUCTION_STRING = "instruction_string";
    private String instructionText;

    public InstructionFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(savedInstanceState != null){
            instructionText = savedInstanceState.getString(INSTRUCTION_STRING);
        }

        View rootView = inflater.inflate(R.layout.instruction_fragment_layout, container, false);
        final TextView textView = rootView.findViewById(R.id.instruction_frag_textView);
        textView.setText(instructionText);
        return rootView;
    }

    public void setInstructionText(String instructionText){
        this.instructionText = instructionText;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle currentState) {
        currentState.putString(INSTRUCTION_STRING, instructionText);
    }
}

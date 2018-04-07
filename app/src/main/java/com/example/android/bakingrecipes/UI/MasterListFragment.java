package com.example.android.bakingrecipes.UI;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingrecipes.R;
import com.example.android.bakingrecipes.RecipeObjects.Step;

import java.util.List;

/** This master list fragment will display a recycler view of steps for the selected recipe.
 * Each step will have a click listener that will update the RHS of the 2 pane UI to show the
 * video and the instruction of the step.
 * Created by Silky on 18/03/2018.
 */

public class MasterListFragment extends Fragment {

    OnStepItemSelectedListener mCallback;
    private List<Step> listOfSteps;
    private MasterListAdapter mAdapter;
    private int stepToHighlight;


    public MasterListFragment(){
    }


    public void setListOfSteps(List<Step> listOfSteps){
        this.listOfSteps=listOfSteps;
    }

    public void setStepToBeHighlighted(int stepNumber) {
        this.stepToHighlight=stepNumber;
    }

    public interface OnStepItemSelectedListener {
        void onStepItemSelected(Step step);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //Ensure that the container activity has implemented
        //callback interface. If not, throw an exception.
        try{
            mCallback = (OnStepItemSelectedListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString()
            + " must implement OnStepItemSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.recipes_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setFocusable(false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MasterListAdapter(listOfSteps, new MasterListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Step step) {
                mCallback.onStepItemSelected(step);
            }
        });
        mAdapter.setStepToHighlight(stepToHighlight);

        mRecyclerView.setAdapter(mAdapter);
        return rootView;
    }

}
